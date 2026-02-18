package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO {
    private Long id;
    private String title;
    private String description;
    private List<String> objectives;
    private Integer orderIndex;
    private Integer estimatedDuration;
    private Boolean isPublished;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
