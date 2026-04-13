package com.englishflow.community.dto;

import lombok.Data;
import java.util.List;

@Data
public class EnrichedDictionaryResponse {
    private DictionaryResponse[] basicData;
    private String context;              // The sentence where the word was found
    private String cefrLevel;            // A1, A2, B1, B2, C1, C2
    private String wordType;             // Academic, Conversational, General
    private List<String> similarWords;   // Homophones, similar sounding words
    private List<String> commonConfusions; // Words often confused with this one
    private String imageUrl;             // Image from Unsplash (for nouns)
}
