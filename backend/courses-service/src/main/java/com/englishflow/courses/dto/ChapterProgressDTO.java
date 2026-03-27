package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChapterProgressDTO {
    
    private Long id;
    private Long studentId;
    private Long chapterId;
    private String chapterTitle;
    private Boolean isCompleted;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
    private Integer completedLessons;
    private Integer totalLessons;
    private Double progressPercentage;
}