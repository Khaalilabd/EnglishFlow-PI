package com.englishflow.sponsors.exception;

public class SponsorNotFoundException extends RuntimeException {
    public SponsorNotFoundException(Long id) {
        super("Sponsor not found with id: " + id);
    }
    
    public SponsorNotFoundException(String message) {
        super(message);
    }
}
