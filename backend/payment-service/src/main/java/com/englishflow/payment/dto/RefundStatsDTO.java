package com.englishflow.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for refund statistics.
 * Provides aggregate data about refunds for administrative dashboards.
 */
@Data
@Builder
public class RefundStatsDTO {
    private Long totalRefunds;
    private Long pendingRefunds;
    private Long approvedRefunds;
    private Long completedRefunds;
    private Long rejectedRefunds;
    private Long failedRefunds;
    private BigDecimal totalRefundAmount;
    private BigDecimal completedRefundAmount;
}
