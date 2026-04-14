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
        
        List<DictionaryResponse.Meaning> meanings = Arrays.asList(new DictionaryResponse.Meaning());
        response.setMeanings(meanings);
        
        assertEquals("test", response.getWord());
        assertEquals("/test/", response.getPhonetic());
        assertEquals(1, response.getMeanings().size());
    }

    @Test
    void testMeaningClass() {
        DictionaryResponse.Meaning meaning = new DictionaryResponse.Meaning();
        meaning.setPartOfSpeech("noun");
        
        List<DictionaryResponse.Definition> definitions = Arrays.asList(new DictionaryResponse.Definition());
        meaning.setDefinitions(definitions);
        
        assertEquals("noun", meaning.getPartOfSpeech());
        assertEquals(1, meaning.getDefinitions().size());
    }

    @Test
    void testDefinitionClass() {
        DictionaryResponse.Definition definition = new DictionaryResponse.Definition();
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
