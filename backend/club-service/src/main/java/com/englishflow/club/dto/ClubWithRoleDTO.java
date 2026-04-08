package com.englishflow.club.dto;

import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.RankType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> origin/club/event-service

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClubWithRoleDTO {
    
    private Integer id;
    private String name;
    private String description;
    private String objective;
    private ClubCategory category;
    private Integer maxMembers;
<<<<<<< HEAD
=======
    private Double registrationFee;
>>>>>>> origin/club/event-service
    private String image;
    private ClubStatus status;
    private Integer createdBy;
    private Integer reviewedBy;
    private String reviewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
<<<<<<< HEAD
=======
    private List<SkillDTO> skills;
>>>>>>> origin/club/event-service
    
    // Member information
    private RankType userRole; // Role of the user in this club
    private LocalDateTime joinedAt; // When the user joined this club
}
