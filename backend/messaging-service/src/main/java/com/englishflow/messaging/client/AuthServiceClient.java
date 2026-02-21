package com.englishflow.messaging.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AuthServiceClient {
    
    private final RestTemplate restTemplate;
    private final RestTemplate directRestTemplate;
    
    public AuthServiceClient(RestTemplate restTemplate, 
                            @Qualifier("directRestTemplate") RestTemplate directRestTemplate) {
        this.restTemplate = restTemplate;
        this.directRestTemplate = directRestTemplate;
    }
    
    public UserInfo getUserInfo(Long userId) {
        // Essayer d'abord via Eureka avec l'endpoint public
        try {
            String url = "http://auth-service/auth/users/" + userId + "/public";
            log.info("Fetching user info from Eureka (public endpoint): {}", url);
            
            UserInfo userInfo = restTemplate.getForObject(url, UserInfo.class);
            if (userInfo != null) {
                log.info("Successfully fetched user info for userId {}: {} {}", userId, userInfo.getFirstName(), userInfo.getLastName());
                return userInfo;
            } else {
                log.warn("User info is null for userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to fetch user info via Eureka for userId: {}. Error: {}", userId, e.getMessage());
            log.debug("Full stack trace:", e);
        }
        
        // Fallback: essayer via localhost (pour développement)
        try {
            String fallbackUrl = "http://localhost:8080/api/auth/users/" + userId + "/public";
            log.info("Trying fallback URL: {}", fallbackUrl);
            
            UserInfo userInfo = directRestTemplate.getForObject(fallbackUrl, UserInfo.class);
            if (userInfo != null) {
                log.info("Successfully fetched user info via fallback for userId {}: {} {}", userId, userInfo.getFirstName(), userInfo.getLastName());
                return userInfo;
            } else {
                log.warn("User info is null from fallback for userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("Failed to fetch user info via fallback for userId: {}. Error: {}", userId, e.getMessage());
            log.debug("Full stack trace:", e);
        }
        
        // Si tout échoue, retourner les infos par défaut
        log.warn("All attempts failed, returning default user info for userId: {}", userId);
        return createDefaultUserInfo(userId);
    }
    
    private UserInfo createDefaultUserInfo(Long userId) {
        UserInfo defaultInfo = new UserInfo();
        defaultInfo.setId(userId);
        defaultInfo.setFirstName("User");
        defaultInfo.setLastName(String.valueOf(userId));
        defaultInfo.setEmail("user" + userId + "@unknown.com");
        defaultInfo.setRole("STUDENT");
        defaultInfo.setProfilePhoto(null);
        return defaultInfo;
    }
    
    @Data
    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String role;
        private String profilePhoto;  // Changed from profilePhotoUrl to match UserDTO
        
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
        
        // Getter for backward compatibility
        public String getProfilePhotoUrl() {
            return profilePhoto;
        }
    }
}
