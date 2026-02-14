package com.englishflow.auth.dto;

import com.englishflow.auth.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phone;
    
    private String cin;

    @NotBlank(message = "Role is required")
    private String role;
    
    // Optional fields
    private String profilePhoto;
    private LocalDate dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String bio;
    private String englishLevel;
    private Integer yearsOfExperience;
    private String recaptchaToken;
}
