package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbookChapterDTO {
    private Long id;
    private Long ebookId;
    private String title;
    private String description;
    private Integer orderIndex;
    private Integer startPage;
    private Integer endPage;
    private String fileUrl;
    private Boolean isFree;
}
