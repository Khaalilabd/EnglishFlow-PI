package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {
    private Long id;
    private Long progressId;
    private Integer pageNumber;
    private String note;
    private LocalDateTime createdAt;
}
