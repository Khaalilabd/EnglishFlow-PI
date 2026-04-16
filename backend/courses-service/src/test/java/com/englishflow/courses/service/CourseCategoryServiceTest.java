package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseCategoryDTO;
import com.englishflow.courses.entity.CourseCategory;
import com.englishflow.courses.repository.CourseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCategoryServiceTest {

    @Mock
    private CourseCategoryRepository categoryRepository;

    @InjectMocks
    private CourseCategoryService courseCategoryService;

    private CourseCategory testCategory;
    private CourseCategoryDTO testCategoryDTO;

    @BeforeEach
    void setUp() {
        testCategory = new CourseCategory();
        testCategory.setId(1L);
        testCategory.setName("English");
        testCategory.setDescription("English Language Courses");
        testCategory.setIcon("language-icon");
        testCategory.setColor("#FF5733");
        testCategory.setActive(true);
        testCategory.setDisplayOrder(1);
        testCategory.setCreatedBy(1L);
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        testCategoryDTO = new CourseCategoryDTO();
        testCategoryDTO.setId(1L);
        testCategoryDTO.setName("English");
        testCategoryDTO.setDescription("English Language Courses");
        testCategoryDTO.setIcon("language-icon");
        testCategoryDTO.setColor("#FF5733");
        testCategoryDTO.setActive(true);
        testCategoryDTO.setDisplayOrder(1);
        testCategoryDTO.setCreatedBy(1L);
    }

    @Test
    void createCategory_WithValidData_ShouldCreateCategory() {
        when(categoryRepository.existsByName("English")).thenReturn(false);
        when(categoryRepository.save(any(CourseCategory.class))).thenReturn(testCategory);

        CourseCategoryDTO result = courseCategoryService.createCategory(testCategoryDTO);

        assertNotNull(result);
        assertEquals("English", result.getName());
        verify(categoryRepository, times(1)).existsByName("English");
        verify(categoryRepository, times(1)).save(any(CourseCategory.class));
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {
        when(categoryRepository.existsByName("English")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseCategoryService.createCategory(testCategoryDTO));
        verify(categoryRepository, times(1)).existsByName("English");
        verify(categoryRepository, never()).save(any(CourseCategory.class));
    }

    @Test
    void createCategory_WithNullActive_ShouldDefaultToTrue() {
        testCategoryDTO.setActive(null);
        when(categoryRepository.existsByName("English")).thenReturn(false);
        when(categoryRepository.save(any(CourseCategory.class))).thenReturn(testCategory);

        CourseCategoryDTO result = courseCategoryService.createCategory(testCategoryDTO);

        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(CourseCategory.class));
    }

    @Test
    void updateCategory_WhenCategoryExists_ShouldUpdateCategory() {
        testCategoryDTO.setDescription("Updated Description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(CourseCategory.class))).thenReturn(testCategory);

        CourseCategoryDTO result = courseCategoryService.updateCategory(1L, testCategoryDTO);

        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(CourseCategory.class));
    }

    @Test
    void updateCategory_WhenCategoryNotExists_ShouldThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseCategoryService.updateCategory(999L, testCategoryDTO));
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(CourseCategory.class));
    }

    @Test
    void updateCategory_WithDuplicateName_ShouldThrowException() {
        testCategoryDTO.setName("Math");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.existsByName("Math")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> courseCategoryService.updateCategory(1L, testCategoryDTO));
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsByName("Math");
        verify(categoryRepository, never()).save(any(CourseCategory.class));
    }

    @Test
    void getById_WhenCategoryExists_ShouldReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        CourseCategoryDTO result = courseCategoryService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("English", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getById_WhenCategoryNotExists_ShouldThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseCategoryService.getById(999L));
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategoriesOrderedByDisplayOrder() {
        List<CourseCategory> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(categories);

        List<CourseCategoryDTO> result = courseCategoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English", result.get(0).getName());
        verify(categoryRepository, times(1)).findAllByOrderByDisplayOrderAsc();
    }

    @Test
    void getActiveCategories_ShouldReturnOnlyActiveCategories() {
        List<CourseCategory> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByActiveOrderByDisplayOrderAsc(true)).thenReturn(categories);

        List<CourseCategoryDTO> result = courseCategoryService.getActiveCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getActive());
        verify(categoryRepository, times(1)).findByActiveOrderByDisplayOrderAsc(true);
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() {
        doNothing().when(categoryRepository).deleteById(1L);

        courseCategoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void toggleActive_WhenCategoryExists_ShouldToggleActiveStatus() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(CourseCategory.class))).thenReturn(testCategory);

        courseCategoryService.toggleActive(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(CourseCategory.class));
    }

    @Test
    void toggleActive_WhenCategoryNotExists_ShouldThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseCategoryService.toggleActive(999L));
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(CourseCategory.class));
    }

    @Test
    void updateDisplayOrder_WhenCategoryExists_ShouldUpdateOrder() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(CourseCategory.class))).thenReturn(testCategory);

        courseCategoryService.updateDisplayOrder(1L, 5);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(CourseCategory.class));
    }

    @Test
    void updateDisplayOrder_WhenCategoryNotExists_ShouldThrowException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseCategoryService.updateDisplayOrder(999L, 5));
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).save(any(CourseCategory.class));
    }
}
