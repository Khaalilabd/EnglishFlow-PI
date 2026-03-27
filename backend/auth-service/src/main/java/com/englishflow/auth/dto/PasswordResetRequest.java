package com.englishflow.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PasswordResetRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
