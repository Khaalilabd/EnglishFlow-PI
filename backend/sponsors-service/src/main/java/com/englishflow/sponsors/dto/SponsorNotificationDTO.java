package com.englishflow.sponsors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsorNotificationDTO {
    private String type; // CREATED, UPDATED, DELETED
    private Long sponsorId;
    private String sponsorName;
    private String message;
    private LocalDateTime timestamp;
    private SponsorDTO sponsor; // Full sponsor data for CREATED/UPDATED
}
