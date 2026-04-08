package com.englishflow.club.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KonnectService {

    private final RestTemplate restTemplate;

    @Value("${konnect.api-url:https://api.preprod.konnect.network/api/v2/payments/init-payment}")
    private String konnectApiUrl;

    @Value("${konnect.api-key}")
    private String konnectApiKey;

    @Value("${konnect.wallet-id}")
    private String konnectWalletId;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    public Map<String, Object> initPayment(Integer requestId, Double amount, String firstName, String email) {
        log.info("Initiating Konnect payment for request {} amount {} TND", requestId, amount);

        Map<String, Object> body = new HashMap<>();
        body.put("receiverWalletId", konnectWalletId);
        body.put("token", "TND");
        body.put("amount", Math.round(amount * 1000)); // millimes
        body.put("type", "immediate");
        body.put("description", "Club registration fee - Request " + requestId);
        body.put("acceptedPaymentMethods", List.of("bank_card", "wallet", "e-DINAR"));
        body.put("lifespan", 10);
        body.put("checkoutForm", true);
        body.put("addPaymentFeesToAmount", false);
        body.put("firstName", firstName);
        body.put("email", email);
        body.put("orderId", String.valueOf(requestId));
        body.put("successUrl", frontendUrl + "/user-panel/club-payment/" + requestId + "?status=success&method=KONNECT");
        body.put("failUrl", frontendUrl + "/user-panel/club-payment/" + requestId + "?status=failed&method=KONNECT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", konnectApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(konnectApiUrl, HttpMethod.POST, entity, Map.class);
            log.info("Konnect payment initiated successfully for request {}", requestId);
            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to initiate Konnect payment for request {}: {}", requestId, e.getMessage());
            throw new RuntimeException("Failed to initiate Konnect payment: " + e.getMessage());
        }
    }
}
