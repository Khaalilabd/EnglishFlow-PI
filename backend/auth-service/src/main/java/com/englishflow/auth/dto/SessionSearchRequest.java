package com.englishflow.auth.dto;

import com.englishflow.auth.entity.UserSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionSearchRequest {
    
    private Long userId;
    private UserSession.SessionStatus status;
    private String deviceType;
    private String ipAddress;
    private String country;
    private Boolean suspicious;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Pagination
    private int page = 0;
    private int size = 20;
    private String sortBy = "lastActivity";
    private String sortDirection = "DESC";
    
    // Quick filters
    private String quickFilter; // TODAY, YESTERDAY, LAST_WEEK, LAST_MONTH, ACTIVE_ONLY, SUSPICIOUS_ONLY
    
    // Additional filters
    private String browserName;
    private String operatingSystem;
    private String riskLevel; // HIGH, MEDIUM, LOW
    
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
                case "ACTIVE_ONLY":
                    status = UserSession.SessionStatus.ACTIVE;
                    break;
                case "SUSPICIOUS_ONLY":
                    suspicious = true;
                    break;
            }
        }
    }
}