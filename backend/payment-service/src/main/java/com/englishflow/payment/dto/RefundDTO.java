package com.englishflow.payment.dto;

import com.englishflow.payment.enums.RefundStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for refund responses.
 * Includes all refund fields plus payment and student details for comprehensive display.
 */
@Data
@Builder
public class RefundDTO {
    private Long id;
    private Long paymentId;
    private String orderId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String itemType;
    private Long itemId;
    private String itemName;
    private BigDecimal amount;
    private RefundStatus status;
    private String reason;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private LocalDateTime processingAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private Long adminId;
    private String rejectionReason;
    private String paymeeTransactionId;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
