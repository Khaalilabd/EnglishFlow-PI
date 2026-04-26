package com.englishflow.auth.controller;

import com.englishflow.auth.dto.*;
import com.englishflow.auth.service.AuthService;
import com.englishflow.auth.service.RecaptchaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletRequest;
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
    private HttpServletRequest httpRequest;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private SponsorRegisterRequest sponsorRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("Password123!");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setRecaptchaToken("valid-recaptcha-token");

        sponsorRequest = new SponsorRegisterRequest();
        sponsorRequest.setEmail("sponsor@example.com");
        sponsorRequest.setPassword("Password123!");
        sponsorRequest.setFirstName("Jane");
        sponsorRequest.setLastName("Smith");
        sponsorRequest.setPhone("123456789");
        sponsorRequest.setCin("AB123456");

        authResponse = AuthResponse.builder()
                .id(1L)
                .email("sponsor@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .role("SPONSOR")
                .token("jwt-token")
                .build();
    }

    @Test
    void register_Success() {
        // Arrange
        AuthResponse mockResponse = AuthResponse.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("STUDENT")
                .token(null)
                .profileCompleted(false)
                .build();
        
        when(recaptchaService.verifyRecaptcha("valid-recaptcha-token")).thenReturn(true);
        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("Registration successful"));
        verify(recaptchaService).verifyRecaptcha("valid-recaptcha-token");
        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_RecaptchaFailed() {
        // Arrange
        when(recaptchaService.verifyRecaptcha("valid-recaptcha-token")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").contains("reCAPTCHA verification failed"));
        verify(recaptchaService).verifyRecaptcha("valid-recaptcha-token");
        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void registerSponsor_Success() {
        // Arrange
        when(authService.registerSponsor(any(SponsorRegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.registerSponsor(sponsorRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("message").toString().contains("Sponsor registration successful"));
        assertEquals(1L, response.getBody().get("id"));
        assertEquals("sponsor@example.com", response.getBody().get("email"));
        assertEquals("SPONSOR", response.getBody().get("role"));
        verify(authService).registerSponsor(any(SponsorRegisterRequest.class));
    }

    @Test
    void activateAccountView_Success() {
        // Arrange
        String token = "valid-token";
        when(authService.activateAccount(token)).thenReturn(authResponse);

        // Act
        String viewName = authController.activateAccountView(token, model);

        // Assert
        assertEquals("activation-success", viewName);
        verify(authService).activateAccount(token);
        verify(model, never()).addAttribute(anyString(), any());
    }

    @Test
    void activateAccountView_Error() {
        // Arrange
        String token = "invalid-token";
        when(authService.activateAccount(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        String viewName = authController.activateAccountView(token, model);

        // Assert
        assertEquals("activation-error", viewName);
        verify(authService).activateAccount(token);
        verify(model).addAttribute("error", "Invalid token");
    }

    @Test
    void activateAccountApi_Success() {
        // Arrange
        String token = "valid-token";
        when(authService.activateAccount(token)).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.activateAccountApi(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(authResponse, response.getBody());
        verify(authService).activateAccount(token);
    }

    @Test
    void checkActivationStatus_Success() {
        // Arrange
        String email = "test@example.com";
        Map<String, Object> status = Map.of("activated", true, "email", email);
        when(authService.checkActivationStatus(email)).thenReturn(status);

        // Act
        ResponseEntity<Map<String, Object>> response = authController.checkActivationStatus(email);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(status, response.getBody());
        verify(authService).checkActivationStatus(email);
    }

    @Test
    void validateToken_Success() {
        // Arrange
        String token = "valid-token";
        when(authService.validateToken(token)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = authController.validateToken(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody());
        verify(authService).validateToken(token);
    }

    @Test
    void requestPasswordReset_Success() {
        // Arrange
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail("test@example.com");
        doNothing().when(authService).requestPasswordReset(any(PasswordResetRequest.class));

        // Act
        ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password reset email sent", response.getBody().get("message"));
        verify(authService).requestPasswordReset(any(PasswordResetRequest.class));
    }

    @Test
    void confirmPasswordReset_Success() {
        // Arrange
        PasswordResetConfirm request = new PasswordResetConfirm();
        request.setToken("reset-token");
        request.setNewPassword("NewPassword123!");
        doNothing().when(authService).resetPassword(any(PasswordResetConfirm.class));

        // Act
        ResponseEntity<Map<String, String>> response = authController.confirmPasswordReset(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password reset successful", response.getBody().get("message"));
        verify(authService).resetPassword(any(PasswordResetConfirm.class));
    }

    @Test
    void completeProfile_Success() {
        // Arrange
        Long userId = 1L;
        Map<String, String> profileData = Map.of("bio", "Test bio", "phone", "123456789");
        doNothing().when(authService).completeProfile(userId, profileData);

        // Act
        ResponseEntity<Map<String, String>> response = authController.completeProfile(userId, profileData);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Profile completed successfully", response.getBody().get("message"));
        verify(authService).completeProfile(userId, profileData);
    }

    @Test
    void logout_Success() {
        // Arrange
        Map<String, String> request = Map.of("refreshToken", "refresh-token-123");
        doNothing().when(authService).logout("refresh-token-123");

        // Act
        ResponseEntity<Map<String, String>> response = authController.logout(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Logged out successfully", response.getBody().get("message"));
        verify(authService).logout("refresh-token-123");
    }

    @Test
    void logoutFromAllDevices_Success() {
        // Arrange
        Map<String, Long> request = Map.of("userId", 1L);
        doNothing().when(authService).logoutFromAllDevices(1L);

        // Act
        ResponseEntity<Map<String, String>> response = authController.logoutFromAllDevices(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Logged out from all devices successfully", response.getBody().get("message"));
        verify(authService).logoutFromAllDevices(1L);
    }
}