package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RateLimitExceededException extends RuntimeException {
    
    private final long retryAfterSeconds;
    
    public RateLimitExceededException(String action, long retryAfterSeconds) {
        super(String.format("Rate limit exceeded for %s. Please try again in %d seconds.", action, retryAfterSeconds));
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
