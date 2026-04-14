package com.englishflow.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentStatsDTO {
    private long totalPayments;
    private long successfulPayments;
    private long pendingPayments;
    private long failedPayments;
    private BigDecimal totalRevenue;
}
