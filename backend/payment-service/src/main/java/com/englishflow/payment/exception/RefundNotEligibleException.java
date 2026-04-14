package com.englishflow.payment.exception;

/**
 * Exception thrown when a refund request is not eligible.
 * This can occur due to expired refund window, excessive progress, or other eligibility criteria.
 */
public class RefundNotEligibleException extends RuntimeException {
    
    public RefundNotEligibleException(String message) {
        super(message);
    }
    
    public RefundNotEligibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
