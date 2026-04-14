package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryDTOTest {

    @Test
    void testNoArgsConstructor() {
        CategoryDTO dto = new CategoryDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<SubCategoryDTO> subCategories = new ArrayList<>();
        
        CategoryDTO dto = new CategoryDTO(
            1L, "Test Category", "Description", "icon.png", "#FF0000",
            false, null, null, subCategories, now, now
        );
        
        assertEquals(1L, dto.getId());
        assertEquals("Test Category", dto.getName());
        assertEquals("Description", dto.getDescription());
        assertEquals("icon.png", dto.getIcon());
        assertEquals("#FF0000", dto.getColor());
        assertFalse(dto.getIsLocked());
        assertNull(dto.getLockedBy());
        assertNull(dto.getLockedAt());
        assertEquals(subCategories, dto.getSubCategories());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        CategoryDTO dto = new CategoryDTO();
        LocalDateTime now = LocalDateTime.now();
        List<SubCategoryDTO> subCategories = new ArrayList<>();
        
        dto.setId(2L);
        dto.setName("Updated Category");
        dto.setDescription("Updated Description");
        dto.setIcon("new-icon.png");
        dto.setColor("#00FF00");
        dto.setIsLocked(true);
        dto.setLockedBy(100L);
        dto.setLockedAt(now);
        dto.setSubCategories(subCategories);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);
        
        assertEquals(2L, dto.getId());
        assertEquals("Updated Category", dto.getName());
        assertEquals("Updated Description", dto.getDescription());
        assertEquals("new-icon.png", dto.getIcon());
        assertEquals("#00FF00", dto.getColor());
        assertTrue(dto.getIsLocked());
        assertEquals(100L, dto.getLockedBy());
        assertEquals(now, dto.getLockedAt());
        assertEquals(subCategories, dto.getSubCategories());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        List<SubCategoryDTO> subCategories = new ArrayList<>();
        
        CategoryDTO dto1 = new CategoryDTO(
            1L, "Test", "Desc", "icon", "#FFF", false, null, null, subCategories, now, now
        );
        CategoryDTO dto2 = new CategoryDTO(
            1L, "Test", "Desc", "icon", "#FFF", false, null, null, subCategories, now, now
        );
        
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(1L);
        dto.setName("Test Category");
        
        String toString = dto.toString();
        assertTrue(toString.contains("Test Category"));
        assertTrue(toString.contains("id=1"));
    }
}
