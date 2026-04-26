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
import static org.mockito.ArgumentMatchers.any;
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
                .token("test-token-123")
                .userId(100L)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .deviceInfo("Chrome/Windows")
                .ipAddress("192.168.1.1")
                .build();
    }

    @Test
    void createRefreshToken_ShouldCreateAndReturnToken() {
        // Arrange
        when(refreshTokenRepository.findByUserIdOrderByCreatedAtDesc(100L)).thenReturn(Arrays.asList());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(100L, "Chrome/Windows", "192.168.1.1");

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getUserId());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_WhenTokenExists_ShouldReturnToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-token-123")).thenReturn(Optional.of(testToken));

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("test-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test-token-123", result.get().getToken());
        verify(refreshTokenRepository, times(1)).findByToken("test-token-123");
    }

    @Test
    void findByToken_WhenTokenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("invalid-token");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void verifyExpiration_WhenTokenValid_ShouldUpdateAndReturn() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // Act
        RefreshToken result = refreshTokenService.verifyExpiration(testToken);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getLastUsedAt());
        verify(refreshTokenRepository, times(1)).save(testToken);
    }

    @Test
    void verifyExpiration_WhenTokenExpired_ShouldThrowException() {
        // Arrange
        testToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        doNothing().when(refreshTokenRepository).delete(testToken);

        // Act & Assert
        assertThrows(TokenExpiredException.class, () -> refreshTokenService.verifyExpiration(testToken));
        verify(refreshTokenRepository, times(1)).delete(testToken);
    }

    @Test
    void revokeToken_WhenTokenExists_ShouldRevokeSuccessfully() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-token-123")).thenReturn(Optional.of(testToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // Act
        refreshTokenService.revokeToken("test-token-123");

        // Assert
        verify(refreshTokenRepository, times(1)).save(testToken);
    }

    @Test
    void revokeAllUserTokens_ShouldRevokeAllTokens() {
        // Arrange
        RefreshToken token1 = RefreshToken.builder().id(1L).userId(100L).revoked(false).build();
        RefreshToken token2 = RefreshToken.builder().id(2L).userId(100L).revoked(false).build();
        List<RefreshToken> tokens = Arrays.asList(token1, token2);
        
        when(refreshTokenRepository.findByUserIdAndRevokedFalse(100L)).thenReturn(tokens);
        when(refreshTokenRepository.saveAll(anyList())).thenReturn(tokens);

        // Act
        refreshTokenService.revokeAllUserTokens(100L);

        // Assert
        assertTrue(token1.isRevoked());
        assertTrue(token2.isRevoked());
        verify(refreshTokenRepository, times(1)).saveAll(tokens);
    }

    @Test
    void cleanupExpiredTokens_ShouldDeleteExpiredTokens() {
        // Arrange
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .expiryDate(LocalDateTime.now().minusDays(1))
                .build();
        List<RefreshToken> expiredTokens = Arrays.asList(expiredToken);
        
        when(refreshTokenRepository.findByExpiryDateBefore(any(LocalDateTime.class))).thenReturn(expiredTokens);
        doNothing().when(refreshTokenRepository).deleteAll(expiredTokens);

        // Act
        refreshTokenService.cleanupExpiredTokens();

        // Assert
        verify(refreshTokenRepository, times(1)).deleteAll(expiredTokens);
    }

    @Test
    void getActiveTokensCount_ShouldReturnCount() {
        // Arrange
        when(refreshTokenRepository.countByUserIdAndRevokedFalseAndExpiryDateAfter(eq(100L), any(LocalDateTime.class)))
                .thenReturn(3L);

        // Act
        long count = refreshTokenService.getActiveTokensCount(100L);

        // Assert
        assertEquals(3L, count);
        verify(refreshTokenRepository, times(1))
                .countByUserIdAndRevokedFalseAndExpiryDateAfter(eq(100L), any(LocalDateTime.class));
    }

    @Test
    void getActiveTokensForUser_ShouldReturnActiveTokens() {
        // Arrange
        List<RefreshToken> activeTokens = Arrays.asList(testToken);
        when(refreshTokenRepository.findByUserIdAndRevokedFalseAndExpiryDateAfter(eq(100L), any(LocalDateTime.class)))
                .thenReturn(activeTokens);

        // Act
        List<RefreshToken> result = refreshTokenService.getActiveTokensForUser(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(refreshTokenRepository, times(1))
                .findByUserIdAndRevokedFalseAndExpiryDateAfter(eq(100L), any(LocalDateTime.class));
    }
}
