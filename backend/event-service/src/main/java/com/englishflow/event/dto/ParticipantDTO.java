package com.englishflow.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    
    private Integer id;
    private Integer eventId;
    private Long userId;
    private LocalDateTime joinDate;
    
    // User details (fetched from auth-service)
    private String userEmail;
    private String userFirstName;
    private String userLastName;
}
