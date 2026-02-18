package com.englishflow.courses.client;

import com.englishflow.courses.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${auth-service.url:http://localhost:8081}")
    private String authServiceUrl;
    
    /**
     * Get user details from auth-service
     */
    public UserDTO getUserById(Long userId) {
        try {
            String url = authServiceUrl + "/api/users/" + userId;
            return restTemplate.getForObject(url, UserDTO.class);
        } catch (Exception e) {
            // Log error and return null or throw custom exception
            System.err.println("Failed to fetch user from auth-service: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Validate if user exists and is active
     */
    public boolean isUserValid(Long userId) {
        UserDTO user = getUserById(userId);
        return user != null && user.isActive();
    }
    
    /**
     * Check if user has specific role
     */
    public boolean hasRole(Long userId, String role) {
        UserDTO user = getUserById(userId);
        return user != null && role.equals(user.getRole());
    }
}