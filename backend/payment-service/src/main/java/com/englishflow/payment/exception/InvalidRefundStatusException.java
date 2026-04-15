package com.englishflow.payment.exception;

/**
 * Exception thrown when a refund operation is attempted on a refund with invalid status.
 * For example, trying to approve a refund that is not in PENDING status.
 */
public class InvalidRefundStatusException extends RuntimeException {
    
    public InvalidRefundStatusException(String message) {
        super(message);
    }
    
    public InvalidRefundStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
