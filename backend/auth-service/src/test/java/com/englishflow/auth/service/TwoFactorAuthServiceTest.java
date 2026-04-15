package com.englishflow.auth.service;

import com.englishflow.auth.dto.TwoFactorSetupResponse;
import com.englishflow.auth.dto.TwoFactorStatusResponse;
import com.englishflow.auth.entity.TwoFactorAuth;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.InvalidTokenException;
import com.englishflow.auth.exception.UserNotFoundException;
import com.englishflow.auth.repository.TwoFactorAuthRepository;
import com.englishflow.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthServiceTest {

    @Mock
    private TwoFactorAuthRepository twoFactorAuthRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    private User testUser;
    private TwoFactorAuth twoFactorAuth;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        twoFactorAuth = TwoFactorAuth.builder()
                .id(1L)
                .user(testUser)
                .secret("TESTSECRET123456")
                .enabled(true)
                .backupCodes(Arrays.asList("12345678", "87654321"))
                .enabledAt(LocalDateTime.now())
                .build();

        ReflectionTestUtils.setField(twoFactorAuthService, "appName", "EnglishFlow");
    }

    @Test
    void testSetupTwoFactor_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);

        // Act
        TwoFactorSetupResponse response = twoFactorAuthService.setupTwoFactor(1L);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getSecret());
        assertNotNull(response.getQrCodeUrl());
        assertNotNull(response.getBackupCodes());
        assertEquals(10, response.getBackupCodes().size());
        verify(twoFactorAuthRepository).save(any(TwoFactorAuth.class));
    }

    @Test
    void testSetupTwoFactor_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            twoFactorAuthService.setupTwoFactor(1L);
        });
    }

    @Test
    void testEnableTwoFactor_NotSetup() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> {
            twoFactorAuthService.enableTwoFactor(1L, "123456");
        });
    }

    @Test
    void testDisableTwoFactor_NotFound() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> {
            twoFactorAuthService.disableTwoFactor(1L, "123456");
        });
    }

    @Test
    void testIsTwoFactorEnabled_True() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));

        // Act
        boolean result = twoFactorAuthService.isTwoFactorEnabled(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTwoFactorEnabled_False() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = twoFactorAuthService.isTwoFactorEnabled(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetTwoFactorStatus_Enabled() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));

        // Act
        TwoFactorStatusResponse response = twoFactorAuthService.getTwoFactorStatus(1L);

        // Assert
        assertNotNull(response);
        assertTrue(response.getEnabled());
        assertEquals(2, response.getBackupCodesRemaining());
        assertNotNull(response.getEnabledAt());
    }

    @Test
    void testGetTwoFactorStatus_NotEnabled() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        TwoFactorStatusResponse response = twoFactorAuthService.getTwoFactorStatus(1L);

        // Assert
        assertNotNull(response);
        assertFalse(response.getEnabled());
        assertEquals(0, response.getBackupCodesRemaining());
    }

    @Test
    void testRegenerateBackupCodes_Success() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);

        // Act
        List<String> newCodes = twoFactorAuthService.regenerateBackupCodes(1L);

        // Assert
        assertNotNull(newCodes);
        assertEquals(10, newCodes.size());
        verify(twoFactorAuthRepository).save(any(TwoFactorAuth.class));
    }

    @Test
    void testRegenerateBackupCodes_NotFound() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> {
            twoFactorAuthService.regenerateBackupCodes(1L);
        });
    }

    @Test
    void testVerifyTwoFactorCode_NotEnabled() {
        // Arrange
        TwoFactorAuth disabledAuth = TwoFactorAuth.builder()
                .user(testUser)
                .secret("SECRET")
                .enabled(false)
                .build();
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(disabledAuth));

        // Act
        boolean result = twoFactorAuthService.verifyTwoFactorCode(1L, "123456");

        // Assert
        assertFalse(result);
    }

    @Test
    void testVerifyTwoFactorCode_NotFound() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = twoFactorAuthService.verifyTwoFactorCode(1L, "123456");

        // Assert
        assertFalse(result);
    }
}
