package com.englishflow.messaging.service;

import com.englishflow.messaging.exception.RateLimitExceededException;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final LoadingCache<Long, RateLimiter> rateLimiterCache;

    @Value("${rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    public void checkRateLimit(Long userId) {
        if (!rateLimitEnabled) {
            return;
        }

        try {
            RateLimiter rateLimiter = rateLimiterCache.get(userId);
            if (!rateLimiter.tryAcquire()) {
                log.warn("Rate limit exceeded for user: {}", userId);
                throw new RateLimitExceededException("Too many messages. Please slow down.");
            }
        } catch (Exception e) {
            if (e instanceof RateLimitExceededException) {
                throw (RateLimitExceededException) e;
            }
            log.error("Error checking rate limit for user: {}", userId, e);
        }
    }
}
