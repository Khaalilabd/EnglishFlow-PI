package com.englishflow.auth.dto.recruitment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarEventResponse {

    private Long scheduleId; // ID local dans notre DB
    private String googleEventId; // ID dans Google Calendar
    private String title;
    private String description;
    private LocalDateTime start;
    private LocalDateTime end;
    private Integer durationMinutes;
    private String meetingLink;
    private String platform;
    private String status;
    
    // Informations sur l'application liée
    private Long applicationId;
    private String candidateName;
    private String candidateEmail;
    
    // Source de l'événement
    private EventSource source;
    
    public enum EventSource {
        LOCAL_DB,           // Événement stocké localement
        GOOGLE_CALENDAR,    // Événement venant de Google Calendar
        BOTH                // Synchronisé dans les deux
    }
}
