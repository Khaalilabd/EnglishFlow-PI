package com.englishflow.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventKonnectServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EventKonnectService eventKonnectService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventKonnectService, "konnectApiUrl", "https://api.test.konnect.network/api/v2/payments/init-payment");
        ReflectionTestUtils.setField(eventKonnectService, "konnectApiKey", "test-api-key");
        ReflectionTestUtils.setField(eventKonnectService, "konnectWalletId", "test-wallet-id");
        ReflectionTestUtils.setField(eventKonnectService, "frontendUrl", "http://localhost:4200");
    }

    @Test
    void initPayment_ShouldReturnPaymentResponse_WhenSuccessful() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("paymentRef", "PAY123");
        expectedResponse.put("paymentUrl", "https://payment.url");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        Map<String, Object> result = eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        assertNotNull(result);
        assertEquals("PAY123", result.get("paymentRef"));
        assertEquals("https://payment.url", result.get("paymentUrl"));
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void initPayment_ShouldConvertAmountToMillimes() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.5;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("paymentRef", "PAY123");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            return body != null && body.get("amount").equals(50500L);
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeCorrectSuccessUrl() {
        // Arrange
        Integer participantId = 123;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            String successUrl = (String) body.get("successUrl");
            return successUrl != null && successUrl.contains("/user-panel/event-payment/123") && successUrl.contains("status=success");
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeCorrectFailUrl() {
        // Arrange
        Integer participantId = 123;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            String failUrl = (String) body.get("failUrl");
            return failUrl != null && failUrl.contains("/user-panel/event-payment/123") && failUrl.contains("status=failed");
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeApiKeyInHeaders() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            HttpHeaders headers = entity.getHeaders();
            return headers.containsKey("x-api-key") && "test-api-key".equals(headers.getFirst("x-api-key"));
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeWalletIdInBody() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            return body != null && "test-wallet-id".equals(body.get("receiverWalletId"));
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeOrderIdWithPrefix() {
        // Arrange
        Integer participantId = 456;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            return body != null && "EVT-456".equals(body.get("orderId"));
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeUserDetails() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "Jane";
        String email = "jane@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) entity.getBody();
            return body != null && "Jane".equals(body.get("firstName")) && "jane@example.com".equals(body.get("email"));
        }), eq(Map.class));
    }

    @Test
    void initPayment_ShouldThrowException_WhenRestTemplateThrowsException() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventKonnectService.initPayment(participantId, amount, firstName, email);
        });

        assertTrue(exception.getMessage().contains("Failed to initiate Konnect payment"));
    }

    @Test
    void initPayment_ShouldSetContentTypeToJson() {
        // Arrange
        Integer participantId = 1;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class)))
            .thenReturn(responseEntity);

        // Act
        eventKonnectService.initPayment(participantId, amount, firstName, email);

        // Assert
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), argThat(entity -> {
            HttpHeaders headers = entity.getHeaders();
            return MediaType.APPLICATION_JSON.equals(headers.getContentType());
        }), eq(Map.class));
    }
}
