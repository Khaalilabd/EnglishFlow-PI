package com.englishflow.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for course enrollment information from courses-service.
 * Used to check course progress for refund eligibility.
 */
@Data
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
