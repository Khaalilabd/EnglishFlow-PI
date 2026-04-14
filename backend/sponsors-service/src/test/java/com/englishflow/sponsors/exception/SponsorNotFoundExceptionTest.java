package com.englishflow.sponsors.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SponsorNotFoundExceptionTest {

    @Test
    void constructor_WithId_ShouldCreateExceptionWithFormattedMessage() {
        Long sponsorId = 123L;
        
        SponsorNotFoundException exception = new SponsorNotFoundException(sponsorId);
        
        assertThat(exception.getMessage()).isEqualTo("Sponsor not found with id: 123");
    }

    @Test
    void constructor_WithCustomMessage_ShouldCreateExceptionWithMessage() {
        String customMessage = "Custom error message";
        
        SponsorNotFoundException exception = new SponsorNotFoundException(customMessage);
        
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void exception_ShouldBeRuntimeException() {
        SponsorNotFoundException exception = new SponsorNotFoundException(1L);
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void constructor_WithZeroId_ShouldCreateException() {
        SponsorNotFoundException exception = new SponsorNotFoundException(0L);
        
        assertThat(exception.getMessage()).isEqualTo("Sponsor not found with id: 0");
    }

    @Test
    void constructor_WithNegativeId_ShouldCreateException() {
        SponsorNotFoundException exception = new SponsorNotFoundException(-1L);
        
        assertThat(exception.getMessage()).isEqualTo("Sponsor not found with id: -1");
    }
}
