package com.englishflow.auth.service;

import com.englishflow.auth.entity.RefreshToken;
import com.englishflow.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days in milliseconds
    private long refreshTokenExpirationMs;

    /**
     * Create a new refresh token for a user
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId, String deviceInfo, String ipAddress) {
        // Clean up old tokens for this user (keep only last 5)
        cleanupOldTokensForUser(userId);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpirationMs / 1000))
                .revoked(false)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Find refresh token by token string
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verify if refresh token is valid and not expired
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new com.englishflow.auth.exception.TokenExpiredException("Refresh token");
        }

        // Update last used timestamp
        token.setLastUsedAt(LocalDateTime.now());
        return refreshTokenRepository.save(token);
    }

    /**
     * Revoke a refresh token
     */
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    /**
     * Revoke all refresh tokens for a user
     */
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        List<RefreshToken> userTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        userTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(userTokens);
    }

    /**
     * Clean up expired tokens
     */
    @Transactional
    public void cleanupExpiredTokens() {
        List<RefreshToken> expiredTokens = refreshTokenRepository.findByExpiryDateBefore(LocalDateTime.now());
        refreshTokenRepository.deleteAll(expiredTokens);
        log.info("Cleaned up {} expired refresh tokens", expiredTokens.size());
    }

    /**
     * Clean up old tokens for a user (keep only the 5 most recent)
     */
    @Transactional
    public void cleanupOldTokensForUser(Long userId) {
        List<RefreshToken> userTokens = refreshTokenRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (userTokens.size() > 5) {
            List<RefreshToken> tokensToDelete = userTokens.subList(5, userTokens.size());
            refreshTokenRepository.deleteAll(tokensToDelete);
        }
    }

    /**
     * Get active tokens count for a user
     */
    public long getActiveTokensCount(Long userId) {
        return refreshTokenRepository.countByUserIdAndRevokedFalseAndExpiryDateAfter(userId, LocalDateTime.now());
    }

    /**
     * Get all active tokens for a user
     */
    public List<RefreshToken> getActiveTokensForUser(Long userId) {
        return refreshTokenRepository.findByUserIdAndRevokedFalseAndExpiryDateAfter(userId, LocalDateTime.now());
    }
}