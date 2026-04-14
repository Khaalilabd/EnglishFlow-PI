package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("field", "error message");
        
        ErrorResponse response = new ErrorResponse(
            now, 400, "Bad Request", "Invalid input", "/api/test", errors
        );
        
        assertEquals(now, response.getTimestamp());
        assertEquals(400, response.getStatus());
        assertEquals("Bad Request", response.getError());
        assertEquals("Invalid input", response.getMessage());
        assertEquals("/api/test", response.getPath());
        assertEquals(errors, response.getValidationErrors());
    }

    @Test
    void testPartialConstructor() {
        ErrorResponse response = new ErrorResponse(
            404, "Not Found", "Resource not found", "/api/resource"
        );
        
        assertNotNull(response.getTimestamp());
        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
        assertEquals("/api/resource", response.getPath());
        assertNull(response.getValidationErrors());
    }

    @Test
    void testSettersAndGetters() {
        ErrorResponse response = new ErrorResponse();
        LocalDateTime now = LocalDateTime.now();
        Map<String, String> errors = new HashMap<>();
        errors.put("email", "Invalid email format");
        
        response.setTimestamp(now);
        response.setStatus(422);
        response.setError("Unprocessable Entity");
        response.setMessage("Validation failed");
        response.setPath("/api/validate");
        response.setValidationErrors(errors);
        
        assertEquals(now, response.getTimestamp());
        assertEquals(422, response.getStatus());
        assertEquals("Unprocessable Entity", response.getError());
        assertEquals("Validation failed", response.getMessage());
        assertEquals("/api/validate", response.getPath());
        assertEquals(errors, response.getValidationErrors());
    }

    @Test
    void testWithMultipleValidationErrors() {
        Map<String, String> errors = new HashMap<>();
        errors.put("username", "Username is required");
        errors.put("password", "Password must be at least 8 characters");
        errors.put("email", "Invalid email format");
        
        ErrorResponse response = new ErrorResponse(
            400, "Bad Request", "Validation failed", "/api/register"
        );
        response.setValidationErrors(errors);
        
        assertEquals(3, response.getValidationErrors().size());
        assertTrue(response.getValidationErrors().containsKey("username"));
        assertTrue(response.getValidationErrors().containsKey("password"));
        assertTrue(response.getValidationErrors().containsKey("email"));
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        
        ErrorResponse response1 = new ErrorResponse(
            now, 400, "Bad Request", "Error", "/api/test", null
        );
        ErrorResponse response2 = new ErrorResponse(
            now, 400, "Bad Request", "Error", "/api/test", null
        );
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testToString() {
        ErrorResponse response = new ErrorResponse(
            404, "Not Found", "Resource not found", "/api/resource"
        );
        
        String toString = response.toString();
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
    }
}
