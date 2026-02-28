package com.jungle.learning.controller;

import com.jungle.learning.dto.UserDTO;
import com.jungle.learning.service.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final UserServiceClient userServiceClient;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> testUserFetch(@PathVariable Long userId) {
        log.info("Testing user fetch for userId: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            UserDTO user = userServiceClient.getUserById(userId);
            String userName = userServiceClient.getUserName(userId);
            
            response.put("success", true);
            response.put("userId", userId);
            response.put("user", user);
            response.put("fullName", userName);
            response.put("message", "Successfully fetched user information");
            
            log.info("Test successful for userId {}: {}", userId, userName);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("userId", userId);
            response.put("error", e.getMessage());
            response.put("message", "Failed to fetch user information");
            
            log.error("Test failed for userId {}: {}", userId, e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "learning-service");
        response.put("userServiceClient", "CONFIGURED");
        return ResponseEntity.ok(response);
    }
}
