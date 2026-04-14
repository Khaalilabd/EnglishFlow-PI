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
        VocabularyStatsDTO dto = new VocabularyStatsDTO(100L, 20L, 30L, 10L, 5L, 150L);
        assertEquals(100L, dto.getTotalWords());
        assertEquals(20L, dto.getNewWords());
        assertEquals(30L, dto.getLearningWords());
        assertEquals(10L, dto.getFamiliarWords());
        assertEquals(5L, dto.getMasteredWords());
        assertEquals(150L, dto.getTotalReviews());
    }

    @Test
    void testSettersAndGetters() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO();
        dto.setTotalWords(200L);
        dto.setNewWords(40L);
        dto.setLearningWords(60L);
        dto.setFamiliarWords(20L);
        dto.setMasteredWords(10L);
        dto.setTotalReviews(300L);
        
        assertEquals(200L, dto.getTotalWords());
        assertEquals(40L, dto.getNewWords());
        assertEquals(60L, dto.getLearningWords());
        assertEquals(20L, dto.getFamiliarWords());
        assertEquals(10L, dto.getMasteredWords());
        assertEquals(300L, dto.getTotalReviews());
    }

    @Test
    void testToString() {
        VocabularyStatsDTO dto = new VocabularyStatsDTO(100L, 20L, 30L, 10L, 5L, 150L);
        String toString = dto.toString();
        assertTrue(toString.contains("100"));
    }
}
