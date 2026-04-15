package com.englishflow.messaging.service;

import com.englishflow.messaging.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    private RateLimitService rateLimitService;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        rateLimitService = new RateLimitService(5, true); // 5 messages per minute
    }

    @Test
    void checkRateLimit_WhenUnderLimit_ShouldPass() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> {
            rateLimitService.checkRateLimit(userId);
            rateLimitService.checkRateLimit(userId);
            rateLimitService.checkRateLimit(userId);
        });
    }

    @Test
    void checkRateLimit_WhenExceedsLimit_ShouldThrowException() {
        // Given - Fill up the rate limit
        for (int i = 0; i < 5; i++) {
            rateLimitService.checkRateLimit(userId);
        }

        // When & Then - Next call should throw exception
        assertThrows(RateLimitExceededException.class, 
            () -> rateLimitService.checkRateLimit(userId));
    }

    @Test
    void checkRateLimit_WhenDisabled_ShouldAlwaysPass() {
        // Given
        RateLimitService disabledService = new RateLimitService(5, false);

        // When & Then - Should not throw exception even after many calls
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 100; i++) {
                disabledService.checkRateLimit(userId);
            }
        });
    }

    @Test
    void checkRateLimit_DifferentUsers_ShouldHaveSeparateLimits() {
        // Given
        Long user1 = 1L;
        Long user2 = 2L;

        // When - Fill up user1's limit
        for (int i = 0; i < 5; i++) {
            rateLimitService.checkRateLimit(user1);
        }

        // Then - user2 should still be able to send messages
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit(user2));
        
        // But user1 should be blocked
        assertThrows(RateLimitExceededException.class, 
            () -> rateLimitService.checkRateLimit(user1));
    }
}
