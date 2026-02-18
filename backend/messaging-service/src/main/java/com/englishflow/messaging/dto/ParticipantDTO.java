package com.englishflow.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;
    private String userAvatar;
    private Boolean isOnline;
    private LocalDateTime lastReadAt;
}
