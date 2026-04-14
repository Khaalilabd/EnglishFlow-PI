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
        DictionaryResponse basicData = new DictionaryResponse();
        basicData.setWord("test");
        response.setBasicData(new DictionaryResponse[]{basicData});
        response.setContext("A test context");
        response.setCefrLevel("B1");
        response.setWordType("General");
        
        assertNotNull(response.getBasicData());
        assertEquals("test", response.getBasicData()[0].getWord());
        assertEquals("A test context", response.getContext());
        assertEquals("B1", response.getCefrLevel());
        assertEquals("General", response.getWordType());
    }

    @Test
    void testToString() {
        EnrichedDictionaryResponse response = new EnrichedDictionaryResponse();
        DictionaryResponse basicData = new DictionaryResponse();
        basicData.setWord("test");
        response.setBasicData(new DictionaryResponse[]{basicData});
        String toString = response.toString();
        assertTrue(toString.contains("test"));
    }
}
