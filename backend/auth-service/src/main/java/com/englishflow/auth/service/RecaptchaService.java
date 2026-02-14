package com.englishflow.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class RecaptchaService {

    @Value("${recaptcha.secret:6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe}")
    private String recaptchaSecret;

    private final WebClient webClient;

    public RecaptchaService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.google.com/recaptcha/api").build();
    }

    public boolean verifyRecaptcha(String token) {
        if (token == null || token.isEmpty()) {
            log.warn("reCAPTCHA token is null or empty");
            return false;
        }

        try {
            Map<String, Object> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/siteverify")
                            .queryParam("secret", recaptchaSecret)
                            .queryParam("response", token)
                            .build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("success")) {
                Boolean success = (Boolean) response.get("success");
                log.info("reCAPTCHA verification result: {}", success);
                return Boolean.TRUE.equals(success);
            }

            log.warn("reCAPTCHA verification failed: Invalid response");
            return false;
        } catch (Exception e) {
            log.error("Error verifying reCAPTCHA: {}", e.getMessage());
            return false;
        }
    }
}
