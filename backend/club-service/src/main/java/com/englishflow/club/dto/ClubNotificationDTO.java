package com.englishflow.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubNotificationDTO {
    private String type; // CLUB_CREATED, CLUB_UPDATED, MEMBER_JOINED, MEMBER_LEFT, TASK_CREATED, etc.
    private Long clubId;
    private String clubName;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
}
