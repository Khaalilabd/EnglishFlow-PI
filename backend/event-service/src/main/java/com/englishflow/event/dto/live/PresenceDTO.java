package com.englishflow.event.dto.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PresenceDTO {
    private Integer eventId;
    private Long userId;
    private String userName;
    private String action; // "JOIN" | "LEAVE" | "RANK_UPDATE"
    private LocalDateTime joinedAt = LocalDateTime.now();
    private String rank;              // moderator rank if applicable
    private Long nextModeratorId;     // for LEAVE: who should become next moderator
    private String nextModeratorRank; // for LEAVE: their rank
    private String systemRole;        // system role: ACADEMIC_OFFICE_AFFAIR | SPONSOR | STUDENT | etc.
    private String profilePhoto;      // user profile photo URL
}
