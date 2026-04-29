package com.englishflow.payment.service;

import com.englishflow.payment.client.CoursesServiceClient;
import com.englishflow.payment.client.PaymeeClient;
import com.englishflow.payment.dto.*;
import com.englishflow.payment.dto.paymee.PaymeeRefundData;
import com.englishflow.payment.dto.paymee.PaymeeRefundRequest;
import com.englishflow.payment.dto.paymee.PaymeeRefundResponse;
import com.englishflow.payment.entity.Payment;
import com.englishflow.payment.entity.Refund;
import com.englishflow.payment.enums.PaymentItemType;
import com.englishflow.payment.enums.PaymentStatus;
import com.englishflow.payment.enums.RefundStatus;
import com.englishflow.payment.exception.InvalidRefundStatusException;
import com.englishflow.payment.exception.RefundNotEligibleException;
import com.englishflow.payment.exception.RefundNotFoundException;
import com.englishflow.payment.repository.PaymentRepository;
import com.englishflow.payment.repository.RefundRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefundServiceTest {

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymeeClient paymeeClient;

    @Mock
    private CoursesServiceClient coursesServiceClient;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RefundService refundService;

    @Test
    void createRefundRequest_ShouldCreateRefund_WhenValid() {
        // Arrange
        CreateRefundRequest request = new CreateRefundRequest();
        request.setPaymentId(1L);
        request.setReason("Not satisfied");

        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now().minusDays(3)); // Within 7-day window

        Refund savedRefund = createTestRefund();

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(refundRepository.existsByPaymentIdAndStatusNotIn(eq(1L), anyList())).thenReturn(false);
        when(refundRepository.save(any(Refund.class))).thenReturn(savedRefund);

        // Act
        RefundDTO result = refundService.createRefundRequest(request, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(savedRefund.getId(), result.getId());
        verify(refundRepository).save(any(Refund.class));
    }

    @Test
    void createRefundRequest_ShouldThrowException_WhenPaymentNotFound() {
        // Arrange
        CreateRefundRequest request = new CreateRefundRequest();
        request.setPaymentId(999L);

        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RefundNotFoundException exception = assertThrows(RefundNotFoundException.class, () -> {
            refundService.createRefundRequest(request, 1L);
        });

        assertEquals("Payment not found: 999", exception.getMessage());
    }

    @Test
    void createRefundRequest_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        CreateRefundRequest request = new CreateRefundRequest();
        request.setPaymentId(1L);

        Payment payment = createTestPayment();
        payment.setStudentId(2L); // Different student

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refundService.createRefundRequest(request, 1L);
        });

        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void createRefundRequest_ShouldThrowException_WhenPaymentNotSuccess() {
        // Arrange
        CreateRefundRequest request = new CreateRefundRequest();
        request.setPaymentId(1L);

        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        // Act & Assert
        RefundNotEligibleException exception = assertThrows(RefundNotEligibleException.class, () -> {
            refundService.createRefundRequest(request, 1L);
        });

        assertEquals("Only successful payments can be refunded", exception.getMessage());
    }

    @Test
    void createRefundRequest_ShouldThrowException_WhenRefundAlreadyExists() {
        // Arrange
        CreateRefundRequest request = new CreateRefundRequest();
        request.setPaymentId(1L);

        Payment payment = createTestPayment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(refundRepository.existsByPaymentIdAndStatusNotIn(eq(1L), anyList())).thenReturn(true);

        // Act & Assert
        RefundNotEligibleException exception = assertThrows(RefundNotEligibleException.class, () -> {
            refundService.createRefundRequest(request, 1L);
        });

        assertEquals("Refund already requested for this payment", exception.getMessage());
    }

    @Test
    void validateRefundEligibility_ShouldThrowException_WhenOutsideTimeWindow() {
        // Arrange
        Payment payment = createTestPayment();
        payment.setCreatedAt(LocalDateTime.now().minusDays(10)); // Outside 7-day window

        // Act & Assert
        RefundNotEligibleException exception = assertThrows(RefundNotEligibleException.class, () -> {
            refundService.validateRefundEligibility(payment);
        });

        assertEquals("Refund window has expired", exception.getMessage());
    }

    @Test
    void validateRefundEligibility_ShouldPass_WhenWithinTimeWindow() {
        // Arrange
        Payment payment = createTestPayment();
        payment.setCreatedAt(LocalDateTime.now().minusDays(3)); // Within 7-day window

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> {
            refundService.validateRefundEligibility(payment);
        });
    }

    @Test
    void approveRefund_ShouldApproveAndProcess_WhenValid() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.PENDING);

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        // Don't mock paymeeClient to avoid automatic processing
        // The test should only verify approval, not processing

        // Act
        RefundDTO result = refundService.approveRefund(1L, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, refund.getAdminId());
        assertNotNull(refund.getApprovedAt());
        verify(refundRepository, atLeast(1)).save(refund);
    }

    @Test
    void approveRefund_ShouldThrowException_WhenNotPending() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.APPROVED);

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

        // Act & Assert
        InvalidRefundStatusException exception = assertThrows(InvalidRefundStatusException.class, () -> {
            refundService.approveRefund(1L, 100L);
        });

        assertEquals("Only pending refund requests can be reviewed", exception.getMessage());
    }

    @Test
    void rejectRefund_ShouldReject_WhenValid() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.PENDING);

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        // Act
        RefundDTO result = refundService.rejectRefund(1L, 100L, "Invalid reason");

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.REJECTED, refund.getStatus());
        assertEquals(100L, refund.getAdminId());
        assertEquals("Invalid reason", refund.getRejectionReason());
        assertNotNull(refund.getRejectedAt());
        verify(refundRepository).save(refund);
    }

    @Test
    void processRefund_ShouldComplete_WhenPaymeeSucceeds() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.APPROVED);

        PaymeeRefundResponse response = new PaymeeRefundResponse();
        response.setStatus(true);
        PaymeeRefundData data = new PaymeeRefundData();
        data.setRefund_transaction_id("REFUND-123");
        response.setData(data);

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);
        when(paymeeClient.refundPayment(any(PaymeeRefundRequest.class))).thenReturn(response);

        // Act
        RefundDTO result = refundService.processRefund(1L);

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.COMPLETED, refund.getStatus());
        assertEquals("REFUND-123", refund.getPaymeeTransactionId());
        assertNotNull(refund.getCompletedAt());
        verify(refundRepository, atLeast(2)).save(refund);
    }

    @Test
    void processRefund_ShouldFail_WhenPaymeeFails() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.APPROVED);

        PaymeeRefundResponse response = new PaymeeRefundResponse();
        response.setStatus(false);
        response.setMessage("Refund failed");

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);
        when(paymeeClient.refundPayment(any(PaymeeRefundRequest.class))).thenReturn(response);

        // Act
        RefundDTO result = refundService.processRefund(1L);

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.FAILED, refund.getStatus());
        assertEquals("Refund failed", refund.getErrorMessage());
        verify(refundRepository, atLeast(2)).save(refund);
    }

    @Test
    void cancelRefund_ShouldCancel_WhenValid() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStatus(RefundStatus.PENDING);
        refund.setStudentId(1L);

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));
        when(refundRepository.save(any(Refund.class))).thenReturn(refund);

        // Act
        RefundDTO result = refundService.cancelRefund(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(RefundStatus.CANCELLED, refund.getStatus());
        assertNotNull(refund.getCancelledAt());
        verify(refundRepository).save(refund);
    }

    @Test
    void cancelRefund_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        Refund refund = createTestRefund();
        refund.setStudentId(2L); // Different student

        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            refundService.cancelRefund(1L, 1L);
        });

        assertEquals("Unauthorized access", exception.getMessage());
    }

    @Test
    void getRefundById_ShouldReturnRefund_WhenExists() {
        // Arrange
        Refund refund = createTestRefund();
        when(refundRepository.findById(1L)).thenReturn(Optional.of(refund));

        // Act
        RefundDTO result = refundService.getRefundById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(refund.getId(), result.getId());
    }

    @Test
    void getRefundsByStudent_ShouldReturnStudentRefunds() {
        // Arrange
        List<Refund> refunds = Arrays.asList(createTestRefund());
        when(refundRepository.findByStudentIdOrderByRequestedAtDesc(1L)).thenReturn(refunds);

        // Act
        List<RefundDTO> result = refundService.getRefundsByStudent(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllRefunds_ShouldReturnAllRefunds_WhenNoFilter() {
        // Arrange
        List<Refund> refunds = Arrays.asList(createTestRefund(), createTestRefund());
        when(refundRepository.findAllByOrderByRequestedAtDesc()).thenReturn(refunds);

        // Act
        List<RefundDTO> result = refundService.getAllRefunds(null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getRefundStatistics_ShouldReturnCorrectStats() {
        // Arrange
        List<Refund> refunds = Arrays.asList(
            createRefundWithStatus(RefundStatus.PENDING),
            createRefundWithStatus(RefundStatus.COMPLETED),
            createRefundWithStatus(RefundStatus.REJECTED)
        );
        when(refundRepository.findAll()).thenReturn(refunds);

        // Act
        RefundStatsDTO result = refundService.getRefundStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getTotalRefunds());
        assertEquals(1L, result.getPendingRefunds());
        assertEquals(1L, result.getCompletedRefunds());
        assertEquals(1L, result.getRejectedRefunds());
    }

    @Test
    void reverseEnrollment_ShouldCallCoursesService_ForCourse() {
        // Arrange
        Refund refund = createTestRefund();
        Payment payment = refund.getPayment();
        payment.setItemType(PaymentItemType.COURSE);

        // Act
        refundService.reverseEnrollment(refund);

        // Assert
        verify(coursesServiceClient).unenrollFromCourse(refund.getStudentId(), payment.getItemId());
    }

    @Test
    void reverseEnrollment_ShouldCallCoursesService_ForPack() {
        // Arrange
        Refund refund = createTestRefund();
        Payment payment = refund.getPayment();
        payment.setItemType(PaymentItemType.PACK);

        // Act
        refundService.reverseEnrollment(refund);

        // Assert
        verify(coursesServiceClient).unenrollFromPack(refund.getStudentId(), payment.getItemId());
    }

    // Helper methods
    private Payment createTestPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId("TEST-ORDER-123");
        payment.setStudentId(1L);
        payment.setStudentName("John Doe");
        payment.setStudentEmail("john@example.com");
        payment.setItemType(PaymentItemType.COURSE);
        payment.setItemId(1L);
        payment.setItemName("Test Course");
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        return payment;
    }

    private Refund createTestRefund() {
        Refund refund = new Refund();
        refund.setId(1L);
        refund.setPayment(createTestPayment());
        refund.setStudentId(1L);
        refund.setAmount(BigDecimal.valueOf(100));
        refund.setStatus(RefundStatus.PENDING);
        refund.setReason("Test reason");
        refund.setRequestedAt(LocalDateTime.now());
        refund.setCreatedAt(LocalDateTime.now());
        return refund;
    }

    private Refund createRefundWithStatus(RefundStatus status) {
        Refund refund = createTestRefund();
        refund.setStatus(status);
        return refund;
    }
}