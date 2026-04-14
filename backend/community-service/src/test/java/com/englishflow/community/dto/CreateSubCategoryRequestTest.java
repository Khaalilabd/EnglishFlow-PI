package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateSubCategoryRequestTest {

    @Test
    void testNoArgsConstructor() {
        CreateSubCategoryRequest request = new CreateSubCategoryRequest();
        assertNotNull(request);
    }

    @Test
    void testSettersAndGetters() {
        CreateSubCategoryRequest request = new CreateSubCategoryRequest();
        request.setName("Test SubCategory");
        request.setDescription("Test Description");
        request.setCategoryId(1L);
        
        assertEquals("Test SubCategory", request.getName());
        assertEquals("Test Description", request.getDescription());
        assertEquals(1L, request.getCategoryId());
    }

    @Test
    void testToString() {
        CreateSubCategoryRequest request = new CreateSubCategoryRequest();
        request.setName("Test");
        String toString = request.toString();
        assertTrue(toString.contains("Test"));
    }
}
