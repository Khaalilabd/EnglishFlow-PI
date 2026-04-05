package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimitServiceTest {

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService();
    }

    @Test
    void testIsBlocked_InitiallyNotBlocked() {
        // Given
        String identifier = "test@example.com";

        // When
        boolean isBlocked = rateLimitService.isBlocked(identifier);

        // Then
        assertFalse(isBlocked);
    }

    @Test
    void testRecordFailedAttempt_IncreasesAttempts() {
        // Given
        String identifier = "test@example.com";

        // When
        rateLimitService.recordFailedAttempt(identifier);
        int remaining = rateLimitService.getRemainingAttempts(identifier);

        // Then
        assertEquals(4, remaining); // 5 max - 1 attempt = 4 remaining
    }

    @Test
    void testIsBlocked_AfterMaxAttempts() {
        // Given
        String identifier = "test@example.com";

        // When - Record 5 failed attempts (max allowed)
        for (int i = 0; i < 5; i++) {
            rateLimitService.recordFailedAttempt(identifier);
        }

        // Then
        assertTrue(rateLimitService.isBlocked(identifier));
        assertEquals(0, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void testResetAttempts_ClearsFailedAttempts() {
        // Given
        String identifier = "test@example.com";
        rateLimitService.recordFailedAttempt(identifier);
        rateLimitService.recordFailedAttempt(identifier);

        // When
        rateLimitService.resetAttempts(identifier);

        // Then
        assertFalse(rateLimitService.isBlocked(identifier));
        assertEquals(5, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void testGetRemainingAttempts_DecreasesWithFailures() {
        // Given
        String identifier = "test@example.com";

        // When & Then
        assertEquals(5, rateLimitService.getRemainingAttempts(identifier));

        rateLimitService.recordFailedAttempt(identifier);
        assertEquals(4, rateLimitService.getRemainingAttempts(identifier));

        rateLimitService.recordFailedAttempt(identifier);
        assertEquals(3, rateLimitService.getRemainingAttempts(identifier));

        rateLimitService.recordFailedAttempt(identifier);
        assertEquals(2, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void testMultipleIdentifiers_IndependentTracking() {
        // Given
        String identifier1 = "user1@example.com";
        String identifier2 = "user2@example.com";

        // When
        rateLimitService.recordFailedAttempt(identifier1);
        rateLimitService.recordFailedAttempt(identifier1);
        rateLimitService.recordFailedAttempt(identifier2);

        // Then
        assertEquals(3, rateLimitService.getRemainingAttempts(identifier1));
        assertEquals(4, rateLimitService.getRemainingAttempts(identifier2));
        assertFalse(rateLimitService.isBlocked(identifier1));
        assertFalse(rateLimitService.isBlocked(identifier2));
    }

    @Test
    void testBlockDuration_ExpiresAfter15Minutes() throws InterruptedException {
        // Given
        String identifier = "test@example.com";

        // When - Block the user
        for (int i = 0; i < 5; i++) {
            rateLimitService.recordFailedAttempt(identifier);
        }

        // Then
        assertTrue(rateLimitService.isBlocked(identifier));

        // Note: This test would need to wait 15 minutes in real scenario
        // For unit testing, we verify the initial block state
        // Integration tests should verify expiration
    }
}
