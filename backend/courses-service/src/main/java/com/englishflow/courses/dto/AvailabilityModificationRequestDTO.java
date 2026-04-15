package com.englishflow.courses.dto;

import com.englishflow.courses.entity.AvailabilityModificationRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityModificationRequestDTO {
    
    private Long id;
    private Long tutorId;
    private String tutorName;
    private String tutorEmail;
    private String reason;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private Long reviewerId;
    private String reviewerName;
    private String reviewComment;
    private String proposedAvailability;
    
    // Constructor for creating new request
    public AvailabilityModificationRequestDTO(Long tutorId, String tutorName, String tutorEmail, String reason) {
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.tutorEmail = tutorEmail;
        this.reason = reason;
        this.status = AvailabilityModificationRequest.RequestStatus.PENDING.name();
    }
}
