package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.enums.MeetingPlatform;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMeetingLinkRequest {

    @NotNull(message = "Meeting platform is required")
    private MeetingPlatform platform;

    @NotNull(message = "Interview date and time is required")
    private LocalDateTime interviewScheduledAt;

    private String title; // Titre de la réunion
    private String description; // Description optionnelle
    private Integer durationMinutes; // Durée en minutes (par défaut 60)
}
