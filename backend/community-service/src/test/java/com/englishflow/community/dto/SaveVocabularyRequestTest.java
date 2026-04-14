package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SaveVocabularyRequestTest {

    @Test
    void testNoArgsConstructor() {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        assertNotNull(request);
    }

    @Test
    void testSettersAndGetters() {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        request.setWord("test");
        request.setDefinition("A test word");
        request.setExample("This is a test");
        request.setCategory("General");
        
        assertEquals("test", request.getWord());
        assertEquals("A test word", request.getDefinition());
        assertEquals("This is a test", request.getExample());
        assertEquals("General", request.getCategory());
    }

    @Test
    void testToString() {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        request.setWord("test");
        String toString = request.toString();
        assertTrue(toString.contains("test"));
    }
}
