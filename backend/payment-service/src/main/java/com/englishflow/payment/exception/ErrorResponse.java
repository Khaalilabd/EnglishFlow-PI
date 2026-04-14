package com.englishflow.payment.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standard error response structure for API exceptions.
 */
@Data
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
}
