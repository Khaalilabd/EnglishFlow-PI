package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EbookDTO {
    private Long id;
    private String title;
    private String description;
    private String fileUrl;
    private Long fileSize;
    private String mimeType;
    private String coverImageUrl;
    private String level;
    private String category;
    private Boolean free;
    private Integer downloadCount;
    private LocalDateTime createdAt;
}
