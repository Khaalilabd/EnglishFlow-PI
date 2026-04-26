package com.englishflow.exam.service;

import com.englishflow.exam.dto.request.CreateExamDTO;
import com.englishflow.exam.dto.request.UpdateExamDTO;
import com.englishflow.exam.dto.response.ExamDetailDTO;
import com.englishflow.exam.dto.response.ExamSummaryDTO;
import com.englishflow.exam.entity.Exam;
import com.englishflow.exam.enums.ExamLevel;
import com.englishflow.exam.repository.ExamRepository;
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
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private ExamService examService;

    private Exam testExam;
    private CreateExamDTO createExamDTO;

    @BeforeEach
    void setUp() {
        testExam = Exam.builder()
                .id("exam-123")
                .title("CEFR A1 English Test")
                .level(ExamLevel.A1)
                .description("Beginner level English exam")
                .totalDuration(60)
                .passingScore(70.0)
                .isPublished(false)
                .build();

        createExamDTO = new CreateExamDTO();
        createExamDTO.setTitle("CEFR A1 English Test");
        createExamDTO.setLevel(ExamLevel.A1);
        createExamDTO.setDescription("Beginner level English exam");
        createExamDTO.setTotalDuration(60);
        createExamDTO.setPassingScore(70.0);
    }

    @Test
    void createExam_ShouldCreateAndReturnExam() {
        // Arrange
        when(examRepository.save(any(Exam.class))).thenReturn(testExam);

        // Act
        ExamSummaryDTO result = examService.createExam(createExamDTO);

        // Assert
        assertNotNull(result);
        assertEquals("CEFR A1 English Test", result.getTitle());
        assertEquals(ExamLevel.A1, result.getLevel());
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    void getExamById_WhenExamExists_ShouldReturnExam() {
        // Arrange
        when(examRepository.findById("exam-123")).thenReturn(Optional.of(testExam));

        // Act
        ExamDetailDTO result = examService.getExamById("exam-123");

        // Assert
        assertNotNull(result);
        assertEquals("CEFR A1 English Test", result.getTitle());
        verify(examRepository, times(1)).findById("exam-123");
    }

    @Test
    void getExamById_WhenExamNotExists_ShouldThrowException() {
        // Arrange
        when(examRepository.findById("invalid-id")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> examService.getExamById("invalid-id"));
        verify(examRepository, times(1)).findById("invalid-id");
    }

    @Test
    void getAllExams_ShouldReturnAllExams() {
        // Arrange
        List<Exam> exams = Arrays.asList(testExam);
        when(examRepository.findAll()).thenReturn(exams);

        // Act
        List<ExamSummaryDTO> result = examService.getAllExams();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(examRepository, times(1)).findAll();
    }

    @Test
    void getPublishedExams_WithLevel_ShouldReturnFilteredExams() {
        // Arrange
        testExam.setIsPublished(true);
        List<Exam> exams = Arrays.asList(testExam);
        when(examRepository.findByLevelAndIsPublished(ExamLevel.A1, true)).thenReturn(exams);

        // Act
        List<ExamSummaryDTO> result = examService.getPublishedExams(ExamLevel.A1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(examRepository, times(1)).findByLevelAndIsPublished(ExamLevel.A1, true);
    }

    @Test
    void updateExam_ShouldUpdateAndReturnExam() {
        // Arrange
        UpdateExamDTO updateDTO = new UpdateExamDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setPassingScore(75.0);
        when(examRepository.findById("exam-123")).thenReturn(Optional.of(testExam));
        when(examRepository.save(any(Exam.class))).thenReturn(testExam);

        // Act
        ExamSummaryDTO result = examService.updateExam("exam-123", updateDTO);

        // Assert
        assertNotNull(result);
        verify(examRepository, times(1)).save(testExam);
    }

    @Test
    void deleteExam_WhenExamExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(examRepository.existsById("exam-123")).thenReturn(true);
        doNothing().when(examRepository).deleteById("exam-123");

        // Act
        examService.deleteExam("exam-123");

        // Assert
        verify(examRepository, times(1)).deleteById("exam-123");
    }

    @Test
    void deleteExam_WhenExamNotExists_ShouldThrowException() {
        // Arrange
        when(examRepository.existsById("invalid-id")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> examService.deleteExam("invalid-id"));
        verify(examRepository, never()).deleteById("invalid-id");
    }

    @Test
    void publishExam_ShouldSetPublishedToTrue() {
        // Arrange
        when(examRepository.findById("exam-123")).thenReturn(Optional.of(testExam));
        when(examRepository.save(any(Exam.class))).thenReturn(testExam);

        // Act
        examService.publishExam("exam-123");

        // Assert
        assertTrue(testExam.getIsPublished());
        verify(examRepository, times(1)).save(testExam);
    }

    @Test
    void unpublishExam_ShouldSetPublishedToFalse() {
        // Arrange
        testExam.setIsPublished(true);
        when(examRepository.findById("exam-123")).thenReturn(Optional.of(testExam));
        when(examRepository.save(any(Exam.class))).thenReturn(testExam);

        // Act
        examService.unpublishExam("exam-123");

        // Assert
        assertFalse(testExam.getIsPublished());
        verify(examRepository, times(1)).save(testExam);
    }
}
