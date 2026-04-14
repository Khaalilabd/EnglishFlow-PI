package com.englishflow.community.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ReactionTest {

    @Test
    void testNoArgsConstructor() {
        Reaction reaction = new Reaction();
        assertNotNull(reaction);
    }

    @Test
    void testSettersAndGetters() {
        Reaction reaction = new Reaction();
        Post post = new Post();
        Topic topic = new Topic();
        LocalDateTime now = LocalDateTime.now();
        
        reaction.setId(1L);
        reaction.setUserId(100L);
        reaction.setType(Reaction.ReactionType.LIKE);
        reaction.setPost(post);
        reaction.setTopic(topic);
        reaction.setCreatedAt(now);
        
        assertEquals(1L, reaction.getId());
        assertEquals(100L, reaction.getUserId());
        assertEquals(Reaction.ReactionType.LIKE, reaction.getType());
        assertEquals(post, reaction.getPost());
        assertEquals(topic, reaction.getTopic());
        assertEquals(now, reaction.getCreatedAt());
    }

    @Test
    void testReactionTypes() {
        assertEquals(3, Reaction.ReactionType.values().length);
        assertNotNull(Reaction.ReactionType.LIKE);
        assertNotNull(Reaction.ReactionType.INSIGHTFUL);
        assertNotNull(Reaction.ReactionType.HELPFUL);
    }
}
