package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.dto.UserDetailsResponse;
import com.englishflow.auth.dto.UserIdsRequest;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    @PutMapping("/{id}")
    public ResponseEntity<AuthResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields if provided
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getProfilePhoto() != null) {
            user.setProfilePhoto(request.getProfilePhoto());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getPostalCode() != null) {
            user.setPostalCode(request.getPostalCode());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user = userRepository.save(user);

        return ResponseEntity.ok(new AuthResponse(
                null, // No new token
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return ResponseEntity.ok(UserDetailsResponse.fromEntity(user));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<UserDetailsResponse>> getUsersByIds(@RequestBody UserIdsRequest request) {
        try {
            System.out.println("📥 Received batch request for user IDs: " + request.getUserIds());
            
            if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
                System.out.println("⚠️ Empty or null user IDs list");
                return ResponseEntity.ok(List.of());
            }
            
            List<User> users = userRepository.findAllById(request.getUserIds());
            System.out.println("✅ Found " + users.size() + " users in database");
            
            List<UserDetailsResponse> response = users.stream()
                    .map(user -> {
                        System.out.println("  - User ID " + user.getId() + ": " + user.getFirstName() + " " + user.getLastName());
                        return UserDetailsResponse.fromEntity(user);
                    })
                    .collect(Collectors.toList());
            
            System.out.println("📤 Returning " + response.size() + " user details");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error in getUsersByIds: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error fetching users: " + e.getMessage(), e);
        }
    }
}
