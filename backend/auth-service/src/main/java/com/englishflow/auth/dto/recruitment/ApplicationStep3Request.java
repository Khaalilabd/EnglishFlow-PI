package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStep3Request {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Motivation letter is required")
    @Size(min = 100, max = 2000, message = "Motivation letter must be between 100 and 2000 characters")
    private String motivationLetter;

    @NotBlank(message = "Teaching philosophy is required")
    @Size(min = 50, max = 1000, message = "Teaching philosophy must be between 50 and 1000 characters")
    private String teachingPhilosophy;

    @NotBlank(message = "Availability is required")
    @Size(max = 500)
    private String availability;
}
