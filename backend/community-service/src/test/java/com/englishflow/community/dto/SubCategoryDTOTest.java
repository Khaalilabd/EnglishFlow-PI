package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubCategoryDTOTest {

    @Test
    void testNoArgsConstructor() {
        SubCategoryDTO dto = new SubCategoryDTO();
        assertNotNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        SubCategoryDTO dto = new SubCategoryDTO();
        LocalDateTime now = LocalDateTime.now();
        
        dto.setId(1L);
        dto.setName("Test SubCategory");
        dto.setDescription("Description");
        dto.setCategoryId(10L);
        dto.setCategoryName("Test Category");
        dto.setRequiresClubMembership(true);
        dto.setIsLocked(false);
        dto.setLockedBy(null);
        dto.setLockedAt(null);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        
        assertEquals(1L, dto.getId());
        assertEquals("Test SubCategory", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertEquals(10L, dto.getCategoryId());
        assertEquals("Test Category", dto.getCategoryName());
        assertTrue(dto.getRequiresClubMembership());
        assertFalse(dto.getIsLocked());
        assertNull(dto.getLockedBy());
        assertNull(dto.getLockedAt());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testToString() {
        SubCategoryDTO dto = new SubCategoryDTO();
        dto.setId(1L);
        dto.setName("Test");
        
        String toString = dto.toString();
        assertTrue(toString.contains("id=1"));
    }
}
