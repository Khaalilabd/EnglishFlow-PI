package com.englishflow.courses.service;

import com.englishflow.courses.client.AuthServiceClient;
import com.englishflow.courses.dto.UserDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for validating users and fetching user information from auth-service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    
    private final AuthServiceClient authServiceClient;
    
    /**
     * Get user details by ID with error handling
     */
    public UserDTO getUserById(Long userId) {
        try {
            return authServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            log.error("User not found with ID: {}", userId);
            throw new RuntimeException("User not found with ID: " + userId);
        } catch (FeignException e) {
            log.error("Error fetching user from auth-service: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch user from auth-service: " + e.getMessage());
        }
    }
    
    /**
     * Validate if user exists and is active
     */
    public boolean isUserValid(Long userId) {
        try {
            UserDTO user = authServiceClient.getUserById(userId);
            return user != null && user.isActive();
        } catch (Exception e) {
            log.error("Error validating user {}: {}", userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if user has specific role
     */
    public boolean hasRole(Long userId, String role) {
        try {
            UserDTO user = authServiceClient.getUserById(userId);
            return user != null && role.equalsIgnoreCase(user.getRole());
        } catch (Exception e) {
            log.error("Error checking role for user {}: {}", userId, e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate if user is a tutor
     */
    public boolean isTutor(Long userId) {
        return hasRole(userId, "TUTOR");
    }
    
    /**
     * Validate if user is a student
     */
    public boolean isStudent(Long userId) {
        return hasRole(userId, "STUDENT");
    }
    
    /**
     * Get all tutors
     */
    public List<UserDTO> getAllTutors() {
        try {
            return authServiceClient.getUsersByRole("TUTOR");
        } catch (FeignException e) {
            log.error("Error fetching tutors: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch tutors: " + e.getMessage());
        }
    }
    
    /**
     * Get all students
     */
    public List<UserDTO> getAllStudents() {
        try {
            return authServiceClient.getUsersByRole("STUDENT");
        } catch (FeignException e) {
            log.error("Error fetching students: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch students: " + e.getMessage());
        }
    }
    
    /**
     * Validate tutor exists before creating course
     */
    public void validateTutorExists(Long tutorId) {
        if (!isTutor(tutorId)) {
            throw new RuntimeException("Invalid tutor ID: " + tutorId + ". User is not a tutor or does not exist.");
        }
    }
    
    /**
     * Validate student exists before enrollment
     */
    public void validateStudentExists(Long studentId) {
        if (!isStudent(studentId)) {
            throw new RuntimeException("Invalid student ID: " + studentId + ". User is not a student or does not exist.");
        }
    }
}