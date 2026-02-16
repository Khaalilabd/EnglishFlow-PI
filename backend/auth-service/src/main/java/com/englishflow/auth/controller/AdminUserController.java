package com.englishflow.auth.controller;

import com.englishflow.auth.dto.CreateTutorRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateTutorRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<UserDTO> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        System.out.println("🟢 Activating user with ID: " + id);
        UserDTO user = userService.activateUser(id);
        System.out.println("✅ User activated successfully: " + user.getEmail());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        System.out.println("🔴 Deactivating user with ID: " + id);
        UserDTO user = userService.deactivateUser(id);
        System.out.println("✅ User deactivated successfully: " + user.getEmail());
        return ResponseEntity.ok(user);
    }
}
