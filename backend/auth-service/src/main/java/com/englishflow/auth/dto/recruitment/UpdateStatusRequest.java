package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(DRAFT|SUBMITTED|UNDER_REVIEW|INTERVIEW_SCHEDULED|TEST_PENDING|TEST_COMPLETED|ACCEPTED|REJECTED|WITHDRAWN)$")
    private String status;

    @Size(max = 500)
    private String comment;
}
