package com.englishflow.club.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KonnectServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KonnectService konnectService;

    private static final String API_URL = "https://api.preprod.konnect.network/api/v2/payments/init-payment";
    private static final String API_KEY = "test-api-key";
    private static final String WALLET_ID = "test-wallet-id";
    private static final String FRONTEND_URL = "http://localhost:4200";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(konnectService, "konnectApiUrl", API_URL);
        ReflectionTestUtils.setField(konnectService, "konnectApiKey", API_KEY);
        ReflectionTestUtils.setField(konnectService, "konnectWalletId", WALLET_ID);
        ReflectionTestUtils.setField(konnectService, "frontendUrl", FRONTEND_URL);
    }

    @Test
    void initPayment_ShouldReturnPaymentResponse_WhenSuccessful() {
        // Given
        Integer requestId = 123;
        Double amount = 50.0;
        String firstName = "John";
        String email = "john@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("paymentRef", "PAY123");
        expectedResponse.put("payUrl", "https://payment.url");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(API_URL),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        Map<String, Object> result = konnectService.initPayment(requestId, amount, firstName, email);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).containsEntry("paymentRef", "PAY123");
        assertThat(result).containsEntry("payUrl", "https://payment.url");

        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(API_URL), eq(HttpMethod.POST), entityCaptor.capture(), eq(Map.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) capturedEntity.getBody();

        assertThat(body).containsEntry("receiverWalletId", WALLET_ID);
        assertThat(body).containsEntry("token", "TND");
        assertThat(body).containsEntry("amount", 50000L); // 50 TND * 1000 millimes
        assertThat(body).containsEntry("type", "immediate");
        assertThat(body).containsEntry("description", "Club registration fee - Request 123");
        assertThat(body).containsEntry("acceptedPaymentMethods", List.of("bank_card", "wallet", "e-DINAR"));
        assertThat(body).containsEntry("lifespan", 10);
        assertThat(body).containsEntry("checkoutForm", true);
        assertThat(body).containsEntry("addPaymentFeesToAmount", false);
        assertThat(body).containsEntry("firstName", firstName);
        assertThat(body).containsEntry("email", email);
        assertThat(body).containsEntry("orderId", "123");
        assertThat(body).containsEntry("successUrl", FRONTEND_URL + "/user-panel/club-payment/123?status=success&method=KONNECT");
        assertThat(body).containsEntry("failUrl", FRONTEND_URL + "/user-panel/club-payment/123?status=failed&method=KONNECT");

        HttpHeaders headers = capturedEntity.getHeaders();
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(headers.get("x-api-key")).containsExactly(API_KEY);
    }

    @Test
    void initPayment_ShouldConvertAmountToMillimes() {
        // Given
        Integer requestId = 456;
        Double amount = 75.5;
        String firstName = "Jane";
        String email = "jane@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("paymentRef", "PAY456");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        konnectService.initPayment(requestId, amount, firstName, email);

        // Then
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Map.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) capturedEntity.getBody();
        assertThat(body.get("amount")).isEqualTo(75500L); // 75.5 TND * 1000 millimes
    }

    @Test
    void initPayment_ShouldThrowException_WhenRestTemplateThrowsException() {
        // Given
        Integer requestId = 789;
        Double amount = 100.0;
        String firstName = "Bob";
        String email = "bob@example.com";

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        assertThatThrownBy(() -> konnectService.initPayment(requestId, amount, firstName, email))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to initiate Konnect payment")
            .hasMessageContaining("Connection timeout");

        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void initPayment_ShouldHandleNullResponse() {
        // Given
        Integer requestId = 999;
        Double amount = 25.0;
        String firstName = "Alice";
        String email = "alice@example.com";

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        Map<String, Object> result = konnectService.initPayment(requestId, amount, firstName, email);

        // Then
        assertThat(result).isNull();
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void initPayment_ShouldIncludeCorrectSuccessAndFailUrls() {
        // Given
        Integer requestId = 555;
        Double amount = 30.0;
        String firstName = "Charlie";
        String email = "charlie@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        konnectService.initPayment(requestId, amount, firstName, email);

        // Then
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Map.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) capturedEntity.getBody();
        String successUrl = (String) body.get("successUrl");
        String failUrl = (String) body.get("failUrl");

        assertThat(successUrl).contains("/user-panel/club-payment/555");
        assertThat(successUrl).contains("status=success");
        assertThat(successUrl).contains("method=KONNECT");

        assertThat(failUrl).contains("/user-panel/club-payment/555");
        assertThat(failUrl).contains("status=failed");
        assertThat(failUrl).contains("method=KONNECT");
    }

    @Test
    void initPayment_ShouldSetCorrectPaymentMethods() {
        // Given
        Integer requestId = 777;
        Double amount = 40.0;
        String firstName = "David";
        String email = "david@example.com";

        Map<String, Object> expectedResponse = new HashMap<>();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // When
        konnectService.initPayment(requestId, amount, firstName, email);

        // Then
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), entityCaptor.capture(), eq(Map.class));

        HttpEntity capturedEntity = entityCaptor.getValue();
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) capturedEntity.getBody();
        @SuppressWarnings("unchecked")
        List<String> paymentMethods = (List<String>) body.get("acceptedPaymentMethods");

        assertThat(paymentMethods).containsExactly("bank_card", "wallet", "e-DINAR");
    }
}
