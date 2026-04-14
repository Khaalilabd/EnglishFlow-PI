package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePostRequestTest {

    @Test
    void testNoArgsConstructor() {
        CreatePostRequest request = new CreatePostRequest();
        assertNotNull(request);
    }

    @Test
    void testSettersAndGetters() {
        CreatePostRequest request = new CreatePostRequest();
        
        request.setContent("Test Content");
        request.setUserId(100L);
        request.setUserName("Test User");
        request.setTopicId(10L);
        
        assertEquals("Test Content", request.getContent());
        assertEquals(100L, request.getUserId());
        assertEquals("Test User", request.getUserName());
        assertEquals(10L, request.getTopicId());
    }

    @Test
    void testToString() {
        CreatePostRequest request = new CreatePostRequest();
        request.setContent("Test");
        
        String toString = request.toString();
        assertTrue(toString.contains("Test"));
    }
}
