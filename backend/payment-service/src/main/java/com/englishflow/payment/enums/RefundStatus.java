package com.englishflow.payment.enums;

/**
 * Enum representing the status of a refund request.
 * 
 * Status transitions:
 * - PENDING: Initial state after student creates refund request
 * - APPROVED: Admin has approved the refund, awaiting processing
 * - REJECTED: Admin has rejected the refund request
 * - PROCESSING: Refund is being processed with Paymee gateway
 * - COMPLETED: Refund successfully processed and completed
 * - FAILED: Refund processing failed at Paymee gateway
 * - CANCELLED: Student cancelled the refund request
 */
public enum RefundStatus {
    PENDING,
    APPROVED,
    REJECTED,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}
