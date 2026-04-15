package com.englishflow.auth.service;

import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.PasswordResetRequest;
import com.englishflow.auth.dto.PasswordResetConfirm;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.PasswordResetToken;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.repository.PasswordResetTokenRepository;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Mock
    private ActivationTokenRepository activationTokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private EmailService emailService;
    
    @Mock
    private RateLimitService rateLimitService;
    
    @Mock
    private RefreshTokenService refreshTokenService;
    
    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private UserSessionService userSessionService;
    
    @Mock
    private MetricsService metricsService;
    
    @Mock
    private TwoFactorAuthService twoFactorAuthService;
    
    @Mock
    private GamificationIntegrationService gamificationIntegrationService;
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(User.Role.STUDENT);
        testUser.setActive(true);
        
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
        registerRequest.setRole("STUDENT");
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }
    
    @Test
    void testValidateToken() {
        String token = "valid.jwt.token";
        when(jwtUtil.validateToken(token)).thenReturn(true);
        
        boolean isValid = authService.validateToken(token);
        
        assertTrue(isValid);
        verify(jwtUtil).validateToken(token);
    }
    
    @Test
    void testResetPassword() {
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token("reset-token")
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        PasswordResetConfirm request = new PasswordResetConfirm();
        request.setToken("reset-token");
        request.setNewPassword("newPassword123");
        
        assertDoesNotThrow(() -> authService.resetPassword(request));
        verify(userRepository).save(any(User.class));
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(activationTokenRepository.save(any(ActivationToken.class))).thenReturn(new ActivationToken());
        when(emailService.sendActivationEmail(anyString(), anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        assertDoesNotThrow(() -> authService.register(registerRequest));

        // Then
        verify(userRepository).save(any(User.class));
        verify(activationTokenRepository).save(any(ActivationToken.class));
    }

    @Test
    void testRequestPasswordReset_Success() {
        // Given
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");
        
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenReturn(new PasswordResetToken());
        when(emailService.sendPasswordResetEmail(anyString(), anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        assertDoesNotThrow(() -> authService.requestPasswordReset(request));

        // Then
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void testCheckActivationStatus_Active() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");

        // When
        var result = authService.checkActivationStatus("test@example.com");

        // Then
        assertNotNull(result);
        assertTrue((Boolean) result.get("activated"));
        assertEquals("test@example.com", result.get("email"));
    }

    @Test
    void testCheckActivationStatus_NotFound() {
        // Given
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(com.englishflow.auth.exception.UserNotFoundException.class,
                () -> authService.checkActivationStatus("notfound@example.com"));
    }

    @Test
    void testLogout_Success() {
        // Given
        String refreshToken = "refresh-token-123";
        doNothing().when(refreshTokenService).revokeToken(refreshToken);

        // When
        assertDoesNotThrow(() -> authService.logout(refreshToken));

        // Then
        verify(refreshTokenService).revokeToken(refreshToken);
    }

    @Test
    void testLogoutFromAllDevices_Success() {
        // Given
        Long userId = 1L;
        doNothing().when(refreshTokenService).revokeAllUserTokens(userId);

        // When
        assertDoesNotThrow(() -> authService.logoutFromAllDevices(userId));

        // Then
        verify(refreshTokenService).revokeAllUserTokens(userId);
    }
}
