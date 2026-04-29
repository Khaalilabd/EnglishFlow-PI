package com.englishflow.sponsors.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String jwtSecret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnoughForHS256Algorithm";
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = createValidToken(100L, "STUDENT");

        // Act
        boolean result = jwtUtil.validateToken(token);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean result = jwtUtil.validateToken(invalidToken);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 100L);
        claims.put("role", "STUDENT");
        
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject("100")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000)) // Expired 5 seconds ago
                .signWith(signingKey)
                .compact();

        // Act
        boolean result = jwtUtil.validateToken(expiredToken);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void extractUserId_WithLongUserId_ShouldReturnUserId() {
        // Arrange
        Long expectedUserId = 100L;
        String token = createValidToken(expectedUserId, "STUDENT");

        // Act
        Long userId = jwtUtil.extractUserId(token);

        // Assert
        assertThat(userId).isEqualTo(expectedUserId);
    }

    @Test
    void extractUserId_WithIntegerUserId_ShouldConvertToLong() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 100); // Integer instead of Long
        claims.put("role", "STUDENT");
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("100")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(signingKey)
                .compact();

        // Act
        Long userId = jwtUtil.extractUserId(token);

        // Assert
        assertThat(userId).isEqualTo(100L);
    }

    @Test
    void extractUserId_WithMissingUserId_ShouldReturnNull() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "STUDENT");
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("test")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(signingKey)
                .compact();

        // Act
        Long userId = jwtUtil.extractUserId(token);

        // Assert
        assertThat(userId).isNull();
    }

    @Test
    void extractRole_WithValidToken_ShouldReturnRole() {
        // Arrange
        String expectedRole = "STUDENT";
        String token = createValidToken(100L, expectedRole);

        // Act
        String role = jwtUtil.extractRole(token);

        // Assert
        assertThat(role).isEqualTo(expectedRole);
    }

    @Test
    void extractRole_WithDifferentRoles_ShouldReturnCorrectRole() {
        // Arrange
        String tutorToken = createValidToken(200L, "TUTOR");
        String adminToken = createValidToken(300L, "ADMIN");

        // Act
        String tutorRole = jwtUtil.extractRole(tutorToken);
        String adminRole = jwtUtil.extractRole(adminToken);

        // Assert
        assertThat(tutorRole).isEqualTo("TUTOR");
        assertThat(adminRole).isEqualTo("ADMIN");
    }

    @Test
    void extractRole_WithMissingRole_ShouldReturnNull() {
        // Arrange
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 100L);
        
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("100")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(signingKey)
                .compact();

        // Act
        String role = jwtUtil.extractRole(token);

        // Assert
        assertThat(role).isNull();
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Act
        boolean result = jwtUtil.validateToken(null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Act
        boolean result = jwtUtil.validateToken("");

        // Assert
        assertThat(result).isFalse();
    }

    private String createValidToken(Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(signingKey)
                .compact();
    }
}
