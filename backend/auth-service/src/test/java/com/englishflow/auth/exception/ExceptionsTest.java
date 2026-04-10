package com.englishflow.auth.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom exceptions
 */
class ExceptionsTest {

    @Test
    void testEmailAlreadyExistsException() {
        String email = "test@example.com";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(email);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(email));
    }

    @Test
    void testUserNotFoundException() {
        Long userId = 123L;
        UserNotFoundException exception = new UserNotFoundException(userId);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("123"));
    }

    @Test
    void testInvalidCredentialsException() {
        String message = "Invalid credentials";
        InvalidCredentialsException exception = new InvalidCredentialsException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testAccountNotActivatedException() {
        String email = "inactive@example.com";
        AccountNotActivatedException exception = new AccountNotActivatedException(email);
        
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains(email));
    }

    @Test
    void testRateLimitExceededException() {
        String identifier = "user@example.com";
        long retryAfter = 900L;
        RateLimitExceededException exception = new RateLimitExceededException(identifier, retryAfter);
        
        assertNotNull(exception);
        assertEquals(retryAfter, exception.getRetryAfterSeconds());
    }

    @Test
    void testInvalidTokenException() {
        String message = "Token expired";
        InvalidTokenException exception = new InvalidTokenException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testSessionNotFoundException() {
        String message = "Session not found";
        SessionNotFoundException exception = new SessionNotFoundException(message);
        
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }
}
