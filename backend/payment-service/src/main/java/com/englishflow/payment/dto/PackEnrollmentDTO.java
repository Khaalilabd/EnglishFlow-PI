package com.englishflow.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for pack enrollment information from courses-service.
 * Used to check pack progress for refund eligibility.
 */
@Data
public class PackEnrollmentDTO {
    private Long id;
    private Long studentId;
    private Long packId;
    private String packName;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private String status;
    private Integer progressPercentage;
    private Boolean isActive;
    private Integer completedCourses;
    private Integer totalCourses;
}
