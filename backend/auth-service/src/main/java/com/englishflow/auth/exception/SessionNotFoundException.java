package com.englishflow.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SessionNotFoundException extends RuntimeException {
    
    public SessionNotFoundException(String message) {
        super(message);
    }
    
    public SessionNotFoundException(Long sessionId) {
        super(String.format("Session not found with ID: %s", sessionId));
    }
    
    public SessionNotFoundException(String field, String value) {
        super(String.format("Session not found with %s: %s", field, value));
    }
}
