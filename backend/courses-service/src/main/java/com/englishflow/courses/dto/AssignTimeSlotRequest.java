package com.englishflow.courses.dto;

import com.englishflow.courses.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTimeSlotRequest {
    private DayOfWeek dayOfWeek;
    private String startTime; // HH:mm format
    private String endTime;   // HH:mm format
}
