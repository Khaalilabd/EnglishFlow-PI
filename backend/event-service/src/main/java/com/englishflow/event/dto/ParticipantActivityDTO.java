package com.englishflow.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantActivityDTO {
    private String activityType; // JOINED, LEFT, CHECKED_IN
    private Long eventId;
    private Long userId;
    private String userName;
    private Integer currentParticipants;
    private Integer maxParticipants;
    private LocalDateTime timestamp;
}
