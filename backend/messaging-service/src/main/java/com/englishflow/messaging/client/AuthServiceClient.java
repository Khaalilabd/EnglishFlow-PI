package com.englishflow.messaging.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {
    
    private final RestTemplate restTemplate;
    
    public UserInfo getUserInfo(Long userId) {
        try {
            String url = "http://auth-service/auth/users/" + userId;
            return restTemplate.getForObject(url, UserInfo.class);
        } catch (Exception e) {
            log.error("Failed to fetch user info for userId: {}", userId, e);
            return new UserInfo(userId, "User " + userId, "", "STUDENT", null);
        }
    }
    
    @Data
    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private String profilePhotoUrl;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String firstName, String lastName, String role, String profilePhotoUrl) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = "user" + id + "@unknown.com";  // Email par défaut
            this.role = role;
            this.profilePhotoUrl = profilePhotoUrl;
        }
        
        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}
