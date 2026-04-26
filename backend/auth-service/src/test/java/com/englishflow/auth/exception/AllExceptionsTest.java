package com.englishflow.auth.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AllExceptionsTest {

    @Test
    void accountNotActivatedException_ShouldCreateWithEmail() {
        String email = "test@example.com";
        AccountNotActivatedException exception = new AccountNotActivatedException(email);
        
        assertTrue(exception.getMessage().contains(email));
        assertTrue(exception.getMessage().contains("Account not activated"));
        assertNotNull(exception);
    }
    
    @Test
    void accountNotActivatedException_ShouldCreateWithMessageAndEmail() {
        String message = "Custom message";
        String email = "test@example.com";
        AccountNotActivatedException exception = new AccountNotActivatedException(message, email);
        
        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void emailAlreadyExistsException_ShouldCreateWithEmail() {
        String email = "test@example.com";
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException(email);
        
        assertTrue(exception.getMessage().contains(email));
        assertNotNull(exception);
    }

    @Test
    void emailSendException_ShouldCreateWithMessageAndCause() {
        String message = "Failed to send email";
        Throwable cause = new RuntimeException("SMTP error");
        EmailSendException exception = new EmailSendException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void fileStorageException_ShouldCreateWithMessage() {
        String message = "File storage error";
        FileStorageException exception = new FileStorageException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void fileStorageException_ShouldCreateWithMessageAndCause() {
        String message = "File storage error";
        Throwable cause = new RuntimeException("IO error");
        FileStorageException exception = new FileStorageException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void invalidCredentialsException_ShouldCreateWithMessage() {
        String message = "Invalid credentials";
        InvalidCredentialsException exception = new InvalidCredentialsException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void invalidTokenException_ShouldCreateWithMessage() {
        String message = "Invalid token";
        InvalidTokenException exception = new InvalidTokenException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void invitationAlreadyUsedException_ShouldCreateWithMessage() {
        String message = "Invitation already used";
        InvitationAlreadyUsedException exception = new InvitationAlreadyUsedException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void invitationExpiredException_ShouldCreateWithMessage() {
        String message = "Invitation expired";
        InvitationExpiredException exception = new InvitationExpiredException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void rateLimitExceededException_ShouldCreateWithActionAndRetryAfter() {
        String action = "login";
        long retryAfter = 60L;
        RateLimitExceededException exception = new RateLimitExceededException(action, retryAfter);
        
        assertTrue(exception.getMessage().contains(action));
        assertTrue(exception.getMessage().contains(String.valueOf(retryAfter)));
        assertEquals(retryAfter, exception.getRetryAfterSeconds());
    }

    @Test
    void recaptchaVerificationException_ShouldCreateWithMessage() {
        String message = "Recaptcha verification failed";
        RecaptchaVerificationException exception = new RecaptchaVerificationException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void sessionNotFoundException_ShouldCreateWithMessage() {
        String message = "Session not found";
        SessionNotFoundException exception = new SessionNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void tokenExpiredException_ShouldCreateWithTokenType() {
        String tokenType = "Refresh";
        TokenExpiredException exception = new TokenExpiredException(tokenType);
        
        assertTrue(exception.getMessage().contains(tokenType));
        assertTrue(exception.getMessage().contains("has expired"));
    }

    @Test
    void unauthorizedSessionAccessException_ShouldCreateWithMessage() {
        String message = "Unauthorized session access";
        UnauthorizedSessionAccessException exception = new UnauthorizedSessionAccessException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void userNotFoundException_ShouldCreateWithId() {
        Long userId = 123L;
        UserNotFoundException exception = new UserNotFoundException(userId);
        
        assertTrue(exception.getMessage().contains(userId.toString()));
    }

    @Test
    void userNotFoundException_ShouldCreateWithMessage() {
        String message = "User not found";
        UserNotFoundException exception = new UserNotFoundException(message);
        
        assertEquals(message, exception.getMessage());
    }

    @Test
    void errorResponse_ShouldCreateWithBuilder() {
        String message = "Error occurred";
        String error = "Bad Request";
        int status = 400;
        String path = "/api/test";
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error(error)
                .status(status)
                .path(path)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        assertEquals(message, errorResponse.getMessage());
        assertEquals(error, errorResponse.getError());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(path, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void errorResponse_ShouldHaveGettersAndSetters() {
        ErrorResponse errorResponse = new ErrorResponse();
        
        errorResponse.setMessage("Test message");
        errorResponse.setStatus(500);
        errorResponse.setPath("/test");
        errorResponse.setError("Internal Server Error");
        
        assertEquals("Test message", errorResponse.getMessage());
        assertEquals(500, errorResponse.getStatus());
        assertEquals("/test", errorResponse.getPath());
        assertEquals("Internal Server Error", errorResponse.getError());
    }
    
    @Test
    void errorResponse_ShouldCreateWithStaticMethod() {
        ErrorResponse errorResponse = ErrorResponse.of(404, "Not Found", "Resource not found", "/api/users/123");
        
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals("/api/users/123", errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }
    
    @Test
    void errorResponse_ValidationError_ShouldWork() {
        ErrorResponse.ValidationError validationError = ErrorResponse.ValidationError.builder()
                .field("email")
                .message("Invalid email format")
                .rejectedValue("invalid-email")
                .build();
        
        assertEquals("email", validationError.getField());
        assertEquals("Invalid email format", validationError.getMessage());
        assertEquals("invalid-email", validationError.getRejectedValue());
    }
}
