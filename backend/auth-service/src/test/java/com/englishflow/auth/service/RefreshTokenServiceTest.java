package com.englishflow.auth.service;

import com.englishflow.auth.entity.RefreshToken;
import com.englishflow.auth.exception.TokenExpiredException;
import com.englishflow.auth.repository.RefreshTokenRepository;
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
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationMs", 604800000L);
        
        testToken = RefreshToken.builder()
                .id(1L)
                .token("test-refresh-token")
                .userId(100L)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .deviceInfo("Chrome/120.0")
                .ipAddress("192.168.1.1")
                .build();
    }

    @Test
    void testCreateRefreshToken_Success() {
        // Given
        Long userId = 100L;
        when(refreshTokenRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(Arrays.asList());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // When
        RefreshToken result = refreshTokenService.createRefreshToken(userId, "Chrome/120.0", "192.168.1.1");

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testFindByToken_Success() {
        // Given
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(testToken));

        // When
        Optional<RefreshToken> result = refreshTokenService.findByToken("test-refresh-token");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test-refresh-token", result.get().getToken());
    }

    @Test
    void testVerifyExpiration_ValidToken() {
        // Given
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // When
        RefreshToken result = refreshTokenService.verifyExpiration(testToken);

        // Then
        assertNotNull(result);
        verify(refreshTokenRepository).save(testToken);
    }

    @Test
    void testVerifyExpiration_ExpiredToken() {
        // Given
        testToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        doNothing().when(refreshTokenRepository).delete(testToken);

        // When & Then
        assertThrows(TokenExpiredException.class, () -> {
            refreshTokenService.verifyExpiration(testToken);
        });
        verify(refreshTokenRepository).delete(testToken);
    }

    @Test
    void testRevokeToken_Success() {
        // Given
        when(refreshTokenRepository.findByToken("test-refresh-token")).thenReturn(Optional.of(testToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // When
        refreshTokenService.revokeToken("test-refresh-token");

        // Then
        verify(refreshTokenRepository).save(testToken);
    }

    @Test
    void testRevokeAllUserTokens_Success() {
        // Given
        Long userId = 100L;
        List<RefreshToken> tokens = Arrays.asList(testToken);
        when(refreshTokenRepository.findByUserIdAndRevokedFalse(userId)).thenReturn(tokens);
        when(refreshTokenRepository.saveAll(anyList())).thenReturn(tokens);

        // When
        refreshTokenService.revokeAllUserTokens(userId);

        // Then
        verify(refreshTokenRepository).saveAll(tokens);
    }

    @Test
    void testCleanupExpiredTokens_Success() {
        // Given
        List<RefreshToken> expiredTokens = Arrays.asList(testToken);
        when(refreshTokenRepository.findByExpiryDateBefore(any(LocalDateTime.class))).thenReturn(expiredTokens);
        doNothing().when(refreshTokenRepository).deleteAll(expiredTokens);

        // When
        refreshTokenService.cleanupExpiredTokens();

        // Then
        verify(refreshTokenRepository).deleteAll(expiredTokens);
    }

    @Test
    void testGetActiveTokensCount_Success() {
        // Given
        Long userId = 100L;
        when(refreshTokenRepository.countByUserIdAndRevokedFalseAndExpiryDateAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(3L);

        // When
        long count = refreshTokenService.getActiveTokensCount(userId);

        // Then
        assertEquals(3L, count);
    }

    @Test
    void testGetActiveTokensForUser_Success() {
        // Given
        Long userId = 100L;
        List<RefreshToken> tokens = Arrays.asList(testToken);
        when(refreshTokenRepository.findByUserIdAndRevokedFalseAndExpiryDateAfter(eq(userId), any(LocalDateTime.class)))
                .thenReturn(tokens);

        // When
        List<RefreshToken> result = refreshTokenService.getActiveTokensForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCleanupOldTokensForUser_ExceedsLimit() {
        // Given
        Long userId = 100L;
        List<RefreshToken> tokens = Arrays.asList(
                testToken, testToken, testToken, testToken, testToken, testToken, testToken
        );
        when(refreshTokenRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(tokens);
        doNothing().when(refreshTokenRepository).deleteAll(anyList());

        // When
        refreshTokenService.cleanupOldTokensForUser(userId);

        // Then
        verify(refreshTokenRepository).deleteAll(anyList());
    }
}
