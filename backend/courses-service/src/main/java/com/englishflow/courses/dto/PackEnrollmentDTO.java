package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackEnrollmentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long packId;
    private String packName;
    private String packCategory;
    private String packLevel;
    private Long tutorId;
    private String tutorName;
    private Integer totalCourses;
    private Integer completedCourses;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private String status; // ACTIVE, COMPLETED, CANCELLED
    private Integer progressPercentage;
    private Boolean isActive;
}
