package com.englishflow.payment.dto;

import com.englishflow.payment.enums.RefundStatus;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for filtering refund queries.
 * Used by administrators to filter refund lists by various criteria.
 */
@Data
public class RefundFilterDTO {
    private RefundStatus status;
    private Long studentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String itemType;

    /**
     * Check if any filters are set.
     *
     * @return true if at least one filter is set, false otherwise
     */
    public boolean hasFilters() {
        return status != null || studentId != null || startDate != null || endDate != null || itemType != null;
    }
}
