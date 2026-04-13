package com.englishflow.complaints.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret = "mySecretKeyForTestingPurposesOnlyMustBeLongEnough";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        signingKey = Keys.hmacShaKeyFor(testSecret.getBytes());
    }

    @Test
    void extractUserId_ValidToken_ReturnsUserId() {
        // Arrange
        Long expectedUserId = 123L;
        String token = createTestToken(expectedUserId, "STUDENT", "test@example.com");

        // Act
        Long actualUserId = jwtUtil.extractUserId(token);

        // Assert
        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    void extractRole_ValidToken_ReturnsRole() {
        // Arrange
        String expectedRole = "ADMIN";
        String token = createTestToken(1L, expectedRole, "admin@example.com");

        // Act
        String actualRole = jwtUtil.extractRole(token);

        // Assert
        assertEquals(expectedRole, actualRole);
    }

    @Test
    void extractEmail_ValidToken_ReturnsEmail() {
        // Arrange
        String expectedEmail = "user@example.com";
        String token = createTestToken(1L, "STUDENT", expectedEmail);

        // Act
        String actualEmail = jwtUtil.extractEmail(token);

        // Assert
        assertEquals(expectedEmail, actualEmail);
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Arrange
        String token = createTestToken(1L, "STUDENT", "test@example.com");

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange
        String expiredToken = createExpiredToken(1L, "STUDENT", "test@example.com");

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(expiredToken);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String token = createTestToken(1L, "STUDENT", "test@example.com");

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void extractUserId_InvalidToken_ReturnsNull() {
        // Arrange
        String invalidToken = "invalid.token";

        // Act
        Long userId = jwtUtil.extractUserId(invalidToken);

        // Assert
        assertNull(userId);
    }

    private String createTestToken(Long userId, String role, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(signingKey)
                .compact();
    }

    private String createExpiredToken(Long userId, String role, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .signWith(signingKey)
                .compact();
    }
}
