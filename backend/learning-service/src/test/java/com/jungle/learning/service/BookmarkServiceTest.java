package com.jungle.learning.service;

import com.jungle.learning.dto.BookmarkDTO;
import com.jungle.learning.model.Bookmark;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.BookmarkRepository;
import com.jungle.learning.repository.ReadingProgressRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {
    
    @Mock
    private BookmarkRepository bookmarkRepository;
    
    @Mock
    private ReadingProgressRepository progressRepository;
    
    @InjectMocks
    private BookmarkService bookmarkService;
    
    private ReadingProgress testProgress;
    private Bookmark testBookmark;
    
    @BeforeEach
    void setUp() {
        testProgress = new ReadingProgress();
        testProgress.setId(1L);
        
        testBookmark = new Bookmark();
        testBookmark.setId(1L);
        testBookmark.setProgress(testProgress);
        testBookmark.setPageNumber(10);
        testBookmark.setNote("Important section");
        testBookmark.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void createBookmark_ShouldCreateAndReturnBookmark() {
        // Given
        when(progressRepository.findById(1L)).thenReturn(Optional.of(testProgress));
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(testBookmark);
        
        // When
        BookmarkDTO result = bookmarkService.createBookmark(1L, 10, "Important section");
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(10, result.getPageNumber());
        assertEquals("Important section", result.getNote());
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }
    
    @Test
    void createBookmark_WhenProgressNotFound_ShouldThrowException() {
        // Given
        when(progressRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                bookmarkService.createBookmark(999L, 10, "Note"));
    }
    
    @Test
    void deleteBookmark_ShouldDeleteBookmark() {
        // Given
        doNothing().when(bookmarkRepository).deleteById(1L);
        
        // When
        bookmarkService.deleteBookmark(1L);
        
        // Then
        verify(bookmarkRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void getProgressBookmarks_ShouldReturnBookmarks() {
        // Given
        List<Bookmark> bookmarks = Arrays.asList(testBookmark);
        when(bookmarkRepository.findByProgressIdOrderByPageNumberAsc(1L)).thenReturn(bookmarks);
        
        // When
        List<BookmarkDTO> result = bookmarkService.getProgressBookmarks(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getPageNumber());
        verify(bookmarkRepository, times(1)).findByProgressIdOrderByPageNumberAsc(1L);
    }
    
    @Test
    void getProgressBookmarks_WhenNoBookmarks_ShouldReturnEmptyList() {
        // Given
        when(bookmarkRepository.findByProgressIdOrderByPageNumberAsc(1L)).thenReturn(Arrays.asList());
        
        // When
        List<BookmarkDTO> result = bookmarkService.getProgressBookmarks(1L);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
