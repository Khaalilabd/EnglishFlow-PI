package com.jungle.learning.service;

import com.jungle.learning.dto.NoteDTO;
import com.jungle.learning.model.Note;
import com.jungle.learning.model.ReadingProgress;
import com.jungle.learning.repository.NoteRepository;
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
class NoteServiceTest {
    
    @Mock
    private NoteRepository noteRepository;
    
    @Mock
    private ReadingProgressRepository progressRepository;
    
    @InjectMocks
    private NoteService noteService;
    
    private ReadingProgress testProgress;
    private Note testNote;
    
    @BeforeEach
    void setUp() {
        testProgress = new ReadingProgress();
        testProgress.setId(1L);
        
        testNote = new Note();
        testNote.setId(1L);
        testNote.setProgress(testProgress);
        testNote.setPageNumber(15);
        testNote.setContent("My note");
        testNote.setHighlightedText("Important text");
        testNote.setColor("#FFEB3B");
        testNote.setCreatedAt(LocalDateTime.now());
        testNote.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void createNote_ShouldCreateAndReturnNote() {
        // Given
        when(progressRepository.findById(1L)).thenReturn(Optional.of(testProgress));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);
        
        // When
        NoteDTO result = noteService.createNote(1L, 15, "My note", "Important text", "#FFEB3B");
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(15, result.getPageNumber());
        assertEquals("My note", result.getContent());
        assertEquals("Important text", result.getHighlightedText());
        assertEquals("#FFEB3B", result.getColor());
        verify(noteRepository, times(1)).save(any(Note.class));
    }
    
    @Test
    void createNote_WithDefaultColor_ShouldUseDefaultColor() {
        // Given
        when(progressRepository.findById(1L)).thenReturn(Optional.of(testProgress));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);
        
        // When
        NoteDTO result = noteService.createNote(1L, 15, "My note", "Important text", null);
        
        // Then
        assertNotNull(result);
        verify(noteRepository, times(1)).save(any(Note.class));
    }
    
    @Test
    void createNote_WhenProgressNotFound_ShouldThrowException() {
        // Given
        when(progressRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                noteService.createNote(999L, 15, "Note", "Text", "#FFEB3B"));
    }
    
    @Test
    void updateNote_ShouldUpdateAndReturnNote() {
        // Given
        when(noteRepository.findById(1L)).thenReturn(Optional.of(testNote));
        when(noteRepository.save(any(Note.class))).thenReturn(testNote);
        
        // When
        NoteDTO result = noteService.updateNote(1L, "Updated content");
        
        // Then
        assertNotNull(result);
        verify(noteRepository, times(1)).save(any(Note.class));
    }
    
    @Test
    void updateNote_WhenNoteNotFound_ShouldThrowException() {
        // Given
        when(noteRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> 
                noteService.updateNote(999L, "Updated content"));
    }
    
    @Test
    void deleteNote_ShouldDeleteNote() {
        // Given
        doNothing().when(noteRepository).deleteById(1L);
        
        // When
        noteService.deleteNote(1L);
        
        // Then
        verify(noteRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void getProgressNotes_ShouldReturnNotes() {
        // Given
        List<Note> notes = Arrays.asList(testNote);
        when(noteRepository.findByProgressIdOrderByPageNumberAsc(1L)).thenReturn(notes);
        
        // When
        List<NoteDTO> result = noteService.getProgressNotes(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("My note", result.get(0).getContent());
        verify(noteRepository, times(1)).findByProgressIdOrderByPageNumberAsc(1L);
    }
    
    @Test
    void getPageNotes_ShouldReturnNotesForPage() {
        // Given
        List<Note> notes = Arrays.asList(testNote);
        when(noteRepository.findByProgressIdAndPageNumber(1L, 15)).thenReturn(notes);
        
        // When
        List<NoteDTO> result = noteService.getPageNotes(1L, 15);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(15, result.get(0).getPageNumber());
        verify(noteRepository, times(1)).findByProgressIdAndPageNumber(1L, 15);
    }
    
    @Test
    void getPageNotes_WhenNoNotes_ShouldReturnEmptyList() {
        // Given
        when(noteRepository.findByProgressIdAndPageNumber(1L, 15)).thenReturn(Arrays.asList());
        
        // When
        List<NoteDTO> result = noteService.getPageNotes(1L, 15);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
