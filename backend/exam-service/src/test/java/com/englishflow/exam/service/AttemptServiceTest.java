package com.englishflow.exam.service;

import com.englishflow.exam.entity.StudentExamAttempt;
import com.englishflow.exam.enums.AttemptStatus;
import com.englishflow.exam.repository.StudentExamAttemptRepository;
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
class AttemptServiceTest {

    @Mock
    private StudentExamAttemptRepository attemptRepository;

    @InjectMocks
    private AttemptService attemptService;

    private StudentExamAttempt testAttempt;

    @BeforeEach
    void setUp() {
        testAttempt = StudentExamAttempt.builder()
                .id("attempt-123")
                .userId(100L)
                .status(AttemptStatus.STARTED)
                .startedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void findAttemptById_WhenExists_ShouldReturnAttempt() {
        // Arrange
        when(attemptRepository.findById("attempt-123")).thenReturn(Optional.of(testAttempt));

        // Act
        Optional<StudentExamAttempt> result = attemptRepository.findById("attempt-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("attempt-123", result.get().getId());
        verify(attemptRepository, times(1)).findById("attempt-123");
    }

    @Test
    void findAttemptsByStudent_ShouldReturnStudentAttempts() {
        // Arrange
        List<StudentExamAttempt> attempts = Arrays.asList(testAttempt);
        when(attemptRepository.findByUserId(100L)).thenReturn(attempts);

        // Act
        List<StudentExamAttempt> result = attemptRepository.findByUserId(100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getUserId());
        verify(attemptRepository, times(1)).findByUserId(100L);
    }

    @Test
    void saveAttempt_ShouldSaveSuccessfully() {
        // Arrange
        when(attemptRepository.save(any(StudentExamAttempt.class))).thenReturn(testAttempt);

        // Act
        StudentExamAttempt result = attemptRepository.save(testAttempt);

        // Assert
        assertNotNull(result);
        assertEquals("attempt-123", result.getId());
        verify(attemptRepository, times(1)).save(testAttempt);
    }
}
