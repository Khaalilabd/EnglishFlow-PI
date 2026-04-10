package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStep2Request {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotBlank(message = "Education is required")
    @Size(max = 2000)
    private String education; // JSON string

    @Size(max = 2000)
    private String certifications; // JSON string

    @Size(max = 2000)
    private String workExperience; // JSON string

    @NotNull(message = "Years of experience is required")
    @Min(0)
    @Max(50)
    private Integer yearsOfExperience;

    @NotBlank(message = "English level is required")
    @Pattern(regexp = "^(A1|A2|B1|B2|C1|C2)$")
    private String englishLevel;

    @Size(max = 500)
    private String specializations;
}
