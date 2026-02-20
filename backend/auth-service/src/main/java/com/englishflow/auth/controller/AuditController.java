package com.englishflow.auth.controller;

import com.englishflow.auth.dto.AuditLogResponse;
import com.englishflow.auth.dto.AuditSearchRequest;
import com.englishflow.auth.entity.AuditLog;
import com.englishflow.auth.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditLogService auditLogService;

    /**
     * Get all audit logs with pagination and filtering
     */
    @PostMapping("/search")
    public ResponseEntity<Page<AuditLogResponse>> searchAuditLogs(@RequestBody AuditSearchRequest request) {
        // Apply quick filter if specified
        request.applyQuickFilter();
        
        // Create pageable with sorting
        Sort sort = Sort.by(
            request.getSortDirection().equalsIgnoreCase("ASC") ? 
            Sort.Direction.ASC : Sort.Direction.DESC,
            request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        
        // Search audit logs
        Page<AuditLog> auditLogs = auditLogService.searchAuditLogs(
            request.getUserId(),
            request.getUserEmail(),
            request.getAction(),
            request.getStatus(),
            request.getIpAddress(),
            request.getStartDate(),
            request.getEndDate(),
            pageable
        );
        
        // Convert to response DTOs
        Page<AuditLogResponse> response = auditLogs.map(AuditLogResponse::fromEntity);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get audit logs for a specific user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AuditLogResponse>> getUserAuditLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> auditLogs = auditLogService.getUserAuditLogs(userId, pageable);
        Page<AuditLogResponse> response = auditLogs.map(AuditLogResponse::fromEntity);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent security events
     */
    @GetMapping("/security-events")
    public ResponseEntity<List<AuditLogResponse>> getRecentSecurityEvents(
            @RequestParam(defaultValue = "24") int hours) {
        
        List<AuditLog> securityEvents = auditLogService.getRecentSecurityEvents(hours);
        List<AuditLogResponse> response = securityEvents.stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get audit statistics for dashboard
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAuditStatistics(
            @RequestParam(defaultValue = "30") int days) {
        
        Map<String, Long> actionStats = auditLogService.getAuditStatistics(days);
        List<AuditLog> recentSecurityEvents = auditLogService.getRecentSecurityEvents(24);
        
        Map<String, Object> statistics = Map.of(
            "actionStatistics", actionStats,
            "recentSecurityEvents", recentSecurityEvents.size(),
            "totalEvents", actionStats.values().stream().mapToLong(Long::longValue).sum(),
            "period", days + " days"
        );
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get available filter options
     */
    @GetMapping("/filters")
    public ResponseEntity<Map<String, Object>> getFilterOptions() {
        Map<String, Object> filters = Map.of(
            "actions", AuditLog.AuditAction.values(),
            "statuses", AuditLog.AuditStatus.values(),
            "quickFilters", List.of("TODAY", "YESTERDAY", "LAST_WEEK", "LAST_MONTH"),
            "riskLevels", List.of("HIGH", "MEDIUM", "LOW")
        );
        
        return ResponseEntity.ok(filters);
    }

    /**
     * Cleanup old audit logs (admin only)
     */
    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> cleanupOldLogs(
            @RequestParam(defaultValue = "365") int daysToKeep) {
        
        auditLogService.cleanupOldLogs(daysToKeep);
        
        return ResponseEntity.ok(Map.of(
            "message", "Audit logs older than " + daysToKeep + " days have been cleaned up"
        ));
    }

    /**
     * Export audit logs (for compliance)
     */
    @PostMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> exportAuditLogs(@RequestBody AuditSearchRequest request) {
        // Apply quick filter if specified
        request.applyQuickFilter();
        
        // Set large page size for export (max 10000 records)
        request.setSize(Math.min(request.getSize(), 10000));
        
        Pageable pageable = PageRequest.of(0, request.getSize(), 
            Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<AuditLog> auditLogs = auditLogService.searchAuditLogs(
            request.getUserId(),
            request.getUserEmail(),
            request.getAction(),
            request.getStatus(),
            request.getIpAddress(),
            request.getStartDate(),
            request.getEndDate(),
            pageable
        );
        
        List<AuditLogResponse> response = auditLogs.getContent().stream()
                .map(AuditLogResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
}