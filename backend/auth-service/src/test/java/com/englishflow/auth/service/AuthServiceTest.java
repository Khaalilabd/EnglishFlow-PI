package com.englishflow.auth.service;

import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.RefreshToken;
import com.englishflow.auth.exception.InvalidCredentialsException;
import com.englishflow.auth.exception.AccountNotActivatedException;
import com.englishflow.auth.exception.EmailAlreadyExistsException;
import com.englishflow.auth.exception.RateLimitExceededException;
import com.englishflow.auth.repository.UserRepository;
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
    private UserSessionService userSessionService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private MetricsService metricsService;

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
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);
        testUser.setActive(true);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("SecurePass123!");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setRole("STUDENT");
        registerRequest.setCin("AB123456");
        registerRequest.setRecaptchaToken("valid-token");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setRecaptchaToken("valid-token");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(activationTokenRepository.save(any(ActivationToken.class))).thenReturn(new ActivationToken());
        doNothing().when(emailService).sendActivationEmail(anyString(), anyString(), anyString());

        // When
        authService.register(registerRequest);

        // Then
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository).save(any(User.class));
        verify(activationTokenRepository).save(any(ActivationToken.class));
        verify(emailService).sendActivationEmail(anyString(), anyString(), anyString());
        verify(metricsService).recordRegistration();
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        
        RefreshToken mockRefreshToken = RefreshToken.builder()
                .token("refresh-token")
                .userId(testUser.getId())
                .build();
        when(refreshTokenService.createRefreshToken(anyLong(), anyString(), anyString())).thenReturn(mockRefreshToken);
        when(httpServletRequest.getHeader(anyString())).thenReturn("Mozilla/5.0");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        // When
        AuthResponse response = authService.login(loginRequest, httpServletRequest);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(testUser.getEmail(), response.getEmail());
        verify(rateLimitService).resetAttempts(anyString());
        verify(metricsService).recordLoginSuccess();
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest, httpServletRequest);
        });

        verify(rateLimitService).recordFailedAttempt(anyString());
        verify(metricsService).recordLoginFailure();
    }

    @Test
    void testLogin_AccountNotActivated() {
        // Given
        testUser.setActive(false);
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThrows(AccountNotActivatedException.class, () -> {
            authService.login(loginRequest, httpServletRequest);
        });

        verify(metricsService).recordLoginFailure();
    }

    @Test
    void testLogin_RateLimitExceeded() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(true);
        when(rateLimitService.getRemainingAttempts(anyString())).thenReturn(0);

        // When & Then
        assertThrows(RateLimitExceededException.class, () -> {
            authService.login(loginRequest, httpServletRequest);
        });

        verify(userRepository, never()).findByEmail(anyString());
        verify(metricsService).recordRateLimitExceeded();
    }

    @Test
    void testActivateAccount_Success() {
        // Given
        String token = "valid-token";
        ActivationToken activationToken = ActivationToken.builder()
                .token(token)
                .user(testUser)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        when(activationTokenRepository.findByToken(token)).thenReturn(Optional.of(activationToken));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");
        
        RefreshToken mockRefreshToken = RefreshToken.builder()
                .token("refresh-token")
                .userId(testUser.getId())
                .build();
        when(refreshTokenService.createRefreshToken(anyLong(), anyString(), anyString())).thenReturn(mockRefreshToken);

        // When
        AuthResponse response = authService.activateAccount(token);

        // Then
        assertNotNull(response);
        assertTrue(testUser.isActive());
        assertTrue(activationToken.isUsed());
        verify(metricsService).recordActivation();
    }

    @Test
    void testValidateToken_Valid() {
        // Given
        String token = "valid-jwt-token";
        when(jwtUtil.validateToken(token)).thenReturn(true);

        // When
        boolean isValid = authService.validateToken(token);

        // Then
        assertTrue(isValid);
        verify(jwtUtil).validateToken(token);
    }

    @Test
    void testValidateToken_Invalid() {
        // Given
        String token = "invalid-jwt-token";
        when(jwtUtil.validateToken(token)).thenReturn(false);

        // When
        boolean isValid = authService.validateToken(token);

        // Then
        assertFalse(isValid);
        verify(metricsService).recordInvalidToken();
    }
}
