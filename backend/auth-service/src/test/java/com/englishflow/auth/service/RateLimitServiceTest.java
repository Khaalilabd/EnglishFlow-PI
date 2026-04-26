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
    void isBlocked_WhenNoAttempts_ShouldReturnFalse() {
        // Act
        boolean result = rateLimitService.isBlocked("test@example.com");

        // Assert
        assertFalse(result);
    }

    @Test
    void recordFailedAttempt_ShouldIncrementAttempts() {
        // Arrange
        String identifier = "test@example.com";

        // Act
        rateLimitService.recordFailedAttempt(identifier);
        int remaining = rateLimitService.getRemainingAttempts(identifier);

        // Assert
        assertEquals(4, remaining);
    }

    @Test
    void isBlocked_AfterMaxAttempts_ShouldReturnTrue() {
        // Arrange
        String identifier = "blocked@example.com";

        // Act - Record 5 failed attempts
        for (int i = 0; i < 5; i++) {
            rateLimitService.recordFailedAttempt(identifier);
        }

        // Assert
        assertTrue(rateLimitService.isBlocked(identifier));
        assertEquals(0, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void resetAttempts_ShouldClearFailedAttempts() {
        // Arrange
        String identifier = "reset@example.com";
        rateLimitService.recordFailedAttempt(identifier);
        rateLimitService.recordFailedAttempt(identifier);

        // Act
        rateLimitService.resetAttempts(identifier);

        // Assert
        assertFalse(rateLimitService.isBlocked(identifier));
        assertEquals(5, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void getRemainingAttempts_ShouldReturnCorrectCount() {
        // Arrange
        String identifier = "attempts@example.com";

        // Act & Assert
        assertEquals(5, rateLimitService.getRemainingAttempts(identifier));

        rateLimitService.recordFailedAttempt(identifier);
        assertEquals(4, rateLimitService.getRemainingAttempts(identifier));

        rateLimitService.recordFailedAttempt(identifier);
        assertEquals(3, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void unblock_ShouldResetAttempts() {
        // Arrange
        String identifier = "unblock@example.com";
        for (int i = 0; i < 5; i++) {
            rateLimitService.recordFailedAttempt(identifier);
        }
        assertTrue(rateLimitService.isBlocked(identifier));

        // Act
        rateLimitService.unblock(identifier);

        // Assert
        assertFalse(rateLimitService.isBlocked(identifier));
        assertEquals(5, rateLimitService.getRemainingAttempts(identifier));
    }

    @Test
    void multipleIdentifiers_ShouldBeTrackedSeparately() {
        // Arrange
        String identifier1 = "user1@example.com";
        String identifier2 = "user2@example.com";

        // Act
        rateLimitService.recordFailedAttempt(identifier1);
        rateLimitService.recordFailedAttempt(identifier1);
        rateLimitService.recordFailedAttempt(identifier2);

        // Assert
        assertEquals(3, rateLimitService.getRemainingAttempts(identifier1));
        assertEquals(4, rateLimitService.getRemainingAttempts(identifier2));
    }
}
