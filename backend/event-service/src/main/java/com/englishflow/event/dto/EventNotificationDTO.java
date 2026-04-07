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
public class EventNotificationDTO {
    private String type; // EVENT_CREATED, EVENT_UPDATED, EVENT_CANCELLED, PARTICIPANT_JOINED, etc.
    private Long eventId;
    private String eventTitle;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
}
