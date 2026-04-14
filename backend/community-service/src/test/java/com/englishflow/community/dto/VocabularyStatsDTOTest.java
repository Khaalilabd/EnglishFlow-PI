package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VocabularyStatsDTOTest {

    @Test
    void testNoArgsConstructor() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO(100L, 50L, 30L);
        assertEquals(100L, dto.getTotalWords());
        assertEquals(50L, dto.getWordsThisWeek());
        assertEquals(30L, dto.getWordsThisMonth());
    }

    @Test
    void testSettersAndGetters() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO();
        dto.setTotalWords(200L);
        dto.setWordsThisWeek(75L);
        dto.setWordsThisMonth(150L);
        
        assertEquals(200L, dto.getTotalWords());
        assertEquals(75L, dto.getWordsThisWeek());
        assertEquals(150L, dto.getWordsThisMonth());
    }

    @Test
    void testToString() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO(100L, 50L, 30L);
        String toString = dto.toString();
        assertTrue(toString.contains("100"));
    }
}
