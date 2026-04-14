package com.englishflow.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentDTO {
    private Long id;
    private String orderId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String itemType;
    private Long itemId;
    private String itemName;
    private BigDecimal amount;
    private String status;
    private Long transactionId;
    private BigDecimal receivedAmount;
    private BigDecimal cost;
    private String paymentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
