package com.englishflow.courses.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineMeetingSessionDTO {
    private Long id;
    private Long lessonId;
    private String roomId;
    private String inviteLink;
    private Long tutorId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Boolean isActive;
}
