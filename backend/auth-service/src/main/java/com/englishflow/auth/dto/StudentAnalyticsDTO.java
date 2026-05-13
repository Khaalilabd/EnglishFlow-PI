package com.englishflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnalyticsDTO {
    
    private Long userId;
    private Integer totalClicks;
    private Integer totalSessions;
    private Integer avgClicksPerSession;
    private Integer maxClicksInSession;
    private Double avgScore;
    private Double minScore;
    private Double maxScore;
    private Integer totalAssessments;
    private Integer completedTMA;
    private Integer completedCMA;
    private Integer completedExams;
    private Integer previousAttempts;
    private Integer studiedCredits;
    private LocalDateTime lastActivityAt;
    private LocalDateTime firstRegistrationDate;
    private Boolean isUnregistered;
    
    // Lesson tracking
    private Integer totalLessonsOpened;
    private Integer totalTimeSpentMinutes;
    private Integer avgTimePerLesson;
    private LocalDateTime lastLessonOpenedAt;
}
