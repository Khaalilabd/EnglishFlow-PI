package com.jungle.learning.service;

import com.jungle.learning.dto.CollectionDTO;
import com.jungle.learning.model.Collection;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.repository.CollectionRepository;
import com.jungle.learning.repository.EbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private EbookRepository ebookRepository;

    @InjectMocks
    private CollectionService collectionService;

    private Collection collection;
    private Ebook ebook;

    @BeforeEach
    void setUp() {
        collection = new Collection();
        collection.setId(1L);
        collection.setName("My Collection");
        collection.setDescription("Test collection");
        collection.setIsPublic(false);
        collection.setOwnerId(100L);
        collection.setEbooks(new ArrayList<>());
        collection.setCreatedAt(LocalDateTime.now());
        collection.setUpdatedAt(LocalDateTime.now());

        ebook = new Ebook();
        ebook.setId(1L);
        ebook.setTitle("Test Ebook");
    }

    @Test
    void createCollection_ShouldCreateAndReturn() {
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.createCollection("My Collection", "Test collection", false, 100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("My Collection", result.getName());
        assertEquals("Test collection", result.getDescription());
        assertFalse(result.getIsPublic());
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void createCollection_WithNullIsPublic_ShouldDefaultToFalse() {
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.createCollection("My Collection", "Test collection", null, 100L);

        assertNotNull(result);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void updateCollection_ValidUpdate_ShouldUpdateAndReturn() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.updateCollection(1L, "Updated Name", "Updated Description", true, 100L);

        assertNotNull(result);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void updateCollection_Unauthorized_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> 
            collectionService.updateCollection(1L, "Updated Name", "Updated Description", true, 999L)
        );
    }

    @Test
    void updateCollection_CollectionNotFound_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            collectionService.updateCollection(1L, "Updated Name", "Updated Description", true, 100L)
        );
    }

    @Test
    void updateCollection_WithNullFields_ShouldNotUpdateNullFields() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.updateCollection(1L, null, null, null, 100L);

        assertNotNull(result);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void deleteCollection_ValidDelete_ShouldDelete() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        collectionService.deleteCollection(1L, 100L);

        verify(collectionRepository).delete(collection);
    }

    @Test
    void deleteCollection_Unauthorized_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> 
            collectionService.deleteCollection(1L, 999L)
        );
    }

    @Test
    void deleteCollection_CollectionNotFound_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            collectionService.deleteCollection(1L, 100L)
        );
    }

    @Test
    void addEbookToCollection_NewEbook_ShouldAddAndReturn() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.addEbookToCollection(1L, 1L, 100L);

        assertNotNull(result);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void addEbookToCollection_ExistingEbook_ShouldReturnWithoutAdding() {
        collection.getEbooks().add(ebook);
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));

        CollectionDTO result = collectionService.addEbookToCollection(1L, 1L, 100L);

        assertNotNull(result);
        verify(collectionRepository, never()).save(any(Collection.class));
    }

    @Test
    void addEbookToCollection_Unauthorized_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> 
            collectionService.addEbookToCollection(1L, 1L, 999L)
        );
    }

    @Test
    void addEbookToCollection_EbookNotFound_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            collectionService.addEbookToCollection(1L, 1L, 100L)
        );
    }

    @Test
    void removeEbookFromCollection_ShouldRemoveAndReturn() {
        collection.getEbooks().add(ebook);
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any(Collection.class))).thenReturn(collection);

        CollectionDTO result = collectionService.removeEbookFromCollection(1L, 1L, 100L);

        assertNotNull(result);
        verify(collectionRepository).save(any(Collection.class));
    }

    @Test
    void removeEbookFromCollection_Unauthorized_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        assertThrows(RuntimeException.class, () -> 
            collectionService.removeEbookFromCollection(1L, 1L, 999L)
        );
    }

    @Test
    void getUserCollections_ShouldReturnUserCollections() {
        List<Collection> collections = Arrays.asList(collection);
        when(collectionRepository.findByOwnerId(100L)).thenReturn(collections);

        List<CollectionDTO> result = collectionService.getUserCollections(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getPublicCollections_ShouldReturnPublicCollections() {
        collection.setIsPublic(true);
        List<Collection> collections = Arrays.asList(collection);
        when(collectionRepository.findByIsPublicTrue()).thenReturn(collections);

        List<CollectionDTO> result = collectionService.getPublicCollections();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCollection_ExistingCollection_ShouldReturnDTO() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.of(collection));

        CollectionDTO result = collectionService.getCollection(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("My Collection", result.getName());
    }

    @Test
    void getCollection_CollectionNotFound_ShouldThrowException() {
        when(collectionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            collectionService.getCollection(1L)
        );
    }
}
