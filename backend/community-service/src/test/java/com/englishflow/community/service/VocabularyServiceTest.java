package com.englishflow.community.service;

import com.englishflow.community.dto.SaveVocabularyRequest;
import com.englishflow.community.dto.VocabularyStatsDTO;
import com.englishflow.community.dto.VocabularyWordDTO;
import com.englishflow.community.entity.VocabularyWord;
import com.englishflow.community.repository.VocabularyWordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VocabularyServiceTest {

    @Mock
    private VocabularyWordRepository vocabularyWordRepository;

    @InjectMocks
    private VocabularyService vocabularyService;

    private VocabularyWord word1;
    private VocabularyWord word2;

    @BeforeEach
    void setUp() {
        word1 = new VocabularyWord();
        word1.setId(1L);
        word1.setUserId(100L);
        word1.setWord("eloquent");
        word1.setDefinition("Fluent or persuasive");
        word1.setMasteryLevel(VocabularyWord.MasteryLevel.NEW);
        word1.setReviewCount(0);
        word1.setCreatedAt(LocalDateTime.now());

        word2 = new VocabularyWord();
        word2.setId(2L);
        word2.setUserId(100L);
        word2.setWord("ubiquitous");
        word2.setDefinition("Present everywhere");
        word2.setMasteryLevel(VocabularyWord.MasteryLevel.LEARNING);
        word2.setReviewCount(3);
        word2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void saveWord_NewWord_ShouldSaveSuccessfully() {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        request.setWord("test");
        request.setDefinition("A test definition");

        when(vocabularyWordRepository.existsByUserIdAndWord(100L, "test")).thenReturn(false);
        when(vocabularyWordRepository.save(any(VocabularyWord.class))).thenReturn(word1);

        VocabularyWordDTO result = vocabularyService.saveWord(100L, request);

        assertNotNull(result);
        assertEquals("eloquent", result.getWord());
        verify(vocabularyWordRepository).existsByUserIdAndWord(100L, "test");
        verify(vocabularyWordRepository).save(any(VocabularyWord.class));
    }

    @Test
    void saveWord_DuplicateWord_ShouldThrowException() {
        SaveVocabularyRequest request = new SaveVocabularyRequest();
        request.setWord("duplicate");

        when(vocabularyWordRepository.existsByUserIdAndWord(100L, "duplicate")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            vocabularyService.saveWord(100L, request);
        });

        verify(vocabularyWordRepository, never()).save(any());
    }

    @Test
    void getUserVocabulary_ShouldReturnPagedWords() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<VocabularyWord> page = new PageImpl<>(Arrays.asList(word1, word2));

        when(vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(eq(100L), any(Pageable.class)))
                .thenReturn(page);

        Page<VocabularyWordDTO> result = vocabularyService.getUserVocabulary(100L, 0, 20, "createdAt");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("eloquent", result.getContent().get(0).getWord());
        verify(vocabularyWordRepository).findByUserIdOrderByCreatedAtDesc(eq(100L), any(Pageable.class));
    }

    @Test
    void getUserVocabularyByLevel_ShouldReturnFilteredWords() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<VocabularyWord> page = new PageImpl<>(Arrays.asList(word1));

        when(vocabularyWordRepository.findByUserIdAndMasteryLevelOrderByCreatedAtDesc(
                eq(100L), eq(VocabularyWord.MasteryLevel.NEW), any(Pageable.class)))
                .thenReturn(page);

        Page<VocabularyWordDTO> result = vocabularyService.getUserVocabularyByLevel(
                100L, "NEW", 0, 20, "createdAt");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(VocabularyWord.MasteryLevel.NEW, result.getContent().get(0).getMasteryLevel());
    }

    @Test
    void searchVocabulary_ShouldReturnMatchingWords() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<VocabularyWord> page = new PageImpl<>(Arrays.asList(word1));

        when(vocabularyWordRepository.searchByUserIdAndWord(eq(100L), eq("elo"), any(Pageable.class)))
                .thenReturn(page);

        Page<VocabularyWordDTO> result = vocabularyService.searchVocabulary(100L, "elo", 0, 20);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("eloquent", result.getContent().get(0).getWord());
    }

    @Test
    void getUserStats_ShouldReturnCorrectStats() {
        when(vocabularyWordRepository.countByUserId(100L)).thenReturn(50L);
        when(vocabularyWordRepository.countByUserIdAndMasteryLevel(100L, VocabularyWord.MasteryLevel.NEW))
                .thenReturn(10L);
        when(vocabularyWordRepository.countByUserIdAndMasteryLevel(100L, VocabularyWord.MasteryLevel.LEARNING))
                .thenReturn(30L);
        
        word1.setReviewCount(5);
        word2.setReviewCount(10);
        Page<VocabularyWord> allWords = new PageImpl<>(Arrays.asList(word1, word2));
        when(vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(eq(100L), any(Pageable.class)))
                .thenReturn(allWords);

        VocabularyStatsDTO stats = vocabularyService.getUserStats(100L);

        assertNotNull(stats);
        assertEquals(50L, stats.getTotalWords());
        assertEquals(10L, stats.getNewWords());
        assertEquals(30L, stats.getLearningWords());
        assertEquals(15L, stats.getTotalReviews());
    }

    @Test
    void markAsReviewed_ShouldIncrementReviewCount() {
        word1.setReviewCount(0);
        word1.setMasteryLevel(VocabularyWord.MasteryLevel.NEW);

        when(vocabularyWordRepository.findById(1L)).thenReturn(Optional.of(word1));
        when(vocabularyWordRepository.save(any(VocabularyWord.class))).thenReturn(word1);

        VocabularyWordDTO result = vocabularyService.markAsReviewed(100L, 1L);

        assertNotNull(result);
        assertEquals(1, word1.getReviewCount());
        assertEquals(VocabularyWord.MasteryLevel.LEARNING, word1.getMasteryLevel());
        assertNotNull(word1.getLastReviewedAt());
        verify(vocabularyWordRepository).save(word1);
    }

    @Test
    void markAsReviewed_UnauthorizedUser_ShouldThrowException() {
        when(vocabularyWordRepository.findById(1L)).thenReturn(Optional.of(word1));

        assertThrows(RuntimeException.class, () -> {
            vocabularyService.markAsReviewed(999L, 1L);
        });

        verify(vocabularyWordRepository, never()).save(any());
    }

    @Test
    void deleteWord_ShouldDeleteSuccessfully() {
        when(vocabularyWordRepository.findById(1L)).thenReturn(Optional.of(word1));
        doNothing().when(vocabularyWordRepository).delete(word1);

        vocabularyService.deleteWord(100L, 1L);

        verify(vocabularyWordRepository).delete(word1);
    }

    @Test
    void deleteWord_UnauthorizedUser_ShouldThrowException() {
        when(vocabularyWordRepository.findById(1L)).thenReturn(Optional.of(word1));

        assertThrows(RuntimeException.class, () -> {
            vocabularyService.deleteWord(999L, 1L);
        });

        verify(vocabularyWordRepository, never()).delete(any());
    }

    @Test
    void isWordSaved_ExistingWord_ShouldReturnTrue() {
        when(vocabularyWordRepository.existsByUserIdAndWord(100L, "test")).thenReturn(true);

        boolean result = vocabularyService.isWordSaved(100L, "test");

        assertTrue(result);
    }

    @Test
    void isWordSaved_NonExistingWord_ShouldReturnFalse() {
        when(vocabularyWordRepository.existsByUserIdAndWord(100L, "nonexistent")).thenReturn(false);

        boolean result = vocabularyService.isWordSaved(100L, "nonexistent");

        assertFalse(result);
    }

    @Test
    void getAllUserVocabulary_ShouldReturnAllWords() {
        Page<VocabularyWord> page = new PageImpl<>(Arrays.asList(word1, word2));
        when(vocabularyWordRepository.findByUserIdOrderByCreatedAtDesc(eq(100L), any(Pageable.class)))
                .thenReturn(page);

        List<VocabularyWordDTO> result = vocabularyService.getAllUserVocabulary(100L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("eloquent", result.get(0).getWord());
        assertEquals("ubiquitous", result.get(1).getWord());
    }
}
