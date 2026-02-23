package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgressDTO {
    private Long id;
    private Long ebookId;
    private String ebookTitle;
    private String ebookCoverUrl;
    private Long userId;
    private Integer currentPage;
    private Integer totalPages;
    private Double progressPercentage;
    private LocalDateTime lastReadAt;
    private Integer readingTimeMinutes;
    private Boolean isCompleted;
    private Integer bookmarksCount;
    private Integer notesCount;
}
