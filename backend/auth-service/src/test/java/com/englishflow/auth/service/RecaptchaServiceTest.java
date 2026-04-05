package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecaptchaServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private RecaptchaService recaptchaService;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        
        recaptchaService = new RecaptchaService(webClientBuilder);
        ReflectionTestUtils.setField(recaptchaService, "recaptchaSecret", "test-secret");
    }

    @Test
    void verifyRecaptcha_WithNullToken_ShouldReturnFalse() {
        boolean result = recaptchaService.verifyRecaptcha(null);
        assertFalse(result);
    }

    @Test
    void verifyRecaptcha_WithEmptyToken_ShouldReturnFalse() {
        boolean result = recaptchaService.verifyRecaptcha("");
        assertFalse(result);
    }

    @Test
    void verifyRecaptcha_WithValidToken_ShouldReturnTrue() {
        Map<String, Object> mockResponse = Map.of("success", true);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(mockResponse));

        boolean result = recaptchaService.verifyRecaptcha("valid-token");

        assertTrue(result);
    }

    @Test
    void verifyRecaptcha_WithInvalidToken_ShouldReturnFalse() {
        Map<String, Object> mockResponse = Map.of("success", false);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(mockResponse));

        boolean result = recaptchaService.verifyRecaptcha("invalid-token");

        assertFalse(result);
    }

    @Test
    void verifyRecaptcha_WithException_ShouldReturnFalse() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        boolean result = recaptchaService.verifyRecaptcha("token");

        assertFalse(result);
    }

    @Test
    void verifyRecaptcha_WithNullResponse_ShouldReturnFalse() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.empty());

        boolean result = recaptchaService.verifyRecaptcha("token");

        assertFalse(result);
    }
}
