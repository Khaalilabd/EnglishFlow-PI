package com.jungle.learning.service;

import com.jungle.learning.dto.QuizDTO;
import com.jungle.learning.exception.ResourceNotFoundException;
import com.jungle.learning.model.Quiz;
import com.jungle.learning.repository.QuizRepository;
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
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz quiz;
    private QuizDTO quizDTO;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setCourseId(1L);
        quiz.setDurationMinutes(30);
        quiz.setMaxScore(100);
        quiz.setPassingScore(60);
        quiz.setPublished(true);
        quiz.setCategory("Grammar");
        quiz.setDifficulty("medium");
        quiz.setCreatedAt(LocalDateTime.now());

        quizDTO = new QuizDTO();
        quizDTO.setTitle("Test Quiz");
        quizDTO.setDescription("Test Description");
        quizDTO.setCourseId(1L);
        quizDTO.setDurationMin(30);
        quizDTO.setMaxScore(100);
        quizDTO.setPassingScore(60);
        quizDTO.setPublished(true);
        quizDTO.setCategory("Grammar");
        quizDTO.setDifficulty("medium");
    }

    @Test
    void getAllQuizzes_ShouldReturnAllQuizzes() {
        // Given
        when(quizRepository.findAll()).thenReturn(Arrays.asList(quiz));

        // When
        List<QuizDTO> result = quizService.getAllQuizzes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTitle());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getPublishedQuizzes_ShouldReturnOnlyPublishedQuizzes() {
        // Given
        when(quizRepository.findByPublished(true)).thenReturn(Arrays.asList(quiz));

        // When
        List<QuizDTO> result = quizService.getPublishedQuizzes();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getPublished());
        verify(quizRepository, times(1)).findByPublished(true);
    }

    @Test
    void getQuizzesByCategory_ShouldReturnQuizzesInCategory() {
        // Given
        String category = "Grammar";
        when(quizRepository.findByCategoryAndPublishedTrue(category)).thenReturn(Arrays.asList(quiz));

        // When
        List<QuizDTO> result = quizService.getQuizzesByCategory(category);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.get(0).getCategory());
        verify(quizRepository, times(1)).findByCategoryAndPublishedTrue(category);
    }

    @Test
    void getQuizzesByDifficulty_ShouldReturnQuizzesWithDifficulty() {
        // Given
        String difficulty = "medium";
        when(quizRepository.findByDifficultyAndPublishedTrue(difficulty)).thenReturn(Arrays.asList(quiz));

        // When
        List<QuizDTO> result = quizService.getQuizzesByDifficulty(difficulty);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(difficulty, result.get(0).getDifficulty());
        verify(quizRepository, times(1)).findByDifficultyAndPublishedTrue(difficulty);
    }

    @Test
    void getQuizzesByCourse_ShouldReturnQuizzesForCourse() {
        // Given
        Long courseId = 1L;
        when(quizRepository.findByCourseId(courseId)).thenReturn(Arrays.asList(quiz));

        // When
        List<QuizDTO> result = quizService.getQuizzesByCourse(courseId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseId, result.get(0).getCourseId());
        verify(quizRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    void getQuizById_WhenQuizExists_ShouldReturnQuiz() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // When
        QuizDTO result = quizService.getQuizById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Quiz", result.getTitle());
        verify(quizRepository, times(1)).findById(1L);
    }

    @Test
    void getQuizById_WhenQuizNotExists_ShouldThrowException() {
        // Given
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> quizService.getQuizById(999L));
        verify(quizRepository, times(1)).findById(999L);
    }

    @Test
    void createQuiz_ShouldSaveAndReturnQuiz() {
        // Given
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        // When
        QuizDTO result = quizService.createQuiz(quizDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Quiz", result.getTitle());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void updateQuiz_WhenQuizExists_ShouldUpdateAndReturn() {
        // Given
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        quizDTO.setTitle("Updated Quiz");

        // When
        QuizDTO result = quizService.updateQuiz(1L, quizDTO);

        // Then
        assertNotNull(result);
        verify(quizRepository, times(1)).findById(1L);
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void updateQuiz_WhenQuizNotExists_ShouldThrowException() {
        // Given
        when(quizRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> quizService.updateQuiz(999L, quizDTO));
        verify(quizRepository, times(1)).findById(999L);
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void deleteQuiz_WhenQuizExists_ShouldDelete() {
        // Given
        when(quizRepository.existsById(1L)).thenReturn(true);

        // When
        quizService.deleteQuiz(1L);

        // Then
        verify(quizRepository, times(1)).existsById(1L);
        verify(quizRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteQuiz_WhenQuizNotExists_ShouldThrowException() {
        // Given
        when(quizRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> quizService.deleteQuiz(999L));
        verify(quizRepository, times(1)).existsById(999L);
        verify(quizRepository, never()).deleteById(anyLong());
    }
}
