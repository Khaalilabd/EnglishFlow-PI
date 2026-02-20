package com.englishflow.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "First name contains invalid characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s'-]+$", message = "Last name contains invalid characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format (use international format, e.g., +212612345678)")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    @Size(max = 500, message = "Profile photo URL must not exceed 500 characters")
    private String profilePhoto;

    private String dateOfBirth;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    private String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Pattern(regexp = "^[0-9]{4,10}$", message = "Invalid postal code format")
    private String postalCode;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    @Pattern(regexp = "^[A-Z]{1,2}\\d{5,8}$", message = "Invalid CIN format (e.g., AB123456)")
    @Size(max = 20, message = "CIN must not exceed 20 characters")
    private String cin;

    @Pattern(regexp = "^(A1|A2|B1|B2|C1|C2)$", message = "English level must be one of: A1, A2, B1, B2, C1, C2")
    private String englishLevel;

    @Min(value = 0, message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience must not exceed 50")
    private Integer yearsOfExperience;
}
