package com.englishflow.auth.controller;

import com.englishflow.auth.dto.TwoFactorSetupResponse;
import com.englishflow.auth.dto.TwoFactorStatusResponse;
import com.englishflow.auth.dto.TwoFactorVerifyRequest;
import com.englishflow.auth.security.JwtUtil;
import com.englishflow.auth.service.TwoFactorAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthControllerTest {

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private TwoFactorAuthController twoFactorAuthController;

    private final Long userId = 1L;
    private final String token = "Bearer test-jwt-token";
    private final String jwtToken = "test-jwt-token";

    @BeforeEach
    void setUp() {
        when(jwtUtil.extractUserId(jwtToken)).thenReturn(userId);
    }

    @Test
    void setupTwoFactor_Success() {
        // Arrange
        TwoFactorSetupResponse setupResponse = TwoFactorSetupResponse.builder()
                .secret("JBSWY3DPEHPK3PXP")
                .qrCodeUrl("otpauth://totp/EnglishFlow:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=EnglishFlow")
                .backupCodes(List.of("12345678", "87654321"))
                .build();
        
        when(twoFactorAuthService.setupTwoFactor(userId)).thenReturn(setupResponse);

        // Act
        ResponseEntity<TwoFactorSetupResponse> response = 
            twoFactorAuthController.setupTwoFactor(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("JBSWY3DPEHPK3PXP", response.getBody().getSecret());
        assertNotNull(response.getBody().getQrCodeUrl());
        assertEquals(2, response.getBody().getBackupCodes().size());
        verify(twoFactorAuthService).setupTwoFactor(userId);
    }

    @Test
    void enableTwoFactor_Success() {
        // Arrange
        TwoFactorVerifyRequest request = new TwoFactorVerifyRequest();
        request.setCode("123456");
        
        doNothing().when(twoFactorAuthService).enableTwoFactor(userId, "123456");

        // Act
        ResponseEntity<Map<String, String>> response = 
            twoFactorAuthController.enableTwoFactor(token, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("2FA enabled successfully", response.getBody().get("message"));
        assertEquals("enabled", response.getBody().get("status"));
        verify(twoFactorAuthService).enableTwoFactor(userId, "123456");
    }

    @Test
    void disableTwoFactor_Success() {
        // Arrange
        TwoFactorVerifyRequest request = new TwoFactorVerifyRequest();
        request.setCode("123456");
        
        doNothing().when(twoFactorAuthService).disableTwoFactor(userId, "123456");

        // Act
        ResponseEntity<Map<String, String>> response = 
            twoFactorAuthController.disableTwoFactor(token, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("2FA disabled successfully", response.getBody().get("message"));
        assertEquals("disabled", response.getBody().get("status"));
        verify(twoFactorAuthService).disableTwoFactor(userId, "123456");
    }

    @Test
    void verifyTwoFactorCode_ValidCode() {
        // Arrange
        TwoFactorVerifyRequest request = new TwoFactorVerifyRequest();
        request.setCode("123456");
        
        when(twoFactorAuthService.verifyTwoFactorCode(userId, "123456")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Boolean>> response = 
            twoFactorAuthController.verifyTwoFactorCode(token, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().get("valid"));
        assertTrue(response.getBody().get("verified"));
        verify(twoFactorAuthService).verifyTwoFactorCode(userId, "123456");
    }

    @Test
    void verifyTwoFactorCode_InvalidCode() {
        // Arrange
        TwoFactorVerifyRequest request = new TwoFactorVerifyRequest();
        request.setCode("000000");
        
        when(twoFactorAuthService.verifyTwoFactorCode(userId, "000000")).thenReturn(false);

        // Act
        ResponseEntity<Map<String, Boolean>> response = 
            twoFactorAuthController.verifyTwoFactorCode(token, request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().get("valid"));
        assertFalse(response.getBody().get("verified"));
        verify(twoFactorAuthService).verifyTwoFactorCode(userId, "000000");
    }

    @Test
    void getTwoFactorStatus_Enabled() {
        // Arrange
        TwoFactorStatusResponse statusResponse = TwoFactorStatusResponse.builder()
                .enabled(true)
                .backupCodesRemaining(5)
                .build();
        
        when(twoFactorAuthService.getTwoFactorStatus(userId)).thenReturn(statusResponse);

        // Act
        ResponseEntity<TwoFactorStatusResponse> response = 
            twoFactorAuthController.getTwoFactorStatus(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getEnabled());
        assertEquals(5, response.getBody().getBackupCodesRemaining());
        verify(twoFactorAuthService).getTwoFactorStatus(userId);
    }

    @Test
    void getTwoFactorStatus_Disabled() {
        // Arrange
        TwoFactorStatusResponse statusResponse = TwoFactorStatusResponse.builder()
                .enabled(false)
                .backupCodesRemaining(0)
                .build();
        
        when(twoFactorAuthService.getTwoFactorStatus(userId)).thenReturn(statusResponse);

        // Act
        ResponseEntity<TwoFactorStatusResponse> response = 
            twoFactorAuthController.getTwoFactorStatus(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getEnabled());
        verify(twoFactorAuthService).getTwoFactorStatus(userId);
    }

    @Test
    void regenerateBackupCodes_Success() {
        // Arrange
        List<String> newBackupCodes = List.of(
            "11111111", "22222222", "33333333", "44444444", "55555555"
        );
        
        when(twoFactorAuthService.regenerateBackupCodes(userId)).thenReturn(newBackupCodes);

        // Act
        ResponseEntity<Map<String, Object>> response = 
            twoFactorAuthController.regenerateBackupCodes(token);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(newBackupCodes, response.getBody().get("backupCodes"));
        assertTrue(response.getBody().get("message").toString().contains("backup codes"));
        verify(twoFactorAuthService).regenerateBackupCodes(userId);
    }
}
