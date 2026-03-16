package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectApplicationRequest {

    @NotBlank(message = "Rejection reason is required")
    @Size(min = 20, max = 1000, message = "Rejection reason must be between 20 and 1000 characters")
    private String reason;
}
