package com.englishflow.auth.dto.recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarAvailabilityResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long interviewerId;
    private String interviewerName;
    
    private List<CalendarEventResponse> scheduledEvents;
    private List<TimeSlot> busySlots;
    private List<TimeSlot> availableSlots; // Optionnel : créneaux suggérés
    
    private boolean hasConflicts;
    private String message;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimeSlot {
        private LocalDate date;
        private String startTime; // Format HH:mm
        private String endTime;   // Format HH:mm
        private boolean isAvailable;
    }
}
