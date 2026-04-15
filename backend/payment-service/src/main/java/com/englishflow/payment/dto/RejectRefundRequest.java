package com.englishflow.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for rejecting a refund request.
 * Used by administrators to provide a rejection reason.
 */
@Data
public class RejectRefundRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(max = 1000, message = "Rejection reason must not exceed 1000 characters")
    private String reason;
}
