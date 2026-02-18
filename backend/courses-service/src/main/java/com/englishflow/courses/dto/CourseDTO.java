package com.englishflow.courses.dto;

import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.enums.EnglishLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long id;
    private String title;
    private String description;
    private EnglishLevel level;
    private Integer maxStudents;
    private LocalDateTime schedule;
    private Integer duration;
    private Long tutorId;
    private String fileUrl;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
