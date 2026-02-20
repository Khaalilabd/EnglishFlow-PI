package com.englishflow.auth.service;

import com.englishflow.auth.dto.LoginRequest;
import com.englishflow.auth.dto.RegisterRequest;
import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.exception.*;
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
    private AuditLogService auditLogService;

    @Mock
    private UserSessionService userSessionService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Setup test data
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("SecurePass123!");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setRole("STUDENT");
        registerRequest.setCin("AB123456");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("SecurePass123!");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);
        testUser.setActive(true);
        testUser.setProfileCompleted(true);
    }

    // ==================== Registration Tests ====================

    @Test
    void register_ShouldCreateUser_WhenValidRequest() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(emailService).sendActivationEmail(anyString(), anyString(), anyString());

        // When
        AuthResponse response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).save(any(User.class));
        verify(activationTokenRepository).save(any(ActivationToken.class));
        verify(emailService).sendActivationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== Login Tests ====================

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.login(loginRequest, request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("SecurePass123!", "encodedPassword");
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest, request);
        });
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIncorrect() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest, request);
        });
    }

    @Test
    void login_ShouldThrowException_WhenAccountNotActivated() {
        // Given
        testUser.setActive(false);
        when(rateLimitService.isBlocked(anyString())).thenReturn(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When & Then
        assertThrows(AccountNotActivatedException.class, () -> {
            authService.login(loginRequest, request);
        });
    }

    @Test
    void login_ShouldThrowException_WhenRateLimitExceeded() {
        // Given
        when(rateLimitService.isBlocked(anyString())).thenReturn(true);

        // When & Then
        assertThrows(RateLimitExceededException.class, () -> {
            authService.login(loginRequest, request);
        });

        verify(userRepository, never()).findByEmail(anyString());
    }

    // ==================== Activation Tests ====================

    @Test
    void activateAccount_ShouldActivateUser_WhenTokenValid() {
        // Given
        ActivationToken token = new ActivationToken();
        token.setToken("valid-token");
        token.setUser(testUser);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(false);

        testUser.setActive(false);

        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("jwt-token");

        // When
        AuthResponse response = authService.activateAccount("valid-token");

        // Then
        assertNotNull(response);
        assertTrue(testUser.isActive());
        assertTrue(token.isUsed());
        verify(activationTokenRepository).save(token);
        verify(userRepository).save(testUser);
    }

    @Test
    void activateAccount_ShouldThrowException_WhenTokenNotFound() {
        // Given
        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InvalidTokenException.class, () -> {
            authService.activateAccount("invalid-token");
        });
    }

    @Test
    void activateAccount_ShouldThrowException_WhenTokenExpired() {
        // Given
        ActivationToken token = new ActivationToken();
        token.setToken("expired-token");
        token.setExpiryDate(LocalDateTime.now().minusHours(1));
        token.setUsed(false);

        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            authService.activateAccount("expired-token");
        });
    }

    @Test
    void activateAccount_ShouldThrowException_WhenTokenAlreadyUsed() {
        // Given
        ActivationToken token = new ActivationToken();
        token.setToken("used-token");
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        token.setUsed(true);

        when(activationTokenRepository.findByToken(anyString())).thenReturn(Optional.of(token));

        // When & Then
        assertThrows(InvalidTokenException.class, () -> {
            authService.activateAccount("used-token");
        });
    }
}
