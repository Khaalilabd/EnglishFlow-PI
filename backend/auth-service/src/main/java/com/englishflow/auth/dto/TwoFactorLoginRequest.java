package com.englishflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorLoginRequest {
    
    @NotBlank(message = "Temporary token is required")
    private String tempToken;
    
    @NotBlank(message = "2FA code is required")
    private String code;
}
