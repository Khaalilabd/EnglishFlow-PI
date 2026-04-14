package com.englishflow.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating a new refund request.
 * Used by students to request refunds for their payments.
 */
@Data
public class CreateRefundRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}
