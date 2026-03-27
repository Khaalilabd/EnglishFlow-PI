package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProgressRequest {
    private Long ebookId;
    private Integer currentPage;
    private Integer totalPages;
    private Integer readingTimeMinutes;
}
