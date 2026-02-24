package com.englishflow.courses.dto;

import com.englishflow.courses.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private String category; // Dynamic category
    private String level; // A1, A2, B1, B2, C1, C2
    private Integer maxStudents;
    private LocalDateTime schedule;
    private Integer duration; // in hours
    private Long tutorId;
    private String tutorName; // Added for display
    private BigDecimal price;
    private String fileUrl;
    private String thumbnailUrl;
    private String objectives;
    private String prerequisites;
    private Boolean isFeatured;
    private CourseStatus status;
    private Integer chapterCount; // Calculated field
    private Integer lessonCount; // Calculated field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
