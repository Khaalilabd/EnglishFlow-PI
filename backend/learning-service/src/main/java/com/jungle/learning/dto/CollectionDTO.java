package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isPublic;
    private Long ownerId;
    private String ownerName;
    private List<Long> ebookIds;
    private Integer ebooksCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
