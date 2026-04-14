package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateCategoryRequestTest {

    @Test
    void testNoArgsConstructor() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        assertNotNull(request);
    }

    @Test
    void testSettersAndGetters() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Test Category");
        request.setDescription("Test Description");
        request.setIcon("icon.png");
        request.setColor("#FF0000");
        
        assertEquals("Test Category", request.getName());
        assertEquals("Test Description", request.getDescription());
        assertEquals("icon.png", request.getIcon());
        assertEquals("#FF0000", request.getColor());
    }

    @Test
    void testToString() {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Test");
        String toString = request.toString();
        assertTrue(toString.contains("Test"));
    }
}
