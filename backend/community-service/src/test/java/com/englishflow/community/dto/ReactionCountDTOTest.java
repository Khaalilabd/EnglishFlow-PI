package com.englishflow.community.dto;

import com.englishflow.community.entity.Reaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactionCountDTOTest {

    @Test
    void testNoArgsConstructor() {
        ReactionCountDTO dto = new ReactionCountDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        ReactionCountDTO dto = new ReactionCountDTO(Reaction.ReactionType.LIKE, 10L);
        
        assertEquals(Reaction.ReactionType.LIKE, dto.getType());
        assertEquals(10L, dto.getCount());
    }

    @Test
    void testSettersAndGetters() {
        ReactionCountDTO dto = new ReactionCountDTO();
        
        dto.setType(Reaction.ReactionType.HELPFUL);
        dto.setCount(25L);
        
        assertEquals(Reaction.ReactionType.HELPFUL, dto.getType());
        assertEquals(25L, dto.getCount());
    }

    @Test
    void testAllReactionTypes() {
        ReactionCountDTO dto1 = new ReactionCountDTO(Reaction.ReactionType.LIKE, 5L);
        ReactionCountDTO dto2 = new ReactionCountDTO(Reaction.ReactionType.INSIGHTFUL, 3L);
        ReactionCountDTO dto3 = new ReactionCountDTO(Reaction.ReactionType.HELPFUL, 7L);
        
        assertEquals(Reaction.ReactionType.LIKE, dto1.getType());
        assertEquals(Reaction.ReactionType.INSIGHTFUL, dto2.getType());
        assertEquals(Reaction.ReactionType.HELPFUL, dto3.getType());
    }

    @Test
    void testToString() {
        ReactionCountDTO dto = new ReactionCountDTO(Reaction.ReactionType.LIKE, 10L);
        
        String toString = dto.toString();
        assertTrue(toString.contains("LIKE"));
        assertTrue(toString.contains("10"));
    }
}
