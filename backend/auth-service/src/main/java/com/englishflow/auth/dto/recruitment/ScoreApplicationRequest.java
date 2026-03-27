package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreApplicationRequest {

    @Min(0)
    @Max(100)
    private Integer qualificationScore;

    @Min(0)
    @Max(100)
    private Integer presentationScore;

    @Min(0)
    @Max(100)
    private Integer overallScore;
}
