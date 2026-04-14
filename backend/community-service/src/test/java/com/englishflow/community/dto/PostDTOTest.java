package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PostDTOTest {

    @Test
    void testNoArgsConstructor() {
        PostDTO dto = new PostDTO();
        assertNotNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        PostDTO dto = new PostDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setContent("Test content");
        dto.setUserId(100L);
        dto.setUserName("Test User");
        dto.setTopicId(10L);
        dto.setReactionsCount(15);
        dto.setLikeCount(5);
        dto.setInsightfulCount(5);
        dto.setHelpfulCount(5);
        dto.setWeightedScore(100);
        dto.setIsTrending(true);
        dto.setIsAccepted(false);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        
        assertEquals(1L, dto.getId());
        assertEquals("Test content", dto.getContent());
        assertEquals(100L, dto.getUserId());
        assertEquals("Test User", dto.getUserName());
        assertEquals(10L, dto.getTopicId());
        assertEquals(15, dto.getReactionsCount());
        assertEquals(5, dto.getLikeCount());
        assertEquals(5, dto.getInsightfulCount());
        assertEquals(5, dto.getHelpfulCount());
        assertEquals(100, dto.getWeightedScore());
        assertTrue(dto.getIsTrending());
        assertFalse(dto.getIsAccepted());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testToString() {
        PostDTO dto = new PostDTO();
        dto.setId(1L);
        dto.setContent("Test");
        
        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
    }
}
