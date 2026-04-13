package com.englishflow.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyStatsDTO {
    private Long totalWords;
    private Long newWords;
    private Long learningWords;
    private Long familiarWords;
    private Long masteredWords;
    private Long totalReviews;
}
