package com.englishflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptInvitationRequest {
    
    @NotBlank(message = "Token is required")
    private String token;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, message = "First name must be at least 2 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, message = "Last name must be at least 2 characters")
    private String lastName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private String phone;
    private String cin;
    private String dateOfBirth;
    private String address;
    private String city;
    private String postalCode;
    private String bio;
    private Integer yearsOfExperience;
}
