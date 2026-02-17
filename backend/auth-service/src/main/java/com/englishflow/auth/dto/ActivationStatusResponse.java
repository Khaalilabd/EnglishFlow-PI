package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationStatusResponse {
    private boolean activated;
    private String token;  // JWT token if activated
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String profilePhoto;
    private String message;
}
