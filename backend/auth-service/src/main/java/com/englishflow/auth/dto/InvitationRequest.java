package com.englishflow.auth.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(STUDENT|TUTOR|ACADEMIC_STAFF|ADMIN)$",
             message = "Role must be one of: STUDENT, TUTOR, ACADEMIC_STAFF, ADMIN")
    private String role;
}
