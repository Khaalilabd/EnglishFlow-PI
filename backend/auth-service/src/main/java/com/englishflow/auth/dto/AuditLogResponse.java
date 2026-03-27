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
public class AuditLogResponse {
    
    private Long id;
    private Long userId;
    private String userEmail;
    private String action;
    private String resourceType;
    private String resourceId;
    private String details;
    private String ipAddress;
    private String userAgent;
    private String status;
    private String errorMessage;
    private String sessionId;
    private LocalDateTime createdAt;
    
    // Helper fields for frontend display
    private String actionDisplayName;
    private String statusDisplayName;
    private String timeAgo;
    private String riskLevel;
    
    public static AuditLogResponse fromEntity(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .userEmail(auditLog.getUserEmail())
                .action(auditLog.getAction().name())
                .resourceType(auditLog.getResourceType())
                .resourceId(auditLog.getResourceId())
                .details(auditLog.getDetails())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .status(auditLog.getStatus().name())
                .errorMessage(auditLog.getErrorMessage())
                .sessionId(auditLog.getSessionId())
                .createdAt(auditLog.getCreatedAt())
                .actionDisplayName(getActionDisplayName(auditLog.getAction()))
                .statusDisplayName(getStatusDisplayName(auditLog.getStatus()))
                .riskLevel(getRiskLevel(auditLog.getAction(), auditLog.getStatus()))
                .build();
    }
    
    private static String getActionDisplayName(AuditLog.AuditAction action) {
        return switch (action) {
            case LOGIN_SUCCESS -> "Connexion réussie";
            case LOGIN_FAILED -> "Échec de connexion";
            case LOGOUT -> "Déconnexion";
            case LOGOUT_ALL_DEVICES -> "Déconnexion de tous les appareils";
            case REGISTER_SUCCESS -> "Inscription réussie";
            case REGISTER_FAILED -> "Échec d'inscription";
            case ACCOUNT_ACTIVATION -> "Activation de compte";
            case PASSWORD_RESET_REQUEST -> "Demande de réinitialisation";
            case PASSWORD_RESET_SUCCESS -> "Réinitialisation réussie";
            case PASSWORD_RESET_FAILED -> "Échec de réinitialisation";
            case PASSWORD_CHANGE -> "Changement de mot de passe";
            case TOKEN_REFRESH -> "Actualisation de token";
            case TOKEN_REVOKED -> "Révocation de token";
            case PROFILE_UPDATE -> "Mise à jour du profil";
            case PROFILE_PHOTO_UPLOAD -> "Upload photo de profil";
            case PROFILE_COMPLETION -> "Complétion du profil";
            case INVITATION_SENT -> "Invitation envoyée";
            case INVITATION_ACCEPTED -> "Invitation acceptée";
            case INVITATION_CANCELLED -> "Invitation annulée";
            case INVITATION_EXPIRED -> "Invitation expirée";
            case USER_CREATED -> "Utilisateur créé";
            case USER_UPDATED -> "Utilisateur modifié";
            case USER_DELETED -> "Utilisateur supprimé";
            case USER_ACTIVATED -> "Utilisateur activé";
            case USER_DEACTIVATED -> "Utilisateur désactivé";
            case ROLE_CHANGED -> "Rôle modifié";
            case SUSPICIOUS_LOGIN_ATTEMPT -> "Tentative de connexion suspecte";
            case RATE_LIMIT_EXCEEDED -> "Limite de taux dépassée";
            case INVALID_TOKEN_USED -> "Token invalide utilisé";
            case MULTIPLE_FAILED_LOGINS -> "Multiples échecs de connexion";
            case DATA_EXPORT -> "Export de données";
            case DATA_IMPORT -> "Import de données";
            case SYSTEM_CONFIG_CHANGE -> "Changement de configuration";
        };
    }
    
    private static String getStatusDisplayName(AuditLog.AuditStatus status) {
        return switch (status) {
            case SUCCESS -> "Succès";
            case FAILED -> "Échec";
            case WARNING -> "Avertissement";
            case INFO -> "Information";
        };
    }
    
    private static String getRiskLevel(AuditLog.AuditAction action, AuditLog.AuditStatus status) {
        // High risk actions
        if (action == AuditLog.AuditAction.SUSPICIOUS_LOGIN_ATTEMPT ||
            action == AuditLog.AuditAction.MULTIPLE_FAILED_LOGINS ||
            action == AuditLog.AuditAction.INVALID_TOKEN_USED ||
            action == AuditLog.AuditAction.RATE_LIMIT_EXCEEDED) {
            return "HIGH";
        }
        
        // Medium risk actions
        if (status == AuditLog.AuditStatus.FAILED ||
            action == AuditLog.AuditAction.LOGIN_FAILED ||
            action == AuditLog.AuditAction.PASSWORD_RESET_FAILED ||
            action == AuditLog.AuditAction.LOGOUT_ALL_DEVICES) {
            return "MEDIUM";
        }
        
        // Low risk actions
        return "LOW";
    }
}