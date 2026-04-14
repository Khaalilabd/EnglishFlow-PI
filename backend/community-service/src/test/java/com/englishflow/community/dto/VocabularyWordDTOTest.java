package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class VocabularyWordDTOTest {

    @Test
    void testNoArgsConstructor() {
        VocabularyWordDTO dto = new VocabularyWordDTO();
        assertNotNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        VocabularyWordDTO dto = new VocabularyWordDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setWord("test");
        dto.setDefinition("A test word");
        dto.setExample("This is a test");
        dto.setPhonetic("/test/");
        dto.setPartOfSpeech("noun");
        dto.setCreatedAt(now);
        
        assertEquals(1L, dto.getId());
        assertEquals("test", dto.getWord());
        assertEquals("A test word", dto.getDefinition());
        assertEquals("This is a test", dto.getExample());
        assertEquals("/test/", dto.getPhonetic());
        assertEquals("noun", dto.getPartOfSpeech());
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void testToString() {
        VocabularyWordDTO dto = new VocabularyWordDTO();
        dto.setWord("test");
        String toString = dto.toString();
        assertTrue(toString.contains("test"));
    }
}
