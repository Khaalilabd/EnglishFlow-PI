package com.englishflow.messaging.exception;

public class MessageValidationException extends RuntimeException {
    public MessageValidationException(String message) {
        super(message);
    }
}
