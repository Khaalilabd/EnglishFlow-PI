package com.englishflow.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveVocabularyRequest {
    private String word;
    private String definition;
    private String phonetic;
    private String partOfSpeech;
    private String example;
    private String synonyms;
    private String antonyms;
    private String audioUrl;
    private Long sourceTopicId;
}
