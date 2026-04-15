package com.englishflow.payment.exception;

/**
 * Exception thrown when a refund is not found.
 */
public class RefundNotFoundException extends RuntimeException {
    
    public RefundNotFoundException(String message) {
        super(message);
    }
    
    public RefundNotFoundException(Long refundId) {
        super("Refund not found: " + refundId);
    }
    
    public RefundNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
