package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private Long quizId;
    private String content;
    private String type;
    private String options;
    private String correctAnswer;
    private Integer points;
    private Integer orderIndex;
    private Boolean partialCreditEnabled;
}
