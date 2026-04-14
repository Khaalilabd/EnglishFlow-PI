package com.englishflow.community.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void testNoArgsConstructor() {
        Post post = new Post();
        assertNotNull(post);
    }

    @Test
    void testSettersAndGetters() {
        Post post = new Post();
        Topic topic = new Topic();
        LocalDateTime now = LocalDateTime.now();
        
        post.setId(1L);
        post.setContent("Test content");
        post.setUserId(100L);
        post.setUserName("Test User");
        post.setTopic(topic);
        post.setReactionsCount(10);
        post.setLikeCount(5);
        post.setInsightfulCount(3);
        post.setHelpfulCount(2);
        post.setWeightedScore(100);
        post.setIsTrending(true);
        post.setIsAccepted(false);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        
        assertEquals(1L, post.getId());
        assertEquals("Test content", post.getContent());
        assertEquals(100L, post.getUserId());
        assertEquals("Test User", post.getUserName());
        assertEquals(topic, post.getTopic());
        assertEquals(10, post.getReactionsCount());
        assertEquals(5, post.getLikeCount());
        assertEquals(3, post.getInsightfulCount());
        assertEquals(2, post.getHelpfulCount());
        assertEquals(100, post.getWeightedScore());
        assertTrue(post.getIsTrending());
        assertFalse(post.getIsAccepted());
    }

    @Test
    void testCalculateWeightedScore() {
        Post post = new Post();
        post.setLikeCount(5);
        post.setInsightfulCount(3);
        post.setHelpfulCount(2);
        
        post.calculateWeightedScore();
        
        // Score = (5 * 1) + (3 * 2) + (2 * 3) = 5 + 6 + 6 = 17
        assertEquals(17, post.getWeightedScore());
    }
}
