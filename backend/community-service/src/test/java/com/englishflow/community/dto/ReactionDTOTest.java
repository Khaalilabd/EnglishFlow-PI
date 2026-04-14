package com.englishflow.community.dto;

import com.englishflow.community.entity.Reaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReactionDTOTest {

    @Test
    void testNoArgsConstructor() {
        ReactionDTO dto = new ReactionDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        ReactionDTO dto = new ReactionDTO(
            1L, 100L, Reaction.ReactionType.LIKE, 10L, 5L
        );
        
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getUserId());
        assertEquals(Reaction.ReactionType.LIKE, dto.getType());
        assertEquals(10L, dto.getPostId());
        assertEquals(5L, dto.getTopicId());
    }

    @Test
    void testSettersAndGetters() {
        ReactionDTO dto = new ReactionDTO();
        
        dto.setId(2L);
        dto.setUserId(200L);
        dto.setType(Reaction.ReactionType.HELPFUL);
        dto.setPostId(20L);
        dto.setTopicId(10L);
        
        assertEquals(2L, dto.getId());
        assertEquals(200L, dto.getUserId());
        assertEquals(Reaction.ReactionType.HELPFUL, dto.getType());
        assertEquals(20L, dto.getPostId());
        assertEquals(10L, dto.getTopicId());
    }

    @Test
    void testAllReactionTypes() {
        ReactionDTO dto = new ReactionDTO();
        
        dto.setType(Reaction.ReactionType.LIKE);
        assertEquals(Reaction.ReactionType.LIKE, dto.getType());
        
        dto.setType(Reaction.ReactionType.INSIGHTFUL);
        assertEquals(Reaction.ReactionType.INSIGHTFUL, dto.getType());
        
        dto.setType(Reaction.ReactionType.HELPFUL);
        assertEquals(Reaction.ReactionType.HELPFUL, dto.getType());
    }

    @Test
    void testEqualsAndHashCode() {
        ReactionDTO dto1 = new ReactionDTO(1L, 100L, Reaction.ReactionType.LIKE, 10L, 5L);
        ReactionDTO dto2 = new ReactionDTO(1L, 100L, Reaction.ReactionType.LIKE, 10L, 5L);
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        ReactionDTO dto = new ReactionDTO();
        dto.setId(1L);
        dto.setType(Reaction.ReactionType.LIKE);
        
        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("LIKE"));
    }
}
