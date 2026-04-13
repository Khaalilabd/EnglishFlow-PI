package com.jungle.learning.service;

import com.jungle.learning.dto.EbookDTO;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.EbookAccess;
import com.jungle.learning.repository.EbookAccessRepository;
import com.jungle.learning.repository.EbookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EbookServiceTest {

    @Mock
    private EbookRepository ebookRepository;

    @Mock
    private EbookAccessRepository ebookAccessRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private EbookService ebookService;

    private Ebook ebook;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ebookService, "uploadDir", "uploads/ebooks");

        ebook = new Ebook();
        ebook.setId(1L);
        ebook.setTitle("Test Ebook");
        ebook.setDescription("Test Description");
        ebook.setFileUrl("test-file.pdf");
        ebook.setFileSize(1024L);
        ebook.setMimeType("application/pdf");
        ebook.setLevel(Ebook.Level.B1);
        ebook.setCategory(Ebook.Category.GRAMMAR);
        ebook.setIsFree(true);
        ebook.setDownloadCount(0);
        ebook.setStatus(Ebook.PublishStatus.PUBLISHED);
        ebook.setCreatedBy(1L);
        ebook.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getAllEbooks_ShouldReturnAllEbooks() {
        // Given
        when(ebookRepository.findAll()).thenReturn(Arrays.asList(ebook));
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        List<EbookDTO> result = ebookService.getAllEbooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Ebook", result.get(0).getTitle());
        verify(ebookRepository, times(1)).findAll();
    }

    @Test
    void getFreeEbooks_ShouldReturnOnlyFreeEbooks() {
        // Given
        when(ebookRepository.findByIsFree(true)).thenReturn(Arrays.asList(ebook));
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        List<EbookDTO> result = ebookService.getFreeEbooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getFree());
        verify(ebookRepository, times(1)).findByIsFree(true);
    }

    @Test
    void getEbooksByLevel_ShouldReturnEbooksForLevel() {
        // Given
        when(ebookRepository.findByLevel(Ebook.Level.B1)).thenReturn(Arrays.asList(ebook));
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        List<EbookDTO> result = ebookService.getEbooksByLevel("B1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("B1", result.get(0).getLevel());
        verify(ebookRepository, times(1)).findByLevel(Ebook.Level.B1);
    }

    @Test
    void getEbookById_WhenEbookExists_ShouldReturnEbook() {
        // Given
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        EbookDTO result = ebookService.getEbookById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Ebook", result.getTitle());
        verify(ebookRepository, times(1)).findById(1L);
    }

    @Test
    void getEbookById_WhenEbookNotExists_ShouldThrowException() {
        // Given
        when(ebookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> ebookService.getEbookById(999L));
        verify(ebookRepository, times(1)).findById(999L);
    }

    @Test
    void deleteEbook_WhenEbookExists_ShouldDelete() {
        // Given
        when(ebookRepository.existsById(1L)).thenReturn(true);

        // When
        ebookService.deleteEbook(1L);

        // Then
        verify(ebookRepository, times(1)).existsById(1L);
        verify(ebookRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteEbook_WhenEbookNotExists_ShouldThrowException() {
        // Given
        when(ebookRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> ebookService.deleteEbook(999L));
        verify(ebookRepository, times(1)).existsById(999L);
        verify(ebookRepository, never()).deleteById(anyLong());
    }

    @Test
    void trackAccess_ShouldIncrementDownloadCount() {
        // Given
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(ebookAccessRepository.findByEbook_IdAndStudentId(1L, 1L)).thenReturn(Optional.empty());
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);

        // When
        ebookService.trackAccess(1L, 1L);

        // Then
        verify(ebookRepository, times(1)).findById(1L);
        verify(ebookRepository, times(1)).save(any(Ebook.class));
        verify(ebookAccessRepository, times(1)).save(any(EbookAccess.class));
    }

    @Test
    void approveEbook_ShouldSetStatusToPublished() {
        // Given
        ebook.setStatus(Ebook.PublishStatus.PENDING);
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        EbookDTO result = ebookService.approveEbook(1L);

        // Then
        assertNotNull(result);
        verify(ebookRepository, times(1)).findById(1L);
        verify(ebookRepository, times(1)).save(any(Ebook.class));
    }

    @Test
    void rejectEbook_ShouldSetStatusToRejected() {
        // Given
        ebook.setStatus(Ebook.PublishStatus.PENDING);
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        EbookDTO result = ebookService.rejectEbook(1L, "Not suitable");

        // Then
        assertNotNull(result);
        verify(ebookRepository, times(1)).findById(1L);
        verify(ebookRepository, times(1)).save(any(Ebook.class));
    }

    @Test
    void getPendingEbooks_ShouldReturnPendingEbooks() {
        // Given
        ebook.setStatus(Ebook.PublishStatus.PENDING);
        when(ebookRepository.findByStatus(Ebook.PublishStatus.PENDING)).thenReturn(Arrays.asList(ebook));
        when(userServiceClient.getUserName(anyLong())).thenReturn("Test User");

        // When
        List<EbookDTO> result = ebookService.getPendingEbooks();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ebookRepository, times(1)).findByStatus(Ebook.PublishStatus.PENDING);
    }
}
