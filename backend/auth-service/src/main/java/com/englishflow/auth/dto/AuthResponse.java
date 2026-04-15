package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String sessionToken; // Session token for tracking user sessions
    @Builder.Default
    private String type = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String profilePhoto;
    private String phone;
    private Boolean profileCompleted;
    private String englishLevel; // English proficiency level (A1, A2, B1, B2, C1, C2)
    private long expiresIn; // Access token expiry in seconds
    private LocalDateTime refreshTokenExpiryDate;
    
    // 2FA fields
    private Boolean requires2FA; // Indicates if 2FA verification is required
    private String tempToken; // Temporary token for 2FA verification (short-lived)
    
    // Password change requirement
    private Boolean mustChangePassword; // Indicates if user must change password on first login

    public AuthResponse(String token, Long id, String email, String firstName, String lastName, String role, String profilePhoto, String phone) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.profileCompleted = false; // Default value
        this.englishLevel = null; // Default value
    }
    
    public AuthResponse(String token, Long id, String email, String firstName, String lastName, String role, String profilePhoto, String phone, Boolean profileCompleted) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.profileCompleted = profileCompleted;
        this.englishLevel = null; // Default value
    }

    public AuthResponse(String token, Long id, String email, String firstName, String lastName, String role, String profilePhoto, String phone, Boolean profileCompleted, String englishLevel) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.profilePhoto = profilePhoto;
        this.phone = phone;
        this.profileCompleted = profileCompleted;
        this.englishLevel = englishLevel;
    }
}
