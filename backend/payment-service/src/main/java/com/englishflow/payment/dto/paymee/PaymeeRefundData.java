package com.englishflow.payment.dto.paymee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Paymee refund response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymeeRefundData {
    
    /**
     * Refund transaction ID from Paymee
     */
    private String refund_transaction_id;
    
    /**
     * Refund status from Paymee
     */
    private String status;
    
    /**
     * Refunded amount
     */
    private BigDecimal amount;
}
