package com.englishflow.community.service;

import com.englishflow.community.entity.Category;
import com.englishflow.community.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializationServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private DataInitializationService dataInitializationService;

    @Captor
    private ArgumentCaptor<List<Category>> categoriesCaptor;

    @Test
    void initializeCategories_WhenEmpty_ShouldCreateCategories() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        assertNotNull(savedCategories);
        assertEquals(5, savedCategories.size());
    }

    @Test
    void initializeCategories_WhenNotEmpty_ShouldNotCreateCategories() {
        when(categoryRepository.count()).thenReturn(5L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository, never()).saveAll(any());
    }

    @Test
    void initializeCategories_ShouldCreateGeneralCategory() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        Category general = savedCategories.stream()
                .filter(c -> c.getName().contains("Général"))
                .findFirst()
                .orElse(null);

        assertNotNull(general);
        assertEquals("🏠 Général", general.getName());
        assertFalse(general.getSubCategories().isEmpty());
    }

    @Test
    void initializeCategories_ShouldCreateLinguisticCategory() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        Category linguistic = savedCategories.stream()
                .filter(c -> c.getName().contains("linguistiques"))
                .findFirst()
                .orElse(null);

        assertNotNull(linguistic);
        assertTrue(linguistic.getSubCategories().size() >= 4);
    }

    @Test
    void initializeCategories_ShouldCreateClubsCategory() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        Category clubs = savedCategories.stream()
                .filter(c -> c.getName().contains("Clubs"))
                .findFirst()
                .orElse(null);

        assertNotNull(clubs);
        assertFalse(clubs.getSubCategories().isEmpty());
    }

    @Test
    void initializeCategories_ShouldCreateEventsCategory() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        Category events = savedCategories.stream()
                .filter(c -> c.getName().contains("Événements"))
                .findFirst()
                .orElse(null);

        assertNotNull(events);
        assertFalse(events.getSubCategories().isEmpty());
    }

    @Test
    void initializeCategories_ShouldCreateResourcesCategory() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        Category resources = savedCategories.stream()
                .filter(c -> c.getName().contains("Ressources"))
                .findFirst()
                .orElse(null);

        assertNotNull(resources);
        assertFalse(resources.getSubCategories().isEmpty());
    }

    @Test
    void initializeCategories_AllCategoriesShouldHaveSubCategories() {
        when(categoryRepository.count()).thenReturn(0L);

        dataInitializationService.initializeCategories();

        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        List<Category> savedCategories = categoriesCaptor.getValue();

        for (Category category : savedCategories) {
            assertFalse(category.getSubCategories().isEmpty(),
                    "Category " + category.getName() + " should have subcategories");
        }
    }
}
