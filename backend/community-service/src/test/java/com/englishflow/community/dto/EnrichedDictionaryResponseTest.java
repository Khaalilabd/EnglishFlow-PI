package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EnrichedDictionaryResponseTest {

    @Test
    void testNoArgsConstructor() {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        assertNotNull(response);
    }

    @Test
    void testSettersAndGetters() {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        response.setWord("test");
        response.setPhonetic("/test/");
        response.setDefinition("A test definition");
        response.setExample("An example");
        response.setPartOfSpeech("noun");
        response.setIsSaved(true);
        
        assertEquals("test", response.getWord());
        assertEquals("/test/", response.getPhonetic());
        assertEquals("A test definition", response.getDefinition());
        assertEquals("An example", response.getExample());
        assertEquals("noun", response.getPartOfSpeech());
        assertTrue(response.getIsSaved());
    }

    @Test
    void testToString() {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        response.setWord("test");
        String toString = response.toString();
        assertTrue(toString.contains("test"));
    }
}
