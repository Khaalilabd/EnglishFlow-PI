package com.englishflow.auth.service;

import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.PasswordResetRequest;
import com.englishflow.auth.dto.PasswordResetConfirm;
import com.englishflow.auth.dto.RefreshTokenRequest;
import com.englishflow.auth.dto.RefreshTokenResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.PasswordResetToken;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.RefreshToken;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.repository.PasswordResetTokenRepository;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service("authServiceWithAudit")
@RequiredArgsConstructor
public class AuthServiceWithAudit {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final RateLimitService rateLimitService;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        try {
            if (userRepository.existsByEmail(request.getEmail())) {
                auditLogService.logEvent(
                    com.englishflow.auth.entity.AuditLog.AuditAction.REGISTER_FAILED,
                    com.englishflow.auth.entity.AuditLog.AuditStatus.FAILED,
                    null, request.getEmail(),
                    "Registration failed: Email already exists",
                    httpRequest
                );
                throw new RuntimeException("Email already exists");
            }

            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhone(request.getPhone());
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
            user.setActive(false);
            user.setRegistrationFeePaid(false);
            
            // Set optional fields
            if (request.getCin() != null && !request.getCin().isEmpty()) {
                user.setCin(request.getCin());
            }
            if (request.getProfilePhoto() != null && !request.getProfilePhoto().isEmpty()) {
                user.setProfilePhoto(request.getProfilePhoto());
            }
            if (request.getDateOfBirth() != null) {
                user.setDateOfBirth(request.getDateOfBirth().toString());
            }
            if (request.getAddress() != null && !request.getAddress().isEmpty()) {
                user.setAddress(request.getAddress());
            }
            if (request.getCity() != null && !request.getCity().isEmpty()) {
                user.setCity(request.getCity());
            }
            if (request.getPostalCode() != null && !request.getPostalCode().isEmpty()) {
                user.setPostalCode(request.getPostalCode());
            }
            if (request.getBio() != null && !request.getBio().isEmpty()) {
                user.setBio(request.getBio());
            }
            if (request.getEnglishLevel() != null && !request.getEnglishLevel().isEmpty()) {
                user.setEnglishLevel(request.getEnglishLevel());
            }
            if (request.getYearsOfExperience() != null) {
                user.setYearsOfExperience(request.getYearsOfExperience());
            }
            
            // Mark profile as complete if phone and CIN are provided
            if (request.getPhone() != null && !request.getPhone().isEmpty() && 
                request.getCin() != null && !request.getCin().isEmpty()) {
                user.setProfileCompleted(true);
            } else {
                user.setProfileCompleted(false);
            }

            user = userRepository.save(user);

            // Create activation token
            String activationToken = UUID.randomUUID().toString();
            ActivationToken token = ActivationToken.builder()
                    .token(activationToken)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusHours(24))
                    .used(false)
                    .build();
            activationTokenRepository.save(token);

            // Send activation email
            try {
                emailService.sendActivationEmail(user.getEmail(), user.getFirstName(), activationToken);
            } catch (Exception e) {
                System.err.println("Failed to send activation email: " + e.getMessage());
            }

            // Log successful registration
            auditLogService.logRegistration(user, httpRequest);

