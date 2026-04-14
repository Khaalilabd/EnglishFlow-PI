package com.englishflow.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SponsorRegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$",
             message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    // Relaxed phone — any non-blank string up to 20 chars
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    // Relaxed CIN — any alphanumeric up to 20 chars
    @Size(max = 20, message = "CIN must not exceed 20 characters")
    private String cin;

    // Optional profile fields
    private String dateOfBirth;   // stored as String in User entity anyway
    private String address;
    private String city;

    @Pattern(regexp = "^[0-9]{4,10}$", message = "Invalid postal code format")
    private String postalCode;

    private String nationality;   // stored in bio field
}
