package com.jungle.learning.exception;

public class InvalidQuizAttemptException extends RuntimeException {
    public InvalidQuizAttemptException(String message) {
        super(message);
    }
}
