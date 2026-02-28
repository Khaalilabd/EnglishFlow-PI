package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDTO {
    private Long id;
    private Long quizId;
    private String quizTitle; // Added to display quiz name in history
    private Long studentId;
    private Integer score;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private String status;
}
