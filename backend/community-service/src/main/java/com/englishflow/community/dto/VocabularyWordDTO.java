package com.englishflow.community.dto;

import com.englishflow.community.entity.VocabularyWord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyWordDTO {
    private Long id;
    private String word;
    private String definition;
    private String phonetic;
    private String partOfSpeech;
    private String example;
    private String synonyms;
    private String antonyms;
    private String audioUrl;
    private Long sourceTopicId;
    private VocabularyWord.MasteryLevel masteryLevel;
    private Integer reviewCount;
    private LocalDateTime lastReviewedAt;
    private LocalDateTime createdAt;
}
