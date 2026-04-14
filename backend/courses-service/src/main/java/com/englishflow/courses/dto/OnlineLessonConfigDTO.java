package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineLessonConfigDTO {
    private Long lessonId;
    private Integer duration;
    private String timezone;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ScheduleDTO> schedules;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleDTO {
        private Integer dayOfWeek; // 0=Sunday, 1=Monday, ..., 6=Saturday
        private String time; // HH:mm format
    }
}
