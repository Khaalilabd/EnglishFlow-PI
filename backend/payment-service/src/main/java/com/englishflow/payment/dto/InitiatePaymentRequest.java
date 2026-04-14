package com.englishflow.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitiatePaymentRequest {

    @NotNull
    private Long studentId;

    @NotBlank
    private String studentName;

    @NotBlank
    private String studentEmail;

    private String studentPhone;

    /** "COURSE" or "PACK" */
    @NotBlank
    private String itemType;

    @NotNull
    private Long itemId;

    @NotBlank
    private String itemName;

    @NotNull
    private BigDecimal amount;
}
