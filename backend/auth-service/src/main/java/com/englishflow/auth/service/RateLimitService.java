package com.englishflow.auth.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class RateLimitService {

    private static final int MAX_ATTEMPTS = 5; // Maximum 5 attempts
    private static final int ATTEMPT_WINDOW_MINUTES = 15; // Within 15 minutes

    private final LoadingCache<String, AtomicInteger> attemptsCache;

    public RateLimitService() {
        this.attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(ATTEMPT_WINDOW_MINUTES, TimeUnit.MINUTES)
                .build(new CacheLoader<String, AtomicInteger>() {
                    @Override
                    public AtomicInteger load(String key) {
                        return new AtomicInteger(0);
                    }
                });
    }

    /**
     * Check if the identifier (email or IP) has exceeded the rate limit
     */
    public boolean isBlocked(String identifier) {
        try {
            AtomicInteger attempts = attemptsCache.get(identifier);
            int currentAttempts = attempts.get();
            
            if (currentAttempts >= MAX_ATTEMPTS) {
                log.warn("Rate limit exceeded for identifier: {}", identifier);
                return true;
            }
            
            return false;
        } catch (ExecutionException e) {
            log.error("Error checking rate limit for identifier: {}", identifier, e);
            return false; // Fail open - don't block on error
        }
    }

    /**
     * Record a failed login attempt
     */
    public void recordFailedAttempt(String identifier) {
        try {
            AtomicInteger attempts = attemptsCache.get(identifier);
            int currentAttempts = attempts.incrementAndGet();
            log.info("Failed login attempt {} for identifier: {}", currentAttempts, identifier);
            
            if (currentAttempts >= MAX_ATTEMPTS) {
                log.warn("Identifier {} has been rate limited after {} attempts", identifier, currentAttempts);
            }
        } catch (ExecutionException e) {
            log.error("Error recording failed attempt for identifier: {}", identifier, e);
        }
    }

    /**
     * Reset attempts for an identifier (called on successful login)
     */
    public void resetAttempts(String identifier) {
        attemptsCache.invalidate(identifier);
        log.info("Reset rate limit attempts for identifier: {}", identifier);
    }

    /**
     * Get remaining attempts for an identifier
     */
    public int getRemainingAttempts(String identifier) {
        try {
            AtomicInteger attempts = attemptsCache.get(identifier);
            int remaining = MAX_ATTEMPTS - attempts.get();
            return Math.max(0, remaining);
        } catch (ExecutionException e) {
            log.error("Error getting remaining attempts for identifier: {}", identifier, e);
            return MAX_ATTEMPTS;
        }
    }

    /**
     * Manually unblock an identifier (admin function)
     */
    public void unblock(String identifier) {
        attemptsCache.invalidate(identifier);
        log.info("Manually unblocked identifier: {}", identifier);
    }
}
