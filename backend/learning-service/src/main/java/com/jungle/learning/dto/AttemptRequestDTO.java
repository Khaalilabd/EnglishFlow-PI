package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptRequestDTO {
    private Long quizId;
    private Long studentId;
    private Map<Long, String> answers; // questionId -> answer
}
