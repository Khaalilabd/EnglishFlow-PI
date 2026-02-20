package com.englishflow.auth.dto;

import com.englishflow.auth.entity.UserSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionResponse {
    
    private Long id;
    private Long userId;
    private String userName;  // Nom complet de l'utilisateur
    private String userEmail; // Email de l'utilisateur
    private String sessionToken;
    private String deviceInfo;
    private String browserName;
    private String browserVersion;
    private String operatingSystem;
    private String deviceType;
    private String ipAddress;
    private String country;
    private String city;
    private String isp;
    private String status;
    private LocalDateTime lastActivity;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime terminatedAt;
    private String terminationReason;
    private boolean suspicious;
    private String suspiciousReasons;
    private int loginCount;
    private LocalDateTime lastLoginAt;
    
    // Helper fields for frontend display
    private String statusDisplayName;
    private String deviceDisplayName;
    private String locationDisplayName;
    private String durationDisplayName;
    private String inactivityDisplayName;
    private boolean isCurrentSession;
    private String riskLevel;
    
    public static UserSessionResponse fromEntity(UserSession session) {
        return UserSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .sessionToken(maskSessionToken(session.getSessionToken()))
                .deviceInfo(session.getDeviceInfo())
                .browserName(session.getBrowserName())
                .browserVersion(session.getBrowserVersion())
                .operatingSystem(session.getOperatingSystem())
                .deviceType(session.getDeviceType())
                .ipAddress(session.getIpAddress())
                .country(session.getCountry())
                .city(session.getCity())
                .isp(session.getIsp())
                .status(session.getStatus().name())
                .lastActivity(session.getLastActivity())
                .createdAt(session.getCreatedAt())
                .expiresAt(session.getExpiresAt())
                .terminatedAt(session.getTerminatedAt())
                .terminationReason(session.getTerminationReason() != null ? 
                    session.getTerminationReason().name() : null)
                .suspicious(session.isSuspicious())
                .suspiciousReasons(session.getSuspiciousReasons())
                .loginCount(session.getLoginCount())
                .lastLoginAt(session.getLastLoginAt())
                .statusDisplayName(getStatusDisplayName(session.getStatus()))
                .deviceDisplayName(getDeviceDisplayName(session))
                .locationDisplayName(getLocationDisplayName(session))
                .durationDisplayName(getDurationDisplayName(session))
                .inactivityDisplayName(getInactivityDisplayName(session))
                .riskLevel(getRiskLevel(session))
                .build();
    }
    
    public static UserSessionResponse fromEntityWithCurrentFlag(UserSession session, String currentSessionToken) {
        UserSessionResponse response = fromEntity(session);
        response.setCurrentSession(session.getSessionToken().equals(currentSessionToken));
        return response;
    }
    
    private static String maskSessionToken(String token) {
        if (token == null || token.length() < 8) {
            return "****";
        }
        return token.substring(0, 4) + "****" + token.substring(token.length() - 4);
    }
    
    private static String getStatusDisplayName(UserSession.SessionStatus status) {
        return switch (status) {
            case ACTIVE -> "Actif";
            case INACTIVE -> "Inactif";
            case EXPIRED -> "Expiré";
            case TERMINATED -> "Terminé";
            case SUSPICIOUS -> "Suspect";
        };
    }
    
    private static String getDeviceDisplayName(UserSession session) {
        StringBuilder device = new StringBuilder();
        
        if (session.getBrowserName() != null) {
            device.append(session.getBrowserName());
            if (session.getBrowserVersion() != null) {
                device.append(" ").append(session.getBrowserVersion());
            }
        }
        
        if (session.getOperatingSystem() != null) {
            if (device.length() > 0) {
                device.append(" sur ");
            }
            device.append(session.getOperatingSystem());
        }
        
        if (session.getDeviceType() != null) {
            String deviceTypeDisplay = switch (session.getDeviceType()) {
                case "DESKTOP" -> "Ordinateur";
                case "MOBILE" -> "Mobile";
                case "TABLET" -> "Tablette";
                default -> session.getDeviceType();
            };
            
            if (device.length() > 0) {
                device.append(" (").append(deviceTypeDisplay).append(")");
            } else {
                device.append(deviceTypeDisplay);
            }
        }
        
        return device.length() > 0 ? device.toString() : "Appareil inconnu";
    }
    
    private static String getLocationDisplayName(UserSession session) {
        StringBuilder location = new StringBuilder();
        
        // Handle localhost/local IPs
        String ipAddress = session.getIpAddress();
        boolean isLocalhost = ipAddress != null && 
            (ipAddress.equals("127.0.0.1") || 
             ipAddress.equals("::1") || 
             ipAddress.equals("0:0:0:0:0:0:0:1") ||
             ipAddress.startsWith("192.168.") ||
             ipAddress.startsWith("10.") ||
             ipAddress.startsWith("172."));
        
        if (isLocalhost) {
            return "Connexion locale (" + ipAddress + ")";
        }
        
        if (session.getCity() != null && !session.getCity().equals("Unknown")) {
            location.append(session.getCity());
        }
        
        if (session.getCountry() != null && !session.getCountry().equals("Unknown")) {
            if (location.length() > 0) {
                location.append(", ");
            }
            location.append(session.getCountry());
        }
        
        if (ipAddress != null) {
            if (location.length() > 0) {
                location.append(" (").append(ipAddress).append(")");
            } else {
                location.append(ipAddress);
            }
        }
        
        return location.length() > 0 ? location.toString() : "Localisation inconnue";
    }
    
    private static String getDurationDisplayName(UserSession session) {
        long minutes = session.getDurationInMinutes();
        
        if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (minutes < 1440) { // Less than 24 hours
            long hours = minutes / 60;
            return hours + " heure" + (hours > 1 ? "s" : "");
        } else {
            long days = minutes / 1440;
            return days + " jour" + (days > 1 ? "s" : "");
        }
    }
    
    private static String getInactivityDisplayName(UserSession session) {
        if (session.getStatus() != UserSession.SessionStatus.ACTIVE) {
            return "N/A";
        }
        
        long minutes = session.getInactivityInMinutes();
        
        if (minutes < 1) {
            return "Maintenant";
        } else if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (minutes < 1440) {
            long hours = minutes / 60;
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else {
            long days = minutes / 1440;
            return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
        }
    }
    
    private static String getRiskLevel(UserSession session) {
        if (session.isSuspicious()) {
            return "HIGH";
        }
        
        // Check for other risk factors
        long inactivityMinutes = session.getInactivityInMinutes();
        
        // High risk: Inactive for more than 2 hours but still active
        if (session.getStatus() == UserSession.SessionStatus.ACTIVE && inactivityMinutes > 120) {
            return "MEDIUM";
        }
        
        // Check for unusual device type or location
        if (session.getCountry() != null && session.getCountry().equals("Unknown")) {
            return "MEDIUM";
        }
        
        return "LOW";
    }
    
    // Helper method to format dates for display
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    public String getFormattedLastActivity() {
        if (lastActivity == null) return "";
        return lastActivity.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
    
    public String getFormattedExpiresAt() {
        if (expiresAt == null) return "";
        return expiresAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}