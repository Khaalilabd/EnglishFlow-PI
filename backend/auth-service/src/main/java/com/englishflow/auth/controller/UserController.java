package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            // Retourner uniquement les étudiants pour la messagerie
            return ResponseEntity.ok(userRepository.findAll().stream()
                .filter(user -> user.getRole() == User.Role.STUDENT)
                .map(user -> Map.of(
                    "id", user.getId(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail(),
                    "profilePhotoUrl", user.getProfilePhoto() != null ? user.getProfilePhoto() : ""
                ))
                .toList());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

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
        if (request.getCin() != null) {
            user.setCin(request.getCin());
        }
        if (request.getEnglishLevel() != null) {
            user.setEnglishLevel(request.getEnglishLevel());
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
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/upload-photo")
    public ResponseEntity<Map<String, String>> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // Valider le fichier
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Please select a file"));
            }
            
            // Vérifier le type
            if (!fileStorageService.isValidImageFile(file)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only image files are allowed"));
            }
            
            // Vérifier la taille (max 5MB)
            long maxSize = 5 * 1024 * 1024; // 5MB
            if (!fileStorageService.isValidFileSize(file, maxSize)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size must be less than 5MB"));
            }
            
            // Trouver l'utilisateur
            User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Supprimer l'ancienne photo si elle existe
            if (user.getProfilePhoto() != null) {
                fileStorageService.deleteFile(user.getProfilePhoto());
            }
            
            // Sauvegarder le nouveau fichier
            String photoUrl = fileStorageService.storeFile(file);
            
            // Mettre à jour l'utilisateur
            user.setProfilePhoto(photoUrl);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("profilePhoto", photoUrl));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Map<String, String>> deleteProfilePhoto(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Supprimer le fichier physique
            if (user.getProfilePhoto() != null) {
                fileStorageService.deleteFile(user.getProfilePhoto());
            }
            
            // Mettre à jour l'utilisateur
            user.setProfilePhoto(null);
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Profile photo deleted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete photo: " + e.getMessage()));
        }
    }
}
