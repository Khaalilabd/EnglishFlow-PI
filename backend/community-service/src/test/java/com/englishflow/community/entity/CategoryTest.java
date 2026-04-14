package com.englishflow.community.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Test
    void testNoArgsConstructor() {
        Category category = new Category();
        assertNotNull(category);
    }

    @Test
    void testSettersAndGetters() {
        Category category = new Category();
        LocalDateTime now = LocalDateTime.now();
        
        category.setId(1L);
        category.setName("Test Category");
        category.setDescription("Description");
        category.setIcon("icon.png");
        category.setColor("#FF0000");
        category.setIsLocked(false);
        category.setLockedBy(null);
        category.setLockedAt(null);
        category.setSubCategories(new ArrayList<>());
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        
        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
        assertEquals("Description", category.getDescription());
        assertEquals("icon.png", category.getIcon());
        assertEquals("#FF0000", category.getColor());
        assertFalse(category.getIsLocked());
        assertNotNull(category.getSubCategories());
        assertEquals(now, category.getCreatedAt());
    }
}
