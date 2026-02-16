package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String instructor;
    private String level;
    private Integer durationHours;
    private Double price;
    private Boolean isPublished;
    private Integer enrolledCount;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

