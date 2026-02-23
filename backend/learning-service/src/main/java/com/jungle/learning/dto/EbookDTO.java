package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private String thumbnailUrl;
    private String level;
    private String category;
    
    // Pricing
    private Boolean free;
    private BigDecimal price;
    private String pricingModel;
    
    // Stats
    private Integer downloadCount;
    private Integer viewCount;
    private Double averageRating;
    private Integer reviewCount;
    
    // Publishing
    private String status;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledFor;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
