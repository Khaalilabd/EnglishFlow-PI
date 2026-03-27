package com.englishflow.messaging.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
    
    public UnauthorizedAccessException(Long conversationId, Long userId) {
        super("User " + userId + " is not authorized to access conversation " + conversationId);
    }
}
