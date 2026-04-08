package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.enums.MeetingPlatform;
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

    // Option 1: Génération automatique avec plateforme
    private MeetingPlatform platform;

    // Option 2: Lien manuel (si platform est MANUAL ou null)
    @Size(max = 500)
    private String meetingLink;

    @Size(max = 1000)
    private String notes;

    private String meetingTitle; // Titre de la réunion
    private Integer durationMinutes; // Durée en minutes (défaut: 60)
}
