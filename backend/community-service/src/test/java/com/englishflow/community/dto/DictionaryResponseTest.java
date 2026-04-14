package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DictionaryResponseTest {

    @Test
    void testNoArgsConstructor() {
        DictionaryResponse response = new DictionaryResponse();
        assertNotNull(response);
    }

    @Test
    void testSettersAndGetters() {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("test");
        response.setPhonetic("/test/");
        
        List<DictionaryResponse.MeaningDTO> meanings = Arrays.asList(new DictionaryResponse.MeaningDTO());
        response.setMeanings(meanings);
        
        assertEquals("test", response.getWord());
        assertEquals("/test/", response.getPhonetic());
        assertEquals(1, response.getMeanings().size());
    }

    @Test
    void testMeaningClass() {
        DictionaryResponse.MeaningDTO meaning = new DictionaryResponse.MeaningDTO();
        meaning.setPartOfSpeech("noun");
        
        List<DictionaryResponse.DefinitionDTO> definitions = Arrays.asList(new DictionaryResponse.DefinitionDTO());
        meaning.setDefinitions(definitions);
        
        assertEquals("noun", meaning.getPartOfSpeech());
        assertEquals(1, meaning.getDefinitions().size());
    }

    @Test
    void testDefinitionClass() {
        DictionaryResponse.DefinitionDTO definition = new DictionaryResponse.DefinitionDTO();
        definition.setDefinition("A test definition");
        definition.setExample("An example");
        
        assertEquals("A test definition", definition.getDefinition());
        assertEquals("An example", definition.getExample());
    }

    @Test
    void testToString() {
        DictionaryResponse response = new DictionaryResponse();
        response.setWord("test");
        String toString = response.toString();
        assertTrue(toString.contains("test"));
    }
}
