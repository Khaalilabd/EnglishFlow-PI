package com.englishflow.club.dto;

import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.RankType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private Double registrationFee;
    private String image;
    private ClubStatus status;
    private Integer createdBy;
    private Integer reviewedBy;
    private String reviewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SkillDTO> skills;
    
    // Member information
    private RankType userRole; // Role of the user in this club
    private LocalDateTime joinedAt; // When the user joined this club
}
