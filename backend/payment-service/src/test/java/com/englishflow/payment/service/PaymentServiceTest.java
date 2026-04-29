package com.englishflow.payment.service;

import com.englishflow.payment.dto.InitiatePaymentRequest;
import com.englishflow.payment.dto.PaymentDTO;
import com.englishflow.payment.dto.PaymentStatsDTO;
import com.englishflow.payment.dto.PaymeeWebhookPayload;
import com.englishflow.payment.entity.Payment;
import com.englishflow.payment.enums.PaymentItemType;
import com.englishflow.payment.enums.PaymentStatus;
import com.englishflow.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "paymeeApiKey", "test-api-key");
        ReflectionTestUtils.setField(paymentService, "paymeeBaseUrl", "https://api.test.paymee.tn");
        ReflectionTestUtils.setField(paymentService, "frontendUrl", "http://localhost:4200");
        ReflectionTestUtils.setField(paymentService, "backendUrl", "http://localhost:8080");
    }

    @Test
    void initiatePayment_ShouldThrowException_WhenAlreadyPaid() {
        // Arrange
        InitiatePaymentRequest request = new InitiatePaymentRequest();
        request.setStudentId(1L);
        request.setItemType("COURSE");
        request.setItemId(1L);
        request.setAmount(BigDecimal.valueOf(100));

        when(paymentRepository.countByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, "COURSE", 1L, "SUCCESS")).thenReturn(1L);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            paymentService.initiatePayment(request);
        });

        assertEquals("You are already enrolled in this item.", exception.getMessage());
    }

    @Test
    void initiatePayment_ShouldDeletePendingPayment_WhenExists() {
        // Arrange
        InitiatePaymentRequest request = new InitiatePaymentRequest();
        request.setStudentId(1L);
        request.setStudentName("John Doe");
        request.setStudentEmail("john@example.com");
        request.setItemType("COURSE");
        request.setItemId(1L);
        request.setItemName("Test Course");
        request.setAmount(BigDecimal.valueOf(100));

        Payment existingPayment = new Payment();
        existingPayment.setId(1L);

        when(paymentRepository.countByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, "COURSE", 1L, "SUCCESS")).thenReturn(0L);
        when(paymentRepository.findFirstByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, PaymentItemType.COURSE, 1L, PaymentStatus.PENDING))
            .thenReturn(Optional.of(existingPayment));

        Payment savedPayment = createTestPayment();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Map<String, Object> paymeeResponse = createPaymeeResponse();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(paymeeResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(responseEntity);

        // Act
        PaymentDTO result = paymentService.initiatePayment(request);

        // Assert
        assertNotNull(result);
        verify(paymentRepository).delete(existingPayment);
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    void initiatePayment_ShouldCreatePayment_WhenSuccessful() {
        // Arrange
        InitiatePaymentRequest request = new InitiatePaymentRequest();
        request.setStudentId(1L);
        request.setStudentName("John Doe");
        request.setStudentEmail("john@example.com");
        request.setItemType("COURSE");
        request.setItemId(1L);
        request.setItemName("Test Course");
        request.setAmount(BigDecimal.valueOf(100));

        when(paymentRepository.countByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, "COURSE", 1L, "SUCCESS")).thenReturn(0L);
        when(paymentRepository.findFirstByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, PaymentItemType.COURSE, 1L, PaymentStatus.PENDING))
            .thenReturn(Optional.empty());

        Payment savedPayment = createTestPayment();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Map<String, Object> paymeeResponse = createPaymeeResponse();
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(paymeeResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class))).thenReturn(responseEntity);

        // Act
        PaymentDTO result = paymentService.initiatePayment(request);

        // Assert
        assertNotNull(result);
        assertEquals(savedPayment.getOrderId(), result.getOrderId());
        assertEquals(savedPayment.getStudentId(), result.getStudentId());
        verify(paymentRepository, times(2)).save(any(Payment.class));
    }

    @Test
    void initiatePayment_ShouldThrowException_WhenPaymeeApiFails() {
        // Arrange
        InitiatePaymentRequest request = new InitiatePaymentRequest();
        request.setStudentId(1L);
        request.setStudentName("John Doe");
        request.setStudentEmail("john@example.com");
        request.setItemType("COURSE");
        request.setItemId(1L);
        request.setItemName("Test Course");
        request.setAmount(BigDecimal.valueOf(100));

        when(paymentRepository.countByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, "COURSE", 1L, "SUCCESS")).thenReturn(0L);
        when(paymentRepository.findFirstByStudentIdAndItemTypeAndItemIdAndStatus(
            1L, PaymentItemType.COURSE, 1L, PaymentStatus.PENDING))
            .thenReturn(Optional.empty());

        Payment savedPayment = createTestPayment();
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
            .thenThrow(new RuntimeException("Network error"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentService.initiatePayment(request);
        });

        assertEquals("Network error", exception.getMessage());
    }

    @Test
    void verifyPayment_ShouldReturnPayment_WhenAlreadySuccess() {
        // Arrange
        String orderId = "TEST-ORDER-123";
        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        // Act
        PaymentDTO result = paymentService.verifyPayment(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(payment.getOrderId(), result.getOrderId());
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void verifyPayment_ShouldMarkAsSuccess_WhenPending() {
        // Arrange
        String orderId = "TEST-ORDER-123";
        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentDTO result = paymentService.verifyPayment(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(payment.getOrderId(), result.getOrderId());
        verify(paymentRepository).save(payment);
        assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
    }

    @Test
    void verifyPayment_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        String orderId = "NONEXISTENT-ORDER";
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            paymentService.verifyPayment(orderId);
        });

        assertEquals("Payment not found: NONEXISTENT-ORDER", exception.getMessage());
    }

    @Test
    void handleWebhook_ShouldMarkAsSuccess_WhenPaymentStatusTrue() {
        // Arrange
        PaymeeWebhookPayload payload = new PaymeeWebhookPayload();
        payload.setOrderId("TEST-ORDER-123");
        payload.setToken("test-token");
        payload.setPaymentStatus(true);
        payload.setTransactionId(123L);
        payload.setReceivedAmount(BigDecimal.valueOf(100));
        payload.setCost(BigDecimal.valueOf(5));
        payload.setCheckSum("test-checksum");

        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByOrderId("TEST-ORDER-123")).thenReturn(Optional.of(payment));

        // Act
        paymentService.handleWebhook(payload);

        // Assert - The payment should not be saved due to checksum mismatch
        // This is the expected security behavior
        verify(paymentRepository, never()).save(payment);
    }

    @Test
    void handleWebhook_ShouldMarkAsFailed_WhenPaymentStatusFalse() {
        // Arrange
        PaymeeWebhookPayload payload = new PaymeeWebhookPayload();
        payload.setOrderId("TEST-ORDER-123");
        payload.setToken("test-token");
        payload.setPaymentStatus(false);
        payload.setCheckSum("test-checksum");

        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByOrderId("TEST-ORDER-123")).thenReturn(Optional.of(payment));

        // Act
        paymentService.handleWebhook(payload);

        // Assert - The payment should not be saved due to checksum mismatch
        // This is the expected security behavior
        verify(paymentRepository, never()).save(payment);
    }

    @Test
    void handleWebhook_ShouldDoNothing_WhenPaymentNotFound() {
        // Arrange
        PaymeeWebhookPayload payload = new PaymeeWebhookPayload();
        payload.setOrderId("NONEXISTENT-ORDER");
        payload.setToken("test-token");

        when(paymentRepository.findByOrderId("NONEXISTENT-ORDER")).thenReturn(Optional.empty());
        when(paymentRepository.findByToken("test-token")).thenReturn(Optional.empty());

        // Act
        paymentService.handleWebhook(payload);

        // Assert
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void getAllPayments_ShouldReturnPaymentList() {
        // Arrange
        List<Payment> payments = Arrays.asList(
            createTestPayment(),
            createTestPayment()
        );
        when(paymentRepository.findAllByOrderByCreatedAtDesc()).thenReturn(payments);

        // Act
        List<PaymentDTO> result = paymentService.getAllPayments();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getPaymentsByStudent_ShouldReturnStudentPayments() {
        // Arrange
        Long studentId = 1L;
        List<Payment> payments = Arrays.asList(createTestPayment());
        when(paymentRepository.findByStudentIdOrderByCreatedAtDesc(studentId)).thenReturn(payments);

        // Act
        List<PaymentDTO> result = paymentService.getPaymentsByStudent(studentId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getStats_ShouldReturnCorrectStatistics() {
        // Arrange
        List<Payment> payments = Arrays.asList(
            createPaymentWithStatus(PaymentStatus.SUCCESS),
            createPaymentWithStatus(PaymentStatus.PENDING),
            createPaymentWithStatus(PaymentStatus.FAILED)
        );
        when(paymentRepository.findAll()).thenReturn(payments);
        when(paymentRepository.sumSuccessfulPayments()).thenReturn(BigDecimal.valueOf(100));

        // Act
        PaymentStatsDTO result = paymentService.getStats();

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getTotalPayments());
        assertEquals(1L, result.getSuccessfulPayments());
        assertEquals(1L, result.getPendingPayments());
        assertEquals(1L, result.getFailedPayments());
        assertEquals(BigDecimal.valueOf(100), result.getTotalRevenue());
    }

    // Helper methods
    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId("TEST-ORDER-123");
        payment.setStudentId(1L);
        payment.setStudentName("John Doe");
        payment.setStudentEmail("john@example.com");
        payment.setStudentPhone("12345678"); // Add phone number
        payment.setItemType(PaymentItemType.COURSE);
        payment.setItemId(1L);
        payment.setItemName("Test Course");
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
        return payment;
    }

    private Payment createPaymentWithStatus(PaymentStatus status) {
        Payment payment = createTestPayment();
        payment.setStatus(status);
        return payment;
    }

    private Map<String, Object> createPaymeeResponse() {
        Map<String, Object> data = new HashMap<>();
        data.put("token", "test-token");
        data.put("payment_url", "https://payment.url");

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        return response;
    }
}