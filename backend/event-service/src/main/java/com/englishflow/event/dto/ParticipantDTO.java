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
    private String eventTitle;
    private Double participationFee;
    private Long userId;
    private LocalDateTime joinDate;
    
    // User details
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String userProfilePhoto;

    private String clubRole;

    // Payment fields
    private String paymentStatus;
    private String paymentMethod;
    private String paymentToken;
    private LocalDateTime paymentConfirmedAt;
    private LocalDateTime paymentDeadline;
}
