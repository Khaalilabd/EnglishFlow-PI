package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressSummaryDTO {
    
    private Long studentId;
    private Long courseId;
    private String courseTitle;
    private CourseEnrollmentDTO enrollment;
    private List<ChapterProgressDTO> chapterProgress;
    private List<LessonProgressDTO> lessonProgress;
    private ProgressStatsDTO stats;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressStatsDTO {
        private Integer totalLessons;
        private Integer completedLessons;
        private Integer totalChapters;
        private Integer completedChapters;
        private Double overallProgress;
        private Integer totalTimeSpentMinutes;
    }
}