package com.englishflow.community.service;

import com.englishflow.community.dto.CategoryDTO;
import com.englishflow.community.dto.CreateCategoryRequest;
import com.englishflow.community.entity.Category;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.repository.CategoryRepository;
import com.englishflow.community.repository.SubCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SubCategoryRepository subCategoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CreateCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setDescription("Test Description");
        testCategory.setIcon("fa-test");
        testCategory.setColor("primary");

        createRequest = new CreateCategoryRequest();
        createRequest.setName("New Category");
        createRequest.setDescription("New Description");
        createRequest.setIcon("fa-new");
        createRequest.setColor("secondary");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(testCategory));

        // Act
        List<CategoryDTO> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WhenExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(999L);
        });
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void createCategory_ShouldReturnCreatedCategory() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        CategoryDTO result = categoryService.createCategory(createRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        doNothing().when(categoryRepository).delete(testCategory);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    void deleteCategory_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(999L);
        });
        verify(categoryRepository, times(1)).findById(999L);
        verify(categoryRepository, never()).delete(any());
    }
}
