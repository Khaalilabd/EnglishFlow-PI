package com.englishflow.community.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class SubCategoryTest {

    @Test
    void testNoArgsConstructor() {
        SubCategory subCategory = new SubCategory();
        assertNotNull(subCategory);
    }

    @Test
    void testSettersAndGetters() {
        SubCategory subCategory = new SubCategory();
        Category category = new Category();
        LocalDateTime now = LocalDateTime.now();
        
        subCategory.setId(1L);
        subCategory.setName("Test SubCategory");
        subCategory.setDescription("Description");
        subCategory.setCategory(category);
        subCategory.setRequiresClubMembership(true);
        subCategory.setIsLocked(false);
        subCategory.setLockedBy(null);
        subCategory.setLockedAt(null);
        subCategory.setTopics(new ArrayList<>());
        subCategory.setCreatedAt(now);
        subCategory.setUpdatedAt(now);
        
        assertEquals(1L, subCategory.getId());
        assertEquals("Test SubCategory", subCategory.getName());
        assertEquals("Description", subCategory.getDescription());
        assertEquals(category, subCategory.getCategory());
        assertTrue(subCategory.getRequiresClubMembership());
        assertFalse(subCategory.getIsLocked());
        assertNotNull(subCategory.getTopics());
    }
}
