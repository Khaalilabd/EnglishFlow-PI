package com.englishflow.payment.dto.paymee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Paymee refund API request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymeeRefundRequest {
    
    /**
     * Amount to refund
     */
    private BigDecimal amount;
    
    /**
     * Original payment transaction ID from Paymee
     */
    private String transaction_id;
}
