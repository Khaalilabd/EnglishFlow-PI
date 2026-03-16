package com.englishflow.auth.dto.recruitment;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleInterviewRequest {

    @NotNull(message = "Interview date and time is required")
    private LocalDateTime interviewScheduledAt;

    @Size(max = 500)
    private String meetingLink;

    @Size(max = 1000)
    private String notes;
}
