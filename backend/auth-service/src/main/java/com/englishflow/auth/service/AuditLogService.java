package com.englishflow.auth.service;

import com.englishflow.auth.entity.AuditLog;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Log an audit event asynchronously
     */
    @Async
    @Transactional
    public void logEvent(AuditLog.AuditAction action, AuditLog.AuditStatus status, 
                        Long userId, String userEmail, String details, 
                        HttpServletRequest request) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .status(status)
                    .userId(userId)
                    .userEmail(userEmail)
                    .details(details)
                    .ipAddress(extractIpAddress(request))
                    .userAgent(extractUserAgent(request))
                    .sessionId(extractSessionId(request))
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit event logged: {} - {} - {}", action, status, userEmail);
        } catch (Exception e) {
            log.error("Failed to log audit event: {} - {}", action, e.getMessage());
        }
    }

    /**
     * Log successful authentication
     */
    public void logLoginSuccess(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.LOGIN_SUCCESS, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(), 
                String.format("User %s logged in successfully", user.getEmail()),
                request);
    }

    /**
     * Log failed authentication
     */
    public void logLoginFailed(String email, String reason, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.LOGIN_FAILED, AuditLog.AuditStatus.FAILED,
                null, email, 
                String.format("Login failed for %s: %s", email, reason),
                request);
    }

    /**
     * Log logout
     */
    public void logLogout(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.LOGOUT, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("User %s logged out", user.getEmail()),
                request);
    }

    /**
     * Log logout from all devices
     */
    public void logLogoutAllDevices(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.LOGOUT_ALL_DEVICES, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("User %s logged out from all devices", user.getEmail()),
                request);
    }

    /**
     * Log registration
     */
    public void logRegistration(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.REGISTER_SUCCESS, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("User %s registered with role %s", user.getEmail(), user.getRole()),
                request);
    }

    /**
     * Log account activation
     */
    public void logAccountActivation(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.ACCOUNT_ACTIVATION, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("Account %s activated", user.getEmail()),
                request);
    }

    /**
     * Log password reset request
     */
    public void logPasswordResetRequest(String email, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.PASSWORD_RESET_REQUEST, AuditLog.AuditStatus.SUCCESS,
                null, email,
                String.format("Password reset requested for %s", email),
                request);
    }

    /**
     * Log password reset success
     */
    public void logPasswordResetSuccess(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.PASSWORD_RESET_SUCCESS, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("Password reset completed for %s", user.getEmail()),
                request);
    }

    /**
     * Log token refresh
     */
    public void logTokenRefresh(User user, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.TOKEN_REFRESH, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("Token refreshed for %s", user.getEmail()),
                request);
    }

    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(String email, String reason, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.SUSPICIOUS_LOGIN_ATTEMPT, AuditLog.AuditStatus.WARNING,
                null, email,
                String.format("Suspicious activity detected for %s: %s", email, reason),
                request);
    }

    /**
     * Log rate limit exceeded
     */
    public void logRateLimitExceeded(String email, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.RATE_LIMIT_EXCEEDED, AuditLog.AuditStatus.WARNING,
                null, email,
                String.format("Rate limit exceeded for %s", email),
                request);
    }

    /**
     * Log profile update
     */
    public void logProfileUpdate(User user, String changes, HttpServletRequest request) {
        logEvent(AuditLog.AuditAction.PROFILE_UPDATE, AuditLog.AuditStatus.SUCCESS,
                user.getId(), user.getEmail(),
                String.format("Profile updated for %s: %s", user.getEmail(), changes),
                request);
    }

    /**
     * Get audit logs with pagination
     */
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    /**
     * Get audit logs for a specific user
     */
    public Page<AuditLog> getUserAuditLogs(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Search audit logs with filters
     */
    public Page<AuditLog> searchAuditLogs(Long userId, String email, 
                                         AuditLog.AuditAction action, AuditLog.AuditStatus status,
                                         String ipAddress, LocalDateTime startDate, 
                                         LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, email, action, status, 
                                                 ipAddress, startDate, endDate, pageable);
    }

    /**
     * Get recent security events
     */
    public List<AuditLog> getRecentSecurityEvents(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findRecentSecurityEvents(since);
    }

    /**
     * Get audit statistics for dashboard
     */
    public Map<String, Long> getAuditStatistics(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Object[]> results = auditLogRepository.getAuditStatistics(since);
        
        return results.stream()
                .collect(Collectors.toMap(
                    row -> ((AuditLog.AuditAction) row[0]).name(),
                    row -> (Long) row[1]
                ));
    }

    /**
     * Check for suspicious login patterns
     */
    public boolean isSuspiciousLoginPattern(String email, String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        // Check for multiple failed logins
        List<AuditLog> failedLogins = auditLogRepository.findFailedLoginAttempts(email, oneHourAgo);
        if (failedLogins.size() >= 5) {
            return true;
        }
        
        // Check for suspicious activities from same IP
        List<AuditLog> suspiciousActivities = auditLogRepository.findSuspiciousActivities(ipAddress, oneHourAgo);
        return suspiciousActivities.size() >= 10;
    }

    /**
     * Cleanup old audit logs (older than specified days)
     */
    @Transactional
    public void cleanupOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        auditLogRepository.deleteOldLogs(cutoffDate);
        log.info("Cleaned up audit logs older than {} days", daysToKeep);
    }

    /**
     * Extract IP address from request
     */
    private String extractIpAddress(HttpServletRequest request) {
        if (request == null) return "unknown";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Extract user agent from request
     */
    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) return "unknown";
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "unknown";
    }

    /**
     * Extract session ID from request
     */
    private String extractSessionId(HttpServletRequest request) {
        if (request == null) return null;
        return request.getSession(false) != null ? request.getSession().getId() : null;
    }
}