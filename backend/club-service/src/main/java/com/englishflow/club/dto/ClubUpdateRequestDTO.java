package com.englishflow.club.dto;

import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.UpdateRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubUpdateRequestDTO {
    
    private Integer id;
    private Integer clubId;
    private Long requestedBy;
    private String name;
    private String description;
    private String objective;
    private ClubCategory category;
    private Integer maxMembers;
    private String image;
    private UpdateRequestStatus status;
    private Boolean vicePresidentApproved;
    private Boolean secretaryApproved;
    private LocalDateTime createdAt;
    private LocalDateTime appliedAt;
}
