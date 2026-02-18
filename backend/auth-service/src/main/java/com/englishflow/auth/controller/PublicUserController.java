package com.englishflow.auth.controller;

import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public/users")
@RequiredArgsConstructor
public class PublicUserController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getAllStudents() {
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
    }
}
