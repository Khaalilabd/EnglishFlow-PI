package com.englishflow.auth.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "test-secret-key-for-jwt-token-generation-must-be-long-enough-for-hs512-algorithm";
    private static final Long TEST_EXPIRATION = 900000L; // 15 minutes

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
    }

    @Test
    void testGenerateToken_Success() {
        // Given
        String email = "test@example.com";
        String role = "STUDENT";
        Long userId = 1L;

        // When
        String token = jwtUtil.generateToken(email, role, userId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testExtractEmail_Success() {
        // Given
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email, "STUDENT", 1L);

        // When
        String extractedEmail = jwtUtil.extractEmail(token);

        // Then
        assertEquals(email, extractedEmail);
    }

    @Test
    void testExtractUserId_Success() {
        // Given
        Long userId = 123L;
        String token = jwtUtil.generateToken("test@example.com", "STUDENT", userId);

        // When
        Long extractedUserId = jwtUtil.extractUserId(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testExtractRole_Success() {
        // Given
        String role = "ADMIN";
        String token = jwtUtil.generateToken("admin@example.com", role, 1L);

        // When
        String extractedRole = jwtUtil.extractRole(token);

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Given
        String token = jwtUtil.generateToken("test@example.com", "STUDENT", 1L);

        // When
        boolean isValid = jwtUtil.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Given
        String invalidToken = "invalid.jwt.token";

        // When
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired_NotExpired() {
        // Given
        String token = jwtUtil.generateToken("test@example.com", "STUDENT", 1L);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpired_ExpiredToken() {
        // Given - Create a token with very short expiration
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", 1L); // 1ms
        
        String token = shortExpirationJwtUtil.generateToken("test@example.com", "STUDENT", 1L);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        boolean isExpired = shortExpirationJwtUtil.isTokenExpired(token);

        // Then
        assertTrue(isExpired);
    }

    @Test
    void testExtractExpiration_Success() {
        // Given
        String token = jwtUtil.generateToken("test@example.com", "STUDENT", 1L);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testGetExpirationTimeInSeconds() {
        // When
        long expirationSeconds = jwtUtil.getExpirationTimeInSeconds();

        // Then
        assertEquals(TEST_EXPIRATION / 1000, expirationSeconds);
    }

    @Test
    void testExtractEmail_InvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(JwtException.class, () -> {
            jwtUtil.extractEmail(invalidToken);
        });
    }

    @Test
    void testGenerateToken_MultipleRoles() {
        // Given
        String[] roles = {"STUDENT", "TUTOR", "ADMIN", "ACADEMIC_OFFICE_AFFAIR"};

        // When & Then
        for (String role : roles) {
            String token = jwtUtil.generateToken("test@example.com", role, 1L);
            assertNotNull(token);
            assertEquals(role, jwtUtil.extractRole(token));
        }
    }

    @Test
    void testTokenConsistency() {
        // Given
        String email = "test@example.com";
        String role = "STUDENT";
        Long userId = 1L;

        // When
        String token1 = jwtUtil.generateToken(email, role, userId);
        String token2 = jwtUtil.generateToken(email, role, userId);

        // Then - Tokens should be different (due to timestamp)
        assertNotEquals(token1, token2);
        
        // But should contain same data
        assertEquals(jwtUtil.extractEmail(token1), jwtUtil.extractEmail(token2));
        assertEquals(jwtUtil.extractRole(token1), jwtUtil.extractRole(token2));
        assertEquals(jwtUtil.extractUserId(token1), jwtUtil.extractUserId(token2));
    }
}
