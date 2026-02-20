package com.englishflow.auth.dto;

import com.englishflow.auth.entity.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditSearchRequest {
    
    private Long userId;
    private String userEmail;
    private AuditLog.AuditAction action;
    private AuditLog.AuditStatus status;
    private String ipAddress;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String riskLevel; // HIGH, MEDIUM, LOW
    
    // Pagination
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    
    // Quick filters
    private String quickFilter; // TODAY, YESTERDAY, LAST_WEEK, LAST_MONTH
    
    public void applyQuickFilter() {
        if (quickFilter != null) {
            LocalDateTime now = LocalDateTime.now();
            switch (quickFilter.toUpperCase()) {
                case "TODAY":
                    startDate = now.toLocalDate().atStartOfDay();
                    endDate = now;
                    break;
                case "YESTERDAY":
                    startDate = now.minusDays(1).toLocalDate().atStartOfDay();
                    endDate = now.minusDays(1).toLocalDate().atTime(23, 59, 59);
                    break;
                case "LAST_WEEK":
                    startDate = now.minusWeeks(1);
                    endDate = now;
                    break;
                case "LAST_MONTH":
                    startDate = now.minusMonths(1);
                    endDate = now;
                    break;
            }
        }
    }
}