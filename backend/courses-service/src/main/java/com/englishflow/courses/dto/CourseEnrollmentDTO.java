package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollmentDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private Boolean isActive;
    private Double progress;
    private Integer completedLessons;
    private Integer totalLessons;
    private LocalDateTime lastAccessedAt;
}