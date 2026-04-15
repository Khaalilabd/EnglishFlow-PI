package com.englishflow.courses.dto;

import com.englishflow.courses.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorOnlineLessonDTO {
    private Long lessonId;
    private String lessonTitle;
    private String courseTitle;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
