package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptResultDTO {
    private Long attemptId;
    private Long quizId;
    private String quizTitle; // Added to display quiz name
    private Long studentId;
    private Integer score;
    private Integer maxScore;
    private Boolean passed;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private String status;
    private Map<Long, AnswerDetail> answerDetails; // questionId -> detail

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerDetail {
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Integer pointsEarned;
    }
}
