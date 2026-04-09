package com.englishflow.club.client;

import com.englishflow.club.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceClient {
    
    private final RestTemplate restTemplate;
    
    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;
    
    public UserInfoDTO getUserInfo(Long userId) {
        try {
            String url = authServiceUrl + "/api/users/" + userId;
            log.debug("Fetching user info from: {}", url);
            
            UserInfoDTO userInfo = restTemplate.getForObject(url, UserInfoDTO.class);
            log.debug("Successfully fetched user info for userId: {}", userId);
            return userInfo;
        } catch (Exception e) {
            log.error("Failed to fetch user info for userId {}: {}", userId, e.getMessage());
            // Return default user info if service is unavailable
            return UserInfoDTO.builder()
                    .id(userId)
                    .firstName("User")
                    .lastName(String.valueOf(userId))
                    .email("user" + userId + "@example.com")
                    .build();
        }
    }
    
    public Map<Long, UserInfoDTO> getUserInfoBatch(List<Long> userIds) {
        Map<Long, UserInfoDTO> userInfoMap = new HashMap<>();
        
        if (userIds == null || userIds.isEmpty()) {
            return userInfoMap;
        }
        
        try {
            String url = authServiceUrl + "/api/users/batch";
            log.debug("Fetching batch user info from: {} for {} users", url, userIds.size());
            
            Map<String, List<Long>> requestBody = Map.of("userIds", userIds);
            UserInfoDTO[] users = restTemplate.postForObject(url, requestBody, UserInfoDTO[].class);
            
            if (users != null) {
                for (UserInfoDTO user : users) {
                    userInfoMap.put(user.getId(), user);
                }
                log.debug("Successfully fetched {} user infos", users.length);
            }
        } catch (Exception e) {
            log.error("Failed to fetch batch user info: {}", e.getMessage());
            // Return default user info for all requested users
            for (Long userId : userIds) {
                userInfoMap.put(userId, UserInfoDTO.builder()
                        .id(userId)
                        .firstName("User")
                        .lastName(String.valueOf(userId))
                        .email("user" + userId + "@example.com")
                        .build());
            }
        }
        
        return userInfoMap;
    }
    
    public void sendClubPaymentRequiredEmail(String email, String firstName, String clubName, Double registrationFee, String paymentLink) {
        try {
            String url = authServiceUrl + "/api/email/club-payment-required";
            log.info("📧 Sending payment required email to: {}", email);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("firstName", firstName);
            requestBody.put("clubName", clubName);
            requestBody.put("registrationFee", registrationFee != null ? registrationFee.toString() : "0");
            requestBody.put("paymentLink", paymentLink);

            restTemplate.postForObject(url, requestBody, String.class);
            log.info("✅ Payment required email sent to: {}", email);
        } catch (Exception e) {
            log.error("❌ Failed to send payment required email to {}: {}", email, e.getMessage());
        }
    }

    public void sendClubMembershipRequestPendingEmail(String email, String firstName, String clubName, String message) {
        try {
            String url = authServiceUrl + "/api/email/club-membership-request-pending";
            log.info("📧 Attempting to send club membership request pending email to: {}", email);
            log.info("📧 Email endpoint URL: {}", url);
            log.info("📧 Request details - firstName: {}, clubName: {}, message: {}", 
                firstName, clubName, message != null ? "provided" : "empty");
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("firstName", firstName);
            requestBody.put("clubName", clubName);
            requestBody.put("message", message != null ? message : "");
            
            log.info("📧 Sending POST request to auth-service...");
            String response = restTemplate.postForObject(url, requestBody, String.class);
            log.info("✅ Successfully queued email for: {}. Response: {}", email, response);
        } catch (Exception e) {
            log.error("❌ Failed to send club membership request pending email to {}: {}", email, e.getMessage());
            log.error("❌ Error type: {}", e.getClass().getName());
            log.error("❌ Full error: ", e);
            // Don't throw exception - email is not critical for the flow
        }
    }
}
