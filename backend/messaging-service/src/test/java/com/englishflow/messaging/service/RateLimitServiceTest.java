package com.englishflow.messaging.service;

import com.englishflow.messaging.exception.RateLimitExceededException;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {

    @Mock
    private LoadingCache<Long, RateLimiter> rateLimiterCache;

    @Mock
    private RateLimiter rateLimiter;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(rateLimitService, "rateLimitEnabled", true);
    }

    @Test
    void checkRateLimit_WhenRateLimitDisabled_ShouldNotThrow() throws Exception {
        // Arrange
        ReflectionTestUtils.setField(rateLimitService, "rateLimitEnabled", false);

        // Act & Assert
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit(1L));
        verify(rateLimiterCache, never()).get(anyLong());
    }

    @Test
    void checkRateLimit_WhenWithinLimit_ShouldNotThrow() throws Exception {
        // Arrange
        when(rateLimiterCache.get(1L)).thenReturn(rateLimiter);
        when(rateLimiter.tryAcquire()).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit(1L));
        verify(rateLimiterCache).get(1L);
        verify(rateLimiter).tryAcquire();
    }

    @Test
    void checkRateLimit_WhenExceedingLimit_ShouldThrowException() throws Exception {
        // Arrange
        when(rateLimiterCache.get(1L)).thenReturn(rateLimiter);
        when(rateLimiter.tryAcquire()).thenReturn(false);

        // Act & Assert
        assertThrows(RateLimitExceededException.class, 
            () -> rateLimitService.checkRateLimit(1L));
        verify(rateLimiterCache).get(1L);
        verify(rateLimiter).tryAcquire();
    }

    @Test
    void checkRateLimit_WhenCacheThrowsException_ShouldHandleGracefully() throws Exception {
        // Arrange
        when(rateLimiterCache.get(1L)).thenThrow(new RuntimeException("Cache error"));

        // Act & Assert
        assertDoesNotThrow(() -> rateLimitService.checkRateLimit(1L));
        verify(rateLimiterCache).get(1L);
    }
}
