package com.englishflow.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
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
    private Boolean isPinned;
    private Boolean isLocked;
    private Integer postsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
