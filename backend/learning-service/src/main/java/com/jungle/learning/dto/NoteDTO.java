package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private Long progressId;
    private Integer pageNumber;
    private String content;
    private String highlightedText;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
