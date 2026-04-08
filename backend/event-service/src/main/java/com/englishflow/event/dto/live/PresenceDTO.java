package com.englishflow.event.dto.live;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PresenceDTO {
    private Integer eventId;
    private Long userId;
    private String userName;
    private String action; // "JOIN" | "LEAVE"
    private LocalDateTime joinedAt = LocalDateTime.now();
}
