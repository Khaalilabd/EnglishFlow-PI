package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonSessionDTO {
    private Long id;
    private Long onlineLessonId;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private String status;
    private String meetingUrl;
    private String recordingUrl;
    private String courseName;
    private String lessonTitle;
    private Integer durationMinutes;
    private String timezone;
}
