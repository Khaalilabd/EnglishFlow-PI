package com.englishflow.community.service;

import com.englishflow.community.dto.DictionaryResponse;
import com.englishflow.community.dto.EnrichedDictionaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DictionaryService {
    
    private static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private static final String UNSPLASH_API_URL = "https://api.unsplash.com/search/photos";
    private static final String UNSPLASH_ACCESS_KEY = "YOUR_UNSPLASH_ACCESS_KEY"; // À remplacer
    
    private final RestTemplate restTemplate;
    
    // CEFR levels mapping (simplified - can be expanded with a real database)
    private static final Map<String, String> CEFR_LEVELS = new HashMap<>();
    static {
        // A1 - Basic words
        CEFR_LEVELS.put("hello", "A1");
        CEFR_LEVELS.put("good", "A1");
        CEFR_LEVELS.put("bad", "A1");
        CEFR_LEVELS.put("yes", "A1");
        CEFR_LEVELS.put("no", "A1");
        
        // A2 - Elementary
        CEFR_LEVELS.put("important", "A2");
        CEFR_LEVELS.put("different", "A2");
        
        // B1 - Intermediate
        CEFR_LEVELS.put("essential", "B1");
        CEFR_LEVELS.put("significant", "B1");
        
        // B2 - Upper Intermediate
        CEFR_LEVELS.put("comprehensive", "B2");
        CEFR_LEVELS.put("substantial", "B2");
        
        // C1 - Advanced
        CEFR_LEVELS.put("meticulous", "C1");
        CEFR_LEVELS.put("ubiquitous", "C1");
        
        // C2 - Proficiency
        CEFR_LEVELS.put("quintessential", "C2");
        CEFR_LEVELS.put("ephemeral", "C2");
    }
    
    // Common confusions
    private static final Map<String, List<String>> COMMON_CONFUSIONS = new HashMap<>();
    static {
        COMMON_CONFUSIONS.put("affect", Arrays.asList("effect"));
        COMMON_CONFUSIONS.put("effect", Arrays.asList("affect"));
        COMMON_CONFUSIONS.put("accept", Arrays.asList("except"));
        COMMON_CONFUSIONS.put("except", Arrays.asList("accept"));
        COMMON_CONFUSIONS.put("their", Arrays.asList("there", "they're"));
        COMMON_CONFUSIONS.put("there", Arrays.asList("their", "they're"));
        COMMON_CONFUSIONS.put("your", Arrays.asList("you're"));
        COMMON_CONFUSIONS.put("you're", Arrays.asList("your"));
        COMMON_CONFUSIONS.put("its", Arrays.asList("it's"));
        COMMON_CONFUSIONS.put("it's", Arrays.asList("its"));
        COMMON_CONFUSIONS.put("lose", Arrays.asList("loose"));
        COMMON_CONFUSIONS.put("loose", Arrays.asList("lose"));
        COMMON_CONFUSIONS.put("than", Arrays.asList("then"));
        COMMON_CONFUSIONS.put("then", Arrays.asList("than"));
    }
    
    public DictionaryResponse[] lookupWord(String word) {
        try {
            log.info("Looking up word: {}", word);
            String cleanWord = word.toLowerCase().trim();
            
            // Try the original word first
            try {
                return tryLookup(cleanWord);
            } catch (HttpClientErrorException.NotFound e) {
                log.warn("Word '{}' not found, trying base forms...", cleanWord);
                
                // Try removing common suffixes
                String baseForm = getBaseForm(cleanWord);
                if (!baseForm.equals(cleanWord)) {
                    try {
                        log.info("Trying base form: {}", baseForm);
                        return tryLookup(baseForm);
                    } catch (HttpClientErrorException.NotFound e2) {
                        log.warn("Base form '{}' also not found", baseForm);
                    }
                }
                
                // If all attempts fail, throw the original exception
                throw new RuntimeException("Word not found in dictionary: " + word);
            }
        } catch (Exception e) {
            log.error("Error looking up word '{}': {} - {}", word, e.getClass().getSimpleName(), e.getMessage());
            throw new RuntimeException("Error looking up word: " + e.getMessage());
        }
    }
    
    private DictionaryResponse[] tryLookup(String word) {
        String url = DICTIONARY_API_URL + word;
        log.info("API URL: {}", url);
        
        DictionaryResponse[] response = restTemplate.getForObject(url, DictionaryResponse[].class);
        
        if (response != null && response.length > 0) {
            log.info("Successfully retrieved {} definition(s) for: {}", response.length, word);
            return response;
        } else {
            log.warn("Empty response for word: {}", word);
            throw new RuntimeException("No definitions found for: " + word);
        }
    }
    
    private String getBaseForm(String word) {
        // Remove common suffixes to get base form
        if (word.endsWith("ing") && word.length() > 5) {
            // mastering -> master, running -> run (with double consonant)
            String base = word.substring(0, word.length() - 3);
            // Check if last consonant is doubled (running -> run)
            if (base.length() >= 2 && base.charAt(base.length() - 1) == base.charAt(base.length() - 2)) {
                return base.substring(0, base.length() - 1);
            }
            return base;
        }
        if (word.endsWith("ed") && word.length() > 4) {
            // mastered -> master
            String base = word.substring(0, word.length() - 2);
            if (base.length() >= 2 && base.charAt(base.length() - 1) == base.charAt(base.length() - 2)) {
                return base.substring(0, base.length() - 1);
            }
            return base;
        }
        if (word.endsWith("s") && word.length() > 3 && !word.endsWith("ss")) {
            // masters -> master, but not "class"
            return word.substring(0, word.length() - 1);
        }
        if (word.endsWith("es") && word.length() > 4) {
            // matches -> match
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ies") && word.length() > 5) {
            // studies -> study
            return word.substring(0, word.length() - 3) + "y";
        }
        return word;
    }
    
    public EnrichedDictionaryResponse lookupWordEnriched(String word, String context) {
        // Get basic dictionary data
        DictionaryResponse[] basicResponse = lookupWord(word);
        
        // Enrich with additional data
        EnrichedDictionaryResponse enriched = new EnrichedDictionaryResponse();
        enriched.setBasicData(basicResponse);
        enriched.setContext(context);
        // Removed CEFR level and word type - not reliable enough
        enriched.setSimilarWords(getSimilarWords(word));
        enriched.setCommonConfusions(getCommonConfusions(word));
        enriched.setImageUrl(getImageUrl(word));
        
        return enriched;
    }
    
    // CEFR level and word type methods removed - not reliable enough without proper database
    
    private List<String> getSimilarWords(String word) {
        List<String> similar = new ArrayList<>();
        String lower = word.toLowerCase();
        
        // Add homophones and similar sounding words
        Map<String, List<String>> homophones = new HashMap<>();
        homophones.put("right", Arrays.asList("write", "rite"));
        homophones.put("write", Arrays.asList("right", "rite"));
        homophones.put("hear", Arrays.asList("here"));
        homophones.put("here", Arrays.asList("hear"));
        homophones.put("see", Arrays.asList("sea"));
        homophones.put("sea", Arrays.asList("see"));
        homophones.put("know", Arrays.asList("no"));
        homophones.put("no", Arrays.asList("know"));
        
        if (homophones.containsKey(lower)) {
            similar.addAll(homophones.get(lower));
        }
        
        return similar;
    }
    
    private List<String> getCommonConfusions(String word) {
        return COMMON_CONFUSIONS.getOrDefault(word.toLowerCase(), new ArrayList<>());
    }
    
    private String getImageUrl(String word) {
        // Only fetch images for nouns (simple heuristic)
        // In production, you'd want to check part of speech from dictionary API
        try {
            String url = UNSPLASH_API_URL + "?query=" + word + "&per_page=1&client_id=" + UNSPLASH_ACCESS_KEY;
            // Note: Unsplash API requires authentication
            // For now, return a placeholder or null
            // You can implement this properly with Unsplash API key
            return null; // Will be implemented with proper API key
        } catch (Exception e) {
            log.warn("Could not fetch image for word: {}", word);
            return null;
        }
    }
}
