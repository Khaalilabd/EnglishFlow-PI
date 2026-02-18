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
            log.info("Fetching user info from: {}", url);
            UserInfo userInfo = restTemplate.getForObject(url, UserInfo.class);
            if (userInfo != null) {
                log.info("Successfully fetched user info for userId {}: {} {}", userId, userInfo.getFirstName(), userInfo.getLastName());
                return userInfo;
            } else {
                log.warn("User info is null for userId: {}", userId);
                return createDefaultUserInfo(userId);
            }
        } catch (Exception e) {
            log.error("Failed to fetch user info for userId: {}", userId, e);
            return createDefaultUserInfo(userId);
        }
    }
    
    private UserInfo createDefaultUserInfo(Long userId) {
        UserInfo defaultInfo = new UserInfo();
        defaultInfo.setId(userId);
        defaultInfo.setFirstName("User");
        defaultInfo.setLastName(String.valueOf(userId));
        defaultInfo.setEmail("user" + userId + "@unknown.com");
        defaultInfo.setRole("STUDENT");
        defaultInfo.setProfilePhotoUrl(null);
        return defaultInfo;
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
        
        public String getFullName() {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            }
            return "User " + id;
        }
    }
}
