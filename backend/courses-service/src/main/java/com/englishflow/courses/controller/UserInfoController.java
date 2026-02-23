package com.englishflow.courses.controller;

import com.englishflow.courses.dto.UserDTO;
import com.englishflow.courses.service.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for accessing user information from auth-service
 * This allows the frontend to get user details through the courses-service
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserInfoController {
    
    private final UserValidationService userValidationService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userValidationService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/tutors")
    public ResponseEntity<List<UserDTO>> getAllTutors() {
        List<UserDTO> tutors = userValidationService.getAllTutors();
        return ResponseEntity.ok(tutors);
    }
    
    @GetMapping("/students")
    public ResponseEntity<List<UserDTO>> getAllStudents() {
        List<UserDTO> students = userValidationService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    @GetMapping("/{id}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long id) {
        boolean isValid = userValidationService.isUserValid(id);
        return ResponseEntity.ok(isValid);
    }
    
    @GetMapping("/{id}/is-tutor")
    public ResponseEntity<Boolean> isTutor(@PathVariable Long id) {
        boolean isTutor = userValidationService.isTutor(id);
        return ResponseEntity.ok(isTutor);
    }
    
    @GetMapping("/{id}/is-student")
    public ResponseEntity<Boolean> isStudent(@PathVariable Long id) {
        boolean isStudent = userValidationService.isStudent(id);
        return ResponseEntity.ok(isStudent);
    }
}