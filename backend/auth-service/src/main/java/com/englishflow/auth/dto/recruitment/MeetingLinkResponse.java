package com.englishflow.auth.dto.recruitment;

import com.englishflow.auth.enums.MeetingPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingLinkResponse {
    private String meetingLink;
    private MeetingPlatform platform;
    private String meetingId;
    private String password; // Pour Zoom
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String additionalInfo;
}
