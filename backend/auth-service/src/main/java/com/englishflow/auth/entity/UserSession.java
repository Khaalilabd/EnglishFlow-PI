package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_token", unique = true, nullable = false, length = 255)
    private String sessionToken;

    @Column(name = "refresh_token_id")
    private Long refreshTokenId;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "browser_name", length = 100)
    private String browserName;

    @Column(name = "browser_version", length = 50)
    private String browserVersion;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, TABLET

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "isp", length = 200)
    private String isp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "termination_reason", length = 50)
    private TerminationReason terminationReason;

    @Column(name = "is_suspicious", nullable = false)
    private boolean suspicious = false;

    @Column(name = "suspicious_reasons", columnDefinition = "TEXT")
    private String suspiciousReasons;

    @Column(name = "login_count", nullable = false)
    private int loginCount = 1;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActivity = LocalDateTime.now();
        lastLoginAt = LocalDateTime.now();
        if (status == null) {
            status = SessionStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastActivity = LocalDateTime.now();
    }

    public enum SessionStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED,
        TERMINATED,
        SUSPICIOUS
    }

    public enum TerminationReason {
        USER_LOGOUT,
        ADMIN_TERMINATED,
        SECURITY_VIOLATION,
        TOKEN_EXPIRED,
        INACTIVITY_TIMEOUT,
        CONCURRENT_LOGIN_LIMIT,
        SUSPICIOUS_ACTIVITY,
        SYSTEM_MAINTENANCE
    }

    // Helper methods
    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public long getDurationInMinutes() {
        LocalDateTime endTime = terminatedAt != null ? terminatedAt : LocalDateTime.now();
        return java.time.Duration.between(createdAt, endTime).toMinutes();
    }

    public long getInactivityInMinutes() {
        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
    }

    public void markSuspicious(String reason) {
        this.suspicious = true;
        this.suspiciousReasons = reason;
        this.status = SessionStatus.SUSPICIOUS;
    }

    public void terminate(TerminationReason reason) {
        this.status = SessionStatus.TERMINATED;
        this.terminationReason = reason;
        this.terminatedAt = LocalDateTime.now();
    }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
        if (this.status == SessionStatus.INACTIVE) {
            this.status = SessionStatus.ACTIVE;
        }
    }
}