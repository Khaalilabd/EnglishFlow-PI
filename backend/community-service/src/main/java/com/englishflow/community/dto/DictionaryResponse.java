package com.englishflow.community.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryResponse {
    private String word;
    private String phonetic;
    private List<PhoneticDTO> phonetics;
    private List<MeaningDTO> meanings;
    private List<String> sourceUrls;
    private LicenseDTO license;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhoneticDTO {
        private String text;
        private String audio;
        private String sourceUrl;
        private LicenseDTO license;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeaningDTO {
        private String partOfSpeech;
        private List<DefinitionDTO> definitions;
        private List<String> synonyms;
        private List<String> antonyms;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DefinitionDTO {
        private String definition;
        private String example;
        private List<String> synonyms;
        private List<String> antonyms;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LicenseDTO {
        private String name;
        private String url;
    }
}
