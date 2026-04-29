package com.jungle.learning.service;

import com.jungle.learning.dto.ReadingProgressDTO;
import com.jungle.learning.dto.UpdateProgressRequest;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.EbookRepository;
import com.jungle.learning.repository.ReadingProgressRepository;
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
class ReadingProgressServiceTest {

    @Mock
    private ReadingProgressRepository progressRepository;

    @Mock
    private EbookRepository ebookRepository;

    @InjectMocks
    private ReadingProgressService readingProgressService;

    private Ebook ebook;
    private ReadingProgress progress;
    private UpdateProgressRequest updateRequest;

    @BeforeEach
    void setUp() {
        ebook = new Ebook();
        ebook.setId(1L);
        ebook.setTitle("Test Ebook");
        ebook.setCoverImageUrl("http://example.com/cover.jpg");

        progress = new ReadingProgress();
        progress.setId(1L);
        progress.setEbook(ebook);
        progress.setUserId(100L);
        progress.setCurrentPage(50);
        progress.setTotalPages(200);
        progress.setLastReadAt(LocalDateTime.now());
        progress.setReadingTimeMinutes(120);
        progress.setBookmarks(new ArrayList<>());
        progress.setNotes(new ArrayList<>());

        updateRequest = new UpdateProgressRequest();
        updateRequest.setEbookId(1L);
        updateRequest.setCurrentPage(75);
        updateRequest.setTotalPages(200);
        updateRequest.setReadingTimeMinutes(30);
    }

    @Test
    void updateProgress_ExistingProgress_ShouldUpdateAndReturn() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(progressRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(ReadingProgress.class))).thenReturn(progress);

        ReadingProgressDTO result = readingProgressService.updateProgress(updateRequest, 100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEbookId());
        assertEquals("Test Ebook", result.getEbookTitle());
        verify(progressRepository).save(any(ReadingProgress.class));
    }

    @Test
    void updateProgress_NewProgress_ShouldCreateAndReturn() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(progressRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.empty());
        when(progressRepository.save(any(ReadingProgress.class))).thenReturn(progress);

        ReadingProgressDTO result = readingProgressService.updateProgress(updateRequest, 100L);

        assertNotNull(result);
        verify(progressRepository).save(any(ReadingProgress.class));
    }

    @Test
    void updateProgress_EbookNotFound_ShouldThrowException() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            readingProgressService.updateProgress(updateRequest, 100L)
        );
    }

    @Test
    void updateProgress_WithNullReadingTime_ShouldNotUpdateReadingTime() {
        updateRequest.setReadingTimeMinutes(null);
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(progressRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(ReadingProgress.class))).thenReturn(progress);

        ReadingProgressDTO result = readingProgressService.updateProgress(updateRequest, 100L);

        assertNotNull(result);
        verify(progressRepository).save(any(ReadingProgress.class));
    }

    @Test
    void getProgress_ExistingProgress_ShouldReturnDTO() {
        when(progressRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.of(progress));

        ReadingProgressDTO result = readingProgressService.getProgress(1L, 100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getEbookId());
        assertEquals(100L, result.getUserId());
    }

    @Test
    void getProgress_NoProgress_ShouldReturnNull() {
        when(progressRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.empty());

        ReadingProgressDTO result = readingProgressService.getProgress(1L, 100L);

        assertNull(result);
    }

    @Test
    void getUserProgress_ShouldReturnListOfProgress() {
        List<ReadingProgress> progressList = Arrays.asList(progress);
        when(progressRepository.findByUserIdOrderByLastReadAtDesc(100L)).thenReturn(progressList);

        List<ReadingProgressDTO> result = readingProgressService.getUserProgress(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getInProgressBooks_ShouldReturnInProgressBooks() {
        List<ReadingProgress> progressList = Arrays.asList(progress);
        when(progressRepository.findInProgressByUserId(100L)).thenReturn(progressList);

        List<ReadingProgressDTO> result = readingProgressService.getInProgressBooks(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCompletedBooks_ShouldReturnCompletedBooks() {
        List<ReadingProgress> progressList = Arrays.asList(progress);
        when(progressRepository.findByUserIdAndIsCompleted(100L, true)).thenReturn(progressList);

        List<ReadingProgressDTO> result = readingProgressService.getCompletedBooks(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
