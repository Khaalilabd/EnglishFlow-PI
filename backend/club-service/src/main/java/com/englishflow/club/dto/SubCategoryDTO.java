package com.englishflow.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private Boolean requiresClubMembership;
    private Boolean requiresAdminRole;
    private Boolean isLocked;
    private Integer topicsCount;
}
