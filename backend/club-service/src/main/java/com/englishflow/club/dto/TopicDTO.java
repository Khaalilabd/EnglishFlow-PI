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
public class TopicDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;
    private Long subCategoryId;
    private Integer viewsCount;
    private Integer reactionsCount;
    private Integer likeCount;
    private Integer insightfulCount;
    private Integer helpfulCount;
    private Double weightedScore;
    private Boolean isTrending;
    private Boolean isPinned;
    private Boolean isLocked;
    private Integer postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String resourceType;
    private String resourceLink;
}
