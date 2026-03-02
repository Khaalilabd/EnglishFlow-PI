package com.englishflow.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResultRequest {
    private Long userId;
    private String assessedLevel; // A1, A2, B1, B2, C1, C2
    private Integer score; // Score du test (0-100)
    private Integer totalQuestions;
    private Integer correctAnswers;
}
