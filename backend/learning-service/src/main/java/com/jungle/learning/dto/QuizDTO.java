package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private Integer durationMin;
    private Integer maxScore;
    private Integer passingScore;
    private Boolean published;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuestionDTO> questions;
    
    // Advanced features
    private LocalDateTime publishAt;
    private Boolean shuffleQuestions;
    private Boolean shuffleOptions;
    private String showAnswersTiming;
    private String category;
    private String difficulty;
    private String tags;
}
