package com.englishflow.payment.dto.paymee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Paymee refund API response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymeeRefundResponse {
    
    /**
     * Success status of the refund request
     */
    private Boolean status;
    
    /**
     * Response message from Paymee
     */
    private String message;
    
    /**
     * Response code from Paymee
     */
    private Integer code;
    
    /**
     * Refund data containing transaction details
     */
    private PaymeeRefundData data;
}
