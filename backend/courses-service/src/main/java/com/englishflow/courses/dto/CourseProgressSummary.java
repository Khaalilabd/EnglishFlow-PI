package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseProgressSummary {
    private Long courseId;
    private Long studentId;
    private Integer totalLessons;
    private Integer completedLessons;
    private Double progressPercentage;
    private LocalDateTime lastAccessedAt;
}
