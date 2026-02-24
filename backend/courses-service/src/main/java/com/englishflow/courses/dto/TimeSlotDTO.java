package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDTO {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
}
