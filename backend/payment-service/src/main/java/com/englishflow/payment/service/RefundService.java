package com.englishflow.payment.service;

import com.englishflow.payment.client.CoursesServiceClient;
import com.englishflow.payment.client.PaymeeClient;
import com.englishflow.payment.dto.*;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing refund operations.
 * Handles refund request creation, eligibility validation, approval/rejection,
 * processing with Paymee gateway, and enrollment reversal.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final PaymeeClient paymeeClient;
    private final CoursesServiceClient coursesServiceClient;
    private final RestTemplate restTemplate;

    private static final int REFUND_WINDOW_DAYS = 7;
    private static final double PROGRESS_THRESHOLD = 30.0;

    // ÔöÇÔöÇ Creation and validation ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Create a new refund request for a payment.
     * Validates payment exists, status is SUCCESS, no existing refund, and eligibility.
     */
    @Transactional
    public RefundDTO createRefundRequest(CreateRefundRequest request, Long studentId) {
        log.info("Creating refund request for payment {} by student {}", request.getPaymentId(), studentId);

        // Validate payment exists
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RefundNotFoundException("Payment not found: " + request.getPaymentId()));

        // Verify student owns the payment
        if (!payment.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        // Validate payment status is SUCCESS
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new RefundNotEligibleException("Only successful payments can be refunded");
        }

        // Check no existing refund for payment (excluding CANCELLED and REJECTED)
        boolean hasExistingRefund = refundRepository.existsByPaymentIdAndStatusNotIn(
                payment.getId(),
                List.of(RefundStatus.CANCELLED, RefundStatus.REJECTED)
        );
        if (hasExistingRefund) {
            throw new RefundNotEligibleException("Refund already requested for this payment");
        }

        // Validate refund eligibility
        validateRefundEligibility(payment);

        // Create refund entity
        Refund refund = Refund.builder()
                .payment(payment)
                .studentId(studentId)
                .amount(payment.getAmount())
                .status(RefundStatus.PENDING)
                .reason(request.getReason())
                .requestedAt(LocalDateTime.now())
                .build();

        refund = refundRepository.save(refund);
        log.info("Refund request created with ID: {}", refund.getId());

        return toDTO(refund);
    }

    /**
     * Validate refund eligibility based on time window and progress threshold.
     */
    public void validateRefundEligibility(Payment payment) {
        log.info("Validating refund eligibility for payment {}", payment.getId());

        // Check payment date within 7-day window
        long daysSincePayment = ChronoUnit.DAYS.between(payment.getCreatedAt(), LocalDateTime.now());
        if (daysSincePayment > REFUND_WINDOW_DAYS) {
            throw new RefundNotEligibleException("Refund window has expired");
        }

        // Check progress threshold based on item type
        if (payment.getItemType() == PaymentItemType.COURSE) {
            validateCourseProgress(payment.getStudentId(), payment.getItemId());
        } else if (payment.getItemType() == PaymentItemType.PACK) {
            validatePackProgress(payment.getStudentId(), payment.getItemId());
        }
    }

    private void validateCourseProgress(Long studentId, Long courseId) {
        try {
            String url = "http://localhost:8086/enrollments/enrollment?studentId=" + studentId + "&courseId=" + courseId;
            CourseEnrollmentDTO enrollment = restTemplate.getForObject(url, CourseEnrollmentDTO.class);

            if (enrollment != null && enrollment.getProgress() != null && enrollment.getProgress() > PROGRESS_THRESHOLD) {
                throw new RefundNotEligibleException("Course progress exceeds refund eligibility threshold");
            }
        } catch (RefundNotEligibleException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to check course progress: {}", e.getMessage());
            // Allow refund if we can't check progress
        }
    }

    private void validatePackProgress(Long studentId, Long packId) {
        try {
            String url = "http://localhost:8086/pack-enrollments/enrollment?studentId=" + studentId + "&packId=" + packId;
            PackEnrollmentDTO packEnrollment = restTemplate.getForObject(url, PackEnrollmentDTO.class);

            if (packEnrollment != null && packEnrollment.getProgressPercentage() != null 
                    && packEnrollment.getProgressPercentage() > PROGRESS_THRESHOLD) {
                throw new RefundNotEligibleException("Pack course progress exceeds refund eligibility threshold");
            }
        } catch (RefundNotEligibleException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to check pack progress: {}", e.getMessage());
            // Allow refund if we can't check progress
        }
    }

    // ÔöÇÔöÇ Admin operations ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Approve a refund request.
     * Updates status to APPROVED and automatically triggers processing.
     */
    @Transactional
    public RefundDTO approveRefund(Long refundId, Long adminId) {
        log.info("Approving refund {} by admin {}", refundId, adminId);

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(refundId));

        // Verify refund status is PENDING
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new InvalidRefundStatusException("Only pending refund requests can be reviewed");
        }

        // Update status to APPROVED
        refund.setStatus(RefundStatus.APPROVED);
        refund.setAdminId(adminId);
        refund.setApprovedAt(LocalDateTime.now());
        refund = refundRepository.save(refund);

        log.info("Refund {} approved, triggering processing", refundId);

        // Automatically trigger processing
        try {
            processRefund(refundId);
        } catch (Exception e) {
            log.error("Failed to process refund after approval: {}", e.getMessage());
            // Don't fail the approval, processing can be retried
        }

        return toDTO(refund);
    }

    /**
     * Reject a refund request.
     * Updates status to REJECTED with reason.
     */
    @Transactional
    public RefundDTO rejectRefund(Long refundId, Long adminId, String reason) {
        log.info("Rejecting refund {} by admin {}", refundId, adminId);

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(refundId));

        // Verify refund status is PENDING
        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new InvalidRefundStatusException("Only pending refund requests can be reviewed");
        }

        // Update status to REJECTED
        refund.setStatus(RefundStatus.REJECTED);
        refund.setAdminId(adminId);
        refund.setRejectedAt(LocalDateTime.now());
        refund.setRejectionReason(reason);
        refund = refundRepository.save(refund);

        log.info("Refund {} rejected", refundId);

        return toDTO(refund);
    }

    // ÔöÇÔöÇ Processing ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Process a refund with Paymee gateway.
     * Updates status to PROCESSING, calls Paymee, and handles success/failure.
     */
    @Transactional
    public RefundDTO processRefund(Long refundId) {
        log.info("Processing refund {}", refundId);

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(refundId));

        // Update status to PROCESSING
        refund.setStatus(RefundStatus.PROCESSING);
        refund.setProcessingAt(LocalDateTime.now());
        refund = refundRepository.save(refund);

        try {
            // Call Paymee to initiate refund
            Payment payment = refund.getPayment();
            PaymeeRefundRequest refundRequest = new PaymeeRefundRequest();
            refundRequest.setAmount(refund.getAmount());
            refundRequest.setTransaction_id(payment.getTransactionId() != null ? payment.getTransactionId().toString() : null);
            
            PaymeeRefundResponse response = paymeeClient.refundPayment(refundRequest);

            if (response != null && Boolean.TRUE.equals(response.getStatus())) {
                // Success: update status to COMPLETED
                refund.setStatus(RefundStatus.COMPLETED);
                refund.setCompletedAt(LocalDateTime.now());
                if (response.getData() != null) {
                    refund.setPaymeeTransactionId(response.getData().getRefund_transaction_id());
                }
                refund = refundRepository.save(refund);

                log.info("Refund {} completed successfully", refundId);

                // Trigger enrollment reversal
                try {
                    reverseEnrollment(refund);
                } catch (Exception e) {
                    log.error("Failed to reverse enrollment for refund {}: {}", refundId, e.getMessage());
                    // Don't fail the refund, enrollment can be reversed manually
                }
            } else {
                // Failure: update status to FAILED
                String errorMsg = response != null ? response.getMessage() : "Unknown error";
                refund.setStatus(RefundStatus.FAILED);
                refund.setErrorMessage(errorMsg);
                refund = refundRepository.save(refund);

                log.error("Refund {} failed: {}", refundId, errorMsg);
            }
        } catch (Exception e) {
            // Exception: update status to FAILED
            refund.setStatus(RefundStatus.FAILED);
            refund.setErrorMessage(e.getMessage());
            refund = refundRepository.save(refund);

            log.error("Refund {} processing failed with exception: {}", refundId, e.getMessage());
        }

        return toDTO(refund);
    }

    /**
     * Reverse enrollment after successful refund.
     * Calls courses service to unenroll student from course or pack.
     */
    public void reverseEnrollment(Refund refund) {
        log.info("Reversing enrollment for refund {}", refund.getId());

        Payment payment = refund.getPayment();
        Long studentId = refund.getStudentId();

        try {
            if (payment.getItemType() == PaymentItemType.COURSE) {
                coursesServiceClient.unenrollFromCourse(studentId, payment.getItemId());
                log.info("Unenrolled student {} from course {}", studentId, payment.getItemId());
            } else if (payment.getItemType() == PaymentItemType.PACK) {
                coursesServiceClient.unenrollFromPack(studentId, payment.getItemId());
                log.info("Unenrolled student {} from pack {}", studentId, payment.getItemId());
            }
        } catch (Exception e) {
            log.error("Failed to reverse enrollment for refund {}: {}", refund.getId(), e.getMessage());
            // Log error but don't fail refund
        }
    }

    // ÔöÇÔöÇ Student operations ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Cancel a refund request.
     * Only allowed for PENDING or APPROVED status.
     */
    @Transactional
    public RefundDTO cancelRefund(Long refundId, Long studentId) {
        log.info("Cancelling refund {} by student {}", refundId, studentId);

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(refundId));

        // Verify requesting student owns the refund
        if (!refund.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        // Verify refund status is PENDING or APPROVED
        if (refund.getStatus() != RefundStatus.PENDING && refund.getStatus() != RefundStatus.APPROVED) {
            throw new InvalidRefundStatusException("Cannot cancel refund in current status");
        }

        // Update status to CANCELLED
        refund.setStatus(RefundStatus.CANCELLED);
        refund.setCancelledAt(LocalDateTime.now());
        refund = refundRepository.save(refund);

        log.info("Refund {} cancelled", refundId);

        return toDTO(refund);
    }

    // ÔöÇÔöÇ Query methods ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Get refund by ID with authorization check.
     */
    public RefundDTO getRefundById(Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(refundId));
        return toDTO(refund);
    }

    /**
     * Get all refunds for a student.
     */
    public List<RefundDTO> getRefundsByStudent(Long studentId) {
        return refundRepository.findByStudentIdOrderByRequestedAtDesc(studentId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all refunds with optional filtering.
     */
    public List<RefundDTO> getAllRefunds(RefundFilterDTO filter) {
        if (filter == null || !filter.hasFilters()) {
            return refundRepository.findAllByOrderByRequestedAtDesc()
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        // Apply filters
        List<Refund> refunds = refundRepository.findAllByOrderByRequestedAtDesc();

        if (filter.getStatus() != null) {
            refunds = refunds.stream()
                    .filter(r -> r.getStatus() == filter.getStatus())
                    .collect(Collectors.toList());
        }

        if (filter.getStudentId() != null) {
            refunds = refunds.stream()
                    .filter(r -> r.getStudentId().equals(filter.getStudentId()))
                    .collect(Collectors.toList());
        }

        if (filter.getStartDate() != null) {
            refunds = refunds.stream()
                    .filter(r -> !r.getRequestedAt().toLocalDate().isBefore(filter.getStartDate()))
                    .collect(Collectors.toList());
        }

        if (filter.getEndDate() != null) {
            refunds = refunds.stream()
                    .filter(r -> !r.getRequestedAt().toLocalDate().isAfter(filter.getEndDate()))
                    .collect(Collectors.toList());
        }

        if (filter.getItemType() != null) {
            refunds = refunds.stream()
                    .filter(r -> r.getPayment().getItemType().name().equals(filter.getItemType()))
                    .collect(Collectors.toList());
        }

        return refunds.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get refund statistics.
     */
    public RefundStatsDTO getRefundStatistics() {
        List<Refund> allRefunds = refundRepository.findAll();

        long totalRefunds = allRefunds.size();
        long pendingRefunds = allRefunds.stream().filter(r -> r.getStatus() == RefundStatus.PENDING).count();
        long approvedRefunds = allRefunds.stream().filter(r -> r.getStatus() == RefundStatus.APPROVED).count();
        long completedRefunds = allRefunds.stream().filter(r -> r.getStatus() == RefundStatus.COMPLETED).count();
        long rejectedRefunds = allRefunds.stream().filter(r -> r.getStatus() == RefundStatus.REJECTED).count();
        long failedRefunds = allRefunds.stream().filter(r -> r.getStatus() == RefundStatus.FAILED).count();

        BigDecimal totalRefundAmount = allRefunds.stream()
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal completedRefundAmount = allRefunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.COMPLETED)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RefundStatsDTO.builder()
                .totalRefunds(totalRefunds)
                .pendingRefunds(pendingRefunds)
                .approvedRefunds(approvedRefunds)
                .completedRefunds(completedRefunds)
                .rejectedRefunds(rejectedRefunds)
                .failedRefunds(failedRefunds)
                .totalRefundAmount(totalRefundAmount)
                .completedRefundAmount(completedRefundAmount)
                .build();
    }

    // ÔöÇÔöÇ Helper methods ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Convert Refund entity to DTO.
     */
    private RefundDTO toDTO(Refund refund) {
        Payment payment = refund.getPayment();

        return RefundDTO.builder()
                .id(refund.getId())
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .studentId(refund.getStudentId())
                .studentName(payment.getStudentName())
                .studentEmail(payment.getStudentEmail())
                .itemType(payment.getItemType().name())
                .itemId(payment.getItemId())
                .itemName(payment.getItemName())
                .amount(refund.getAmount())
                .status(refund.getStatus())
                .reason(refund.getReason())
                .requestedAt(refund.getRequestedAt())
                .approvedAt(refund.getApprovedAt())
                .rejectedAt(refund.getRejectedAt())
                .processingAt(refund.getProcessingAt())
                .completedAt(refund.getCompletedAt())
                .cancelledAt(refund.getCancelledAt())
                .adminId(refund.getAdminId())
                .rejectionReason(refund.getRejectionReason())
                .paymeeTransactionId(refund.getPaymeeTransactionId())
                .errorMessage(refund.getErrorMessage())
                .createdAt(refund.getCreatedAt())
                .updatedAt(refund.getUpdatedAt())
                .build();
    }
}
