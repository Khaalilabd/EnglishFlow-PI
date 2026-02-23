package com.englishflow.courses.client;

import com.englishflow.courses.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client for communicating with Auth Service
 * Uses Eureka service discovery to locate auth-service
 */
@FeignClient(name = "auth-service", path = "/api/users")
public interface AuthServiceClient {
    
    /**
     * Get user details by ID
     */
    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
    
    /**
     * Get all users
     */
    @GetMapping
    List<UserDTO> getAllUsers();
    
    /**
     * Get users by role (TUTOR, STUDENT, ADMIN)
     */
    @GetMapping("/role/{role}")
    List<UserDTO> getUsersByRole(@PathVariable("role") String role);
}