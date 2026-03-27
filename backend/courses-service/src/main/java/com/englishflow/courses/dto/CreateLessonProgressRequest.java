package com.englishflow.courses.dto;

import lombok.Data;

@Data
public class CreateLessonProgressRequest {
    private Long studentId;
    private Long lessonId;
    private Long courseId;
    private Boolean isCompleted;
    private Integer timeSpent;
}
