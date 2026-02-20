package com.englishflow.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_id", length = 100)
    private String resourceId;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AuditStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AuditAction {
        // Authentication actions
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGOUT,
        LOGOUT_ALL_DEVICES,
        
        // Registration actions
        REGISTER_SUCCESS,
        REGISTER_FAILED,
        ACCOUNT_ACTIVATION,
        
        // Password actions
        PASSWORD_RESET_REQUEST,
        PASSWORD_RESET_SUCCESS,
        PASSWORD_RESET_FAILED,
        PASSWORD_CHANGE,
        
        // Token actions
        TOKEN_REFRESH,
        TOKEN_REVOKED,
        
        // Profile actions
        PROFILE_UPDATE,
        PROFILE_PHOTO_UPLOAD,
        PROFILE_COMPLETION,
        
        // Invitation actions
        INVITATION_SENT,
        INVITATION_ACCEPTED,
        INVITATION_CANCELLED,
        INVITATION_EXPIRED,
        
        // Admin actions
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ACTIVATED,
        USER_DEACTIVATED,
        ROLE_CHANGED,
        
        // Security actions
        SUSPICIOUS_LOGIN_ATTEMPT,
        RATE_LIMIT_EXCEEDED,
        INVALID_TOKEN_USED,
        MULTIPLE_FAILED_LOGINS,
        
        // System actions
        DATA_EXPORT,
        DATA_IMPORT,
        SYSTEM_CONFIG_CHANGE
    }

    public enum AuditStatus {
        SUCCESS,
        FAILED,
        WARNING,
        INFO
    }
}