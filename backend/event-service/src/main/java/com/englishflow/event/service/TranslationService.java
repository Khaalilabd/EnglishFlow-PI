package com.englishflow.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class TranslationService {

    @Value("${translation.libretranslate.url:https://libretranslate.com}")
    private String apiUrl;

    @Value("${translation.libretranslate.key:}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String translate(String text, String targetLang) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                "q", text,
                "source", "auto",
                "target", targetLang,
                "api_key", apiKey
            );

            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl + "/translate",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("translatedText")) {
                return (String) response.getBody().get("translatedText");
            }
        } catch (Exception e) {
            log.warn("Translation failed for target lang {}: {}", targetLang, e.getMessage());
        }
        return null; // fallback: return null, frontend shows original
    }
}
