package com.englishflow.event.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String profilePhoto;
        private String phone; // for SMS reminders
    }

    public Map<Long, UserInfo> getUsersByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            String url = authServiceUrl + "/api/users/batch";
            log.info("Fetching batch user info from: {} for {} users", url, userIds.size());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            Map<String, List<Long>> requestBody = new HashMap<>();
            requestBody.put("userIds", userIds);

            HttpEntity<Map<String, List<Long>>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<List<UserInfo>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<List<UserInfo>>() {}
            );

            List<UserInfo> users = response.getBody();
            if (users == null) {
                log.warn("Received null response from auth service");
                return new HashMap<>();
            }

            log.info("Received {} users from auth service", users.size());
            Map<Long, UserInfo> userMap = new HashMap<>();
            for (UserInfo user : users) {
                log.info("User {}: email={}, firstName={}, lastName={}, profilePhoto={}", 
                    user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getProfilePhoto());
                userMap.put(user.getId(), user);
            }

            log.info("Successfully fetched {} users from auth service", userMap.size());
            return userMap;

        } catch (Exception e) {
            log.error("Error fetching users from auth service", e);
            return new HashMap<>();
        }
    }

    public UserInfo getUserById(Long userId) {
        try {
            String url = authServiceUrl + "/api/users/" + userId;
            return restTemplate.getForObject(url, UserInfo.class);
        } catch (Exception e) {
            log.error("Error fetching user {} from auth service", userId, e);
            return null;
        }
    }

    public void sendEventPaymentConfirmedEmail(String email, String firstName, String eventTitle, Double amount, String eventLink) {
        try {
            String url = authServiceUrl + "/api/email/event-payment-confirmed";
            Map<String, Object> body = new HashMap<>();
            body.put("email", email);
            body.put("firstName", firstName);
            body.put("eventTitle", eventTitle);
            body.put("amount", amount);
            body.put("eventLink", eventLink);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
            log.info("Event payment confirmed email sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send event payment confirmed email to {}: {}", email, e.getMessage());
        }
    }
}
