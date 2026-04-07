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
public class MemberActivityDTO {
    private String activityType; // JOINED, LEFT, PROMOTED, DEMOTED
    private Long clubId;
    private Long userId;
    private String userName;
    private String role;
    private LocalDateTime timestamp;
}
