package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressDTO {
    
    private Long id;
    private Long studentId;
    private Long lessonId;
    private String lessonTitle;
    private Boolean isCompleted;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
    private Integer timeSpentMinutes;
    private Double progressPercentage;
    private String notes;
}