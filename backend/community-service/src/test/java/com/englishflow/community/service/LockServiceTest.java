package com.englishflow.community.service;

import com.englishflow.community.entity.Category;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.repository.CategoryRepository;
import com.englishflow.community.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @InjectMocks
    private LockService lockService;

    private Category category;
    private SubCategory subCategory1;
    private SubCategory subCategory2;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("General");
        category.setIsLocked(false);
        category.setSubCategories(new ArrayList<>());

        subCategory1 = new SubCategory();
        subCategory1.setId(1L);
        subCategory1.setName("Sub1");
        subCategory1.setCategory(category);
        subCategory1.setIsLocked(false);

        subCategory2 = new SubCategory();
        subCategory2.setId(2L);
        subCategory2.setName("Sub2");
        subCategory2.setCategory(category);
        subCategory2.setIsLocked(true);

        category.getSubCategories().addAll(Arrays.asList(subCategory1, subCategory2));
    }

    @Test
    void lockCategory_ShouldSetLockedFields() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        lockService.lockCategory(1L, 100L);

        assertTrue(category.getIsLocked());
        assertEquals(100L, category.getLockedBy());
        assertNotNull(category.getLockedAt());
        verify(categoryRepository).save(category);
    }

    @Test
    void lockCategory_CategoryNotFound_ShouldThrowException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            lockService.lockCategory(999L, 100L);
        });

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void unlockCategory_ShouldClearLockedFields() {
        category.setIsLocked(true);
        category.setLockedBy(100L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        lockService.unlockCategory(1L, 100L);

        assertFalse(category.getIsLocked());
        assertNull(category.getLockedBy());
        assertNull(category.getLockedAt());
        verify(categoryRepository).save(category);
    }

    @Test
    void unlockCategory_ShouldUnlockAllSubCategories() {
        category.setIsLocked(true);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        lockService.unlockCategory(1L, 100L);

        assertFalse(category.getIsLocked());
        verify(subCategoryRepository).save(subCategory2);
        assertFalse(subCategory2.getIsLocked());
    }

    @Test
    void lockSubCategory_ShouldSetLockedFields() {
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory1));
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory1);

        lockService.lockSubCategory(1L, 100L);

        assertTrue(subCategory1.getIsLocked());
        assertEquals(100L, subCategory1.getLockedBy());
        assertNotNull(subCategory1.getLockedAt());
        verify(subCategoryRepository).save(subCategory1);
    }

    @Test
    void lockSubCategory_SubCategoryNotFound_ShouldThrowException() {
        when(subCategoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            lockService.lockSubCategory(999L, 100L);
        });

        verify(subCategoryRepository, never()).save(any());
    }

    @Test
    void unlockSubCategory_ShouldClearLockedFields() {
        subCategory1.setIsLocked(true);
        subCategory1.setLockedBy(100L);
        when(subCategoryRepository.findById(1L)).thenReturn(Optional.of(subCategory1));
        when(subCategoryRepository.save(any(SubCategory.class))).thenReturn(subCategory1);

        lockService.unlockSubCategory(1L, 100L);

        assertFalse(subCategory1.getIsLocked());
        assertNull(subCategory1.getLockedBy());
        assertNull(subCategory1.getLockedAt());
        verify(subCategoryRepository).save(subCategory1);
    }
}
