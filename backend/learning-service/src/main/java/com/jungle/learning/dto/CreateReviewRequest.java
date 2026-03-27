package com.jungle.learning.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    private Long ebookId;
    private Integer rating; // 1-5
    private String comment;
}