            return new AuthResponse(
                    null, // No token until activation
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole().name(),
                    user.getProfilePhoto(),
                    user.getPhone()
            );
        } catch (RuntimeException e) {
            auditLogService.logEvent(
                com.englishflow.auth.entity.AuditLog.AuditAction.REGISTER_FAILED,
                com.englishflow.auth.entity.AuditLog.AuditStatus.FAILED,
                null, request.getEmail(),
                "Registration failed: " + e.getMessage(),
                httpRequest
            );
            throw e;
        }
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        // Check rate limit
        if (rateLimitService.isBlocked(request.getEmail())) {
            auditLogService.logRateLimitExceeded(request.getEmail(), httpRequest);
            throw new RuntimeException("Too many failed login attempts. Please try again in 15 minutes.");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    rateLimitService.recordFailedAttempt(request.getEmail());
                    auditLogService.logLoginFailed(request.getEmail(), "User not found", httpRequest);
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            rateLimitService.recordFailedAttempt(request.getEmail());
            auditLogService.logLoginFailed(request.getEmail(), "Invalid password", httpRequest);
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            auditLogService.logLoginFailed(request.getEmail(), "Account not activated", httpRequest);
            throw new RuntimeException("Account not activated. Please check your email.");
        }

        // Check for suspicious login patterns
        String ipAddress = extractIpAddress(httpRequest);
        if (auditLogService.isSuspiciousLoginPattern(request.getEmail(), ipAddress)) {
            auditLogService.logSuspiciousActivity(request.getEmail(), 
                "Multiple failed login attempts detected", httpRequest);
        }

        // Reset rate limit on successful login
        rateLimitService.resetAttempts(request.getEmail());

        // Log successful login
        auditLogService.logLoginSuccess(user, httpRequest);

        // Create AuthResponse with refresh token
        return createAuthResponse(user, httpRequest);
    }

    @Transactional
    public AuthResponse activateAccount(String token, HttpServletRequest httpRequest) {
        ActivationToken activationToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid activation token"));

        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Activation token has expired");
        }

        if (activationToken.isUsed()) {
            throw new RuntimeException("Activation token has already been used");
        }

        User user = activationToken.getUser();
        user.setActive(true);
        userRepository.save(user);

        activationToken.setUsed(true);
        activationTokenRepository.save(activationToken);

        // Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName());

        // Log account activation
        auditLogService.logAccountActivation(user, httpRequest);

        // Generate JWT token
        String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getProfilePhoto(),
                user.getPhone(),
                user.isProfileCompleted()
        );
    }

    @Transactional
    public void requestPasswordReset(PasswordResetRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create new reset token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Log password reset request
        auditLogService.logPasswordResetRequest(request.getEmail(), httpRequest);

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);
    }

    @Transactional
    public void resetPassword(PasswordResetConfirm request, HttpServletRequest httpRequest) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        if (resetToken.getUsed()) {
            throw new RuntimeException("Reset token has already been used");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Log successful password reset
        auditLogService.logPasswordResetSuccess(user, httpRequest);
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String requestRefreshToken = request.getRefreshToken();
        
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        
        User user = userRepository.findById(refreshToken.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        
        // Create new refresh token (token rotation for security)
        String deviceInfo = extractDeviceInfo(httpRequest);
        String ipAddress = extractIpAddress(httpRequest);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId(), deviceInfo, ipAddress);
        
        // Revoke old refresh token
        refreshTokenService.revokeToken(requestRefreshToken);
        
        // Log token refresh
        auditLogService.logTokenRefresh(user, httpRequest);
        
        return new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                "Bearer",
                jwtUtil.getExpirationTimeInSeconds(),
                newRefreshToken.getExpiryDate()
        );
    }

    @Transactional
    public void logout(String refreshToken, HttpServletRequest httpRequest) {
        if (refreshToken != null) {
            // Find user for logging
            refreshTokenService.findByToken(refreshToken).ifPresent(token -> {
                userRepository.findById(token.getUserId()).ifPresent(user -> {
                    auditLogService.logLogout(user, httpRequest);
                });
            });
            
            refreshTokenService.revokeToken(refreshToken);
        }
    }

    @Transactional
    public void logoutFromAllDevices(Long userId, HttpServletRequest httpRequest) {
        // Find user for logging
        userRepository.findById(userId).ifPresent(user -> {
            auditLogService.logLogoutAllDevices(user, httpRequest);
        });
        
        refreshTokenService.revokeAllUserTokens(userId);
    }

    // Helper methods
    private AuthResponse createAuthResponse(User user, HttpServletRequest request) {
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
        
        String deviceInfo = extractDeviceInfo(request);
        String ipAddress = extractIpAddress(request);
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), deviceInfo, ipAddress);
        
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .type("Bearer")
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .profilePhoto(user.getProfilePhoto())
                .phone(user.getPhone())
                .profileCompleted(user.isProfileCompleted())
                .expiresIn(jwtUtil.getExpirationTimeInSeconds())
                .refreshTokenExpiryDate(refreshToken.getExpiryDate())
                .build();
    }

    private String extractDeviceInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown Device";
    }

    private String extractIpAddress(HttpServletRequest request) {
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

    // Delegate methods to maintain compatibility
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public Map<String, Object> checkActivationStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("activated", user.isActive());

        if (user.isActive()) {
            String jwtToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getId());
            response.put("token", jwtToken);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole().name());
            response.put("profilePhoto", user.getProfilePhoto());
            response.put("profileCompleted", user.isProfileCompleted());
            response.put("message", "Account activated successfully!");
        } else {
            response.put("message", "Waiting for account activation...");
        }

        return response;
    }

    @Transactional
    public void completeProfile(Long userId, Map<String, String> profileData, HttpServletRequest httpRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StringBuilder changes = new StringBuilder();
        
        if (profileData.containsKey("phone") && profileData.get("phone") != null) {
            user.setPhone(profileData.get("phone"));
            changes.append("phone, ");
        }
        if (profileData.containsKey("cin") && profileData.get("cin") != null) {
            user.setCin(profileData.get("cin"));
            changes.append("cin, ");
        }
        if (profileData.containsKey("dateOfBirth") && profileData.get("dateOfBirth") != null) {
            user.setDateOfBirth(profileData.get("dateOfBirth"));
            changes.append("dateOfBirth, ");
        }
        if (profileData.containsKey("address") && profileData.get("address") != null) {
            user.setAddress(profileData.get("address"));
            changes.append("address, ");
        }
        if (profileData.containsKey("city") && profileData.get("city") != null) {
            user.setCity(profileData.get("city"));
            changes.append("city, ");
        }
        if (profileData.containsKey("postalCode") && profileData.get("postalCode") != null) {
            user.setPostalCode(profileData.get("postalCode"));
            changes.append("postalCode, ");
        }
        if (profileData.containsKey("bio") && profileData.get("bio") != null) {
            user.setBio(profileData.get("bio"));
            changes.append("bio, ");
        }
        if (profileData.containsKey("englishLevel") && profileData.get("englishLevel") != null) {
            user.setEnglishLevel(profileData.get("englishLevel"));
            changes.append("englishLevel, ");
        }

        user.setProfileCompleted(true);
        userRepository.save(user);

        // Log profile completion
        auditLogService.logEvent(
            com.englishflow.auth.entity.AuditLog.AuditAction.PROFILE_COMPLETION,
            com.englishflow.auth.entity.AuditLog.AuditStatus.SUCCESS,
            user.getId(), user.getEmail(),
            "Profile completed with changes: " + changes.toString(),
            httpRequest
        );
    }
}