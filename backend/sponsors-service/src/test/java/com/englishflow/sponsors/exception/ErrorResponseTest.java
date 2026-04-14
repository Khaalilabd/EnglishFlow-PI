package com.englishflow.sponsors.exception;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void builder_ShouldCreateErrorResponseWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(now)
                .status(404)
                .error("Not Found")
                .message("Resource not found")
                .build();

        assertThat(errorResponse.getTimestamp()).isEqualTo(now);
        assertThat(errorResponse.getStatus()).isEqualTo(404);
        assertThat(errorResponse.getError()).isEqualTo("Not Found");
        assertThat(errorResponse.getMessage()).isEqualTo("Resource not found");
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();

        assertThat(errorResponse.getTimestamp()).isNull();
        assertThat(errorResponse.getStatus()).isZero();
        assertThat(errorResponse.getError()).isNull();
        assertThat(errorResponse.getMessage()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldCreateErrorResponseWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse errorResponse = new ErrorResponse(now, 500, "Internal Server Error", "Something went wrong");

        assertThat(errorResponse.getTimestamp()).isEqualTo(now);
        assertThat(errorResponse.getStatus()).isEqualTo(500);
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Something went wrong");
    }

    @Test
    void setters_ShouldUpdateFields() {
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();

        errorResponse.setTimestamp(now);
        errorResponse.setStatus(400);
        errorResponse.setError("Bad Request");
        errorResponse.setMessage("Invalid input");

        assertThat(errorResponse.getTimestamp()).isEqualTo(now);
        assertThat(errorResponse.getStatus()).isEqualTo(400);
        assertThat(errorResponse.getError()).isEqualTo("Bad Request");
        assertThat(errorResponse.getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void builder_WithPartialFields_ShouldCreateErrorResponse() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(403)
                .error("Forbidden")
                .build();

        assertThat(errorResponse.getStatus()).isEqualTo(403);
        assertThat(errorResponse.getError()).isEqualTo("Forbidden");
        assertThat(errorResponse.getTimestamp()).isNull();
        assertThat(errorResponse.getMessage()).isNull();
    }
}
