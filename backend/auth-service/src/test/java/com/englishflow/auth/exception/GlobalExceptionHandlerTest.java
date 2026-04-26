package com.englishflow.auth.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() {
        UserNotFoundException exception = new UserNotFoundException(1L);
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(exception, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("1"));
    }

    @Test
    void handleInvalidTokenException_ShouldReturnUnauthorized() {
        InvalidTokenException exception = new InvalidTokenException("Activation");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidTokenException(exception, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Activation"));
    }

    @Test
    void handleTokenExpiredException_ShouldReturnUnauthorized() {
        TokenExpiredException exception = new TokenExpiredException("Refresh");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTokenExpiredException(exception, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Refresh"));
    }

    @Test
    void handleAccountNotActivatedException_ShouldReturnForbidden() {
        AccountNotActivatedException exception = new AccountNotActivatedException("test@example.com");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccountNotActivatedException(exception, request);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("test@example.com"));
    }

    @Test
    void handleRateLimitExceededException_ShouldReturnTooManyRequests() {
        RateLimitExceededException exception = new RateLimitExceededException("login", 60L);
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRateLimitExceededException(exception, request);
        
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("login"));
        assertNotNull(response.getBody().getAdditionalInfo());
        assertEquals(60L, response.getBody().getAdditionalInfo().get("retryAfterSeconds"));
    }

    @Test
    void handleInvitationExpiredException_ShouldReturnGone() {
        InvitationExpiredException exception = new InvitationExpiredException("Invitation has expired");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvitationExpiredException(exception, request);
        
        assertEquals(HttpStatus.GONE, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleInvitationAlreadyUsedException_ShouldReturnConflict() {
        InvitationAlreadyUsedException exception = new InvitationAlreadyUsedException("Invitation already used");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvitationAlreadyUsedException(exception, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleEmailAlreadyExistsException_ShouldReturnConflict() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("test@example.com");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmailAlreadyExistsException(exception, request);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("test@example.com"));
    }

    @Test
    void handleInvalidCredentialsException_ShouldReturnUnauthorized() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCredentialsException(exception, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleRecaptchaVerificationException_ShouldReturnBadRequest() {
        RecaptchaVerificationException exception = new RecaptchaVerificationException("Recaptcha failed");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRecaptchaVerificationException(exception, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleSessionNotFoundException_ShouldReturnNotFound() {
        SessionNotFoundException exception = new SessionNotFoundException("Session not found");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSessionNotFoundException(exception, request);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleUnauthorizedSessionAccessException_ShouldReturnForbidden() {
        UnauthorizedSessionAccessException exception = new UnauthorizedSessionAccessException("Unauthorized access");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnauthorizedSessionAccessException(exception, request);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleFileStorageException_ShouldReturnInternalServerError() {
        FileStorageException exception = new FileStorageException("File storage error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleFileStorageException(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleEmailSendException_ShouldReturnInternalServerError() {
        EmailSendException exception = new EmailSendException("Email send failed", new RuntimeException());
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleEmailSendException(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleBadCredentialsException_ShouldReturnUnauthorized() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadCredentialsException(exception, request);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbidden() {
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAccessDeniedException(exception, request);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleRuntimeException_ShouldReturnBadRequest() {
        RuntimeException exception = new RuntimeException("Runtime error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRuntimeException(exception, request);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Generic error");
        
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleException(exception, request);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
