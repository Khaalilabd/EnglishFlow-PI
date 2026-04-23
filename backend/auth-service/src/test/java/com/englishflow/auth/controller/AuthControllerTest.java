package com.englishflow.auth.controller;

import com.englishflow.auth.dto.*;
import com.englishflow.auth.service.AuthService;
import com.englishflow.auth.service.RecaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private RecaptchaService recaptchaService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setRecaptchaToken("valid-token");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("Password123!");
        loginRequest.setRecaptchaToken("valid-token");

        authResponse = new AuthResponse();
        authResponse.setId(1L);
        authResponse.setEmail("test@example.com");
        authResponse.setToken("jwt-token");
    }

    @Test
    void testRegister_Success() {
        // Given
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(true);
        doNothing().when(authService).register(any(RegisterRequest.class));

        // When
        ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Registration successful"));
        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    void testRegister_RecaptchaFailed() {
        // Given
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(false);

        // When
        ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("reCAPTCHA verification failed"));
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void testRegisterSponsor_Success() {
        // Given
        SponsorRegisterRequest sponsorRequest = new SponsorRegisterRequest();
        sponsorRequest.setEmail("sponsor@example.com");
        sponsorRequest.setPassword("Password123!");
        sponsorRequest.setFirstName("Sponsor");
        sponsorRequest.setLastName("Company");

        when(authService.registerSponsor(any(SponsorRegisterRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<Map<String, Object>> response = authController.registerSponsor(sponsorRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("email"));
        verify(authService, times(1)).registerSponsor(any(SponsorRegisterRequest.class));
    }

    @Test
    void testActivateAccountApi_Success() {
        // Given
        String token = "activation-token";
        when(authService.activateAccount(token)).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.activateAccountApi(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());
        verify(authService, times(1)).activateAccount(token);
    }

    @Test
    void testCheckActivationStatus() {
        // Given
        String email = "test@example.com";
        Map<String, Object> status = Map.of("activated", true);
        when(authService.checkActivationStatus(email)).thenReturn(status);

        // When
        ResponseEntity<Map<String, Object>> response = authController.checkActivationStatus(email);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(status, response.getBody());
        verify(authService, times(1)).checkActivationStatus(email);
    }

    @Test
    void testLogin_Success() {
        // Given
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(true);
        when(authService.login(any(LoginRequest.class), any(HttpServletRequest.class))).thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());
        verify(authService, times(1)).login(any(LoginRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testLogin_RecaptchaFailed() {
        // Given
        when(recaptchaService.verifyRecaptcha(anyString())).thenReturn(false);

        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest, httpServletRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(authService, never()).login(any(LoginRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testValidateToken() {
        // Given
        String token = "jwt-token";
        when(authService.validateToken(token)).thenReturn(true);

        // When
        ResponseEntity<Boolean> response = authController.validateToken(token);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(authService, times(1)).validateToken(token);
    }

    @Test
    void testRequestPasswordReset() {
        // Given
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");
        doNothing().when(authService).requestPasswordReset(any(PasswordResetRequest.class));

        // When
        ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Password reset email sent"));
        verify(authService, times(1)).requestPasswordReset(any(PasswordResetRequest.class));
    }

    @Test
    void testConfirmPasswordReset() {
        // Given
        PasswordResetConfirm request = new PasswordResetConfirm();
        request.setToken("reset-token");
        request.setNewPassword("NewPassword123!");
        doNothing().when(authService).resetPassword(any(PasswordResetConfirm.class));

        // When
        ResponseEntity<Map<String, String>> response = authController.confirmPasswordReset(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Password reset successful"));
        verify(authService, times(1)).resetPassword(any(PasswordResetConfirm.class));
    }

    @Test
    void testCompleteProfile() {
        // Given
        Long userId = 1L;
        Map<String, String> profileData = Map.of("bio", "Test bio");
        doNothing().when(authService).completeProfile(userId, profileData);

        // When
        ResponseEntity<Map<String, String>> response = authController.completeProfile(userId, profileData);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Profile completed successfully"));
        verify(authService, times(1)).completeProfile(userId, profileData);
    }

    @Test
    void testRefreshToken_Success() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh-token");
        RefreshTokenResponse refreshResponse = new RefreshTokenResponse();
        refreshResponse.setAccessToken("new-access-token");
        when(authService.refreshToken(any(RefreshTokenRequest.class), any(HttpServletRequest.class)))
                .thenReturn(refreshResponse);

        // When
        ResponseEntity<RefreshTokenResponse> response = authController.refreshToken(request, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("new-access-token", response.getBody().getAccessToken());
        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testRefreshToken_Unauthorized() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("invalid-token");
        when(authService.refreshToken(any(RefreshTokenRequest.class), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Invalid token"));

        // When
        ResponseEntity<RefreshTokenResponse> response = authController.refreshToken(request, httpServletRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).refreshToken(any(RefreshTokenRequest.class), any(HttpServletRequest.class));
    }

    @Test
    void testLogout() {
        // Given
        Map<String, String> request = Map.of("refreshToken", "refresh-token");
        doNothing().when(authService).logout(anyString());

        // When
        ResponseEntity<Map<String, String>> response = authController.logout(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Logged out successfully"));
        verify(authService, times(1)).logout(anyString());
    }

    @Test
    void testLogoutFromAllDevices() {
        // Given
        Map<String, Long> request = Map.of("userId", 1L);
        doNothing().when(authService).logoutFromAllDevices(anyLong());

        // When
        ResponseEntity<Map<String, String>> response = authController.logoutFromAllDevices(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Logged out from all devices successfully"));
        verify(authService, times(1)).logoutFromAllDevices(anyLong());
    }

    @Test
    void testVerifyTwoFactorLogin_Success() {
        // Given
        TwoFactorLoginRequest request = new TwoFactorLoginRequest();
        request.setTempToken("temp-token");
        request.setCode("123456");
        when(authService.verifyTwoFactorLogin(anyString(), anyString(), any(HttpServletRequest.class)))
                .thenReturn(authResponse);

        // When
        ResponseEntity<AuthResponse> response = authController.verifyTwoFactorLogin(request, httpServletRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(authResponse, response.getBody());
        verify(authService, times(1)).verifyTwoFactorLogin(anyString(), anyString(), any(HttpServletRequest.class));
    }

    @Test
    void testVerifyTwoFactorLogin_Unauthorized() {
        // Given
        TwoFactorLoginRequest request = new TwoFactorLoginRequest();
        request.setTempToken("temp-token");
        request.setCode("invalid");
        when(authService.verifyTwoFactorLogin(anyString(), anyString(), any(HttpServletRequest.class)))
                .thenThrow(new RuntimeException("Invalid code"));

        // When
        ResponseEntity<AuthResponse> response = authController.verifyTwoFactorLogin(request, httpServletRequest);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authService, times(1)).verifyTwoFactorLogin(anyString(), anyString(), any(HttpServletRequest.class));
    }
}
