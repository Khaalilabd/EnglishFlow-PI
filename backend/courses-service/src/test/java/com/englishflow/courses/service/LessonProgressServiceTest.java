package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseProgressSummary;
import com.englishflow.courses.dto.CreateLessonProgressRequest;
import com.englishflow.courses.entity.LessonProgress;
import com.englishflow.courses.repository.LessonProgressRepository;
import com.englishflow.courses.repository.LessonRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonProgressServiceTest {

    @Mock
    private LessonProgressRepository progressRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseEnrollmentService courseEnrollmentService;

    @Mock
    private PackEnrollmentService packEnrollmentService;

    private LessonProgressService progressService;

    private LessonProgress progress;

    @BeforeEach
    void setUp() {
        progressService = new LessonProgressService(
                progressRepository,
                lessonRepository,
                courseEnrollmentService,
                packEnrollmentService
        );

        progress = new LessonProgress();
        progress.setId(1L);
        progress.setStudentId(1L);
        progress.setLessonId(1L);
        progress.setCourseId(1L);
        progress.setIsCompleted(false);
        progress.setTimeSpent(30);
        progress.setLastAccessedAt(LocalDateTime.now());
    }

    @Test
    void getProgressByStudentAndLesson_ShouldReturnProgress() {
        when(progressRepository.findByStudentIdAndLessonId(1L, 1L)).thenReturn(Optional.of(progress));

        LessonProgress result = progressService.getProgressByStudentAndLesson(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals(1L, result.getLessonId());
        verify(progressRepository, times(1)).findByStudentIdAndLessonId(1L, 1L);
    }

    @Test
    void getProgressByStudentAndCourse_ShouldReturnProgressList() {
        when(progressRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Arrays.asList(progress));

        List<LessonProgress> result = progressService.getProgressByStudentAndCourse(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(progressRepository, times(1)).findByStudentIdAndCourseId(1L, 1L);
    }

    @Test
    void getCourseProgressSummary_ShouldReturnSummary() {
        when(lessonRepository.countPublishedByCourseId(1L)).thenReturn(10L);
        when(progressRepository.countCompletedLessonsByStudentAndCourse(1L, 1L)).thenReturn(5L);
        when(progressRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Arrays.asList(progress));

        CourseProgressSummary result = progressService.getCourseProgressSummary(1L, 1L);

        assertNotNull(result);
        assertEquals(10, result.getTotalLessons());
        assertEquals(5, result.getCompletedLessons());
        assertEquals(50.0, result.getProgressPercentage());
    }

    @Test
    void createOrUpdateProgress_WhenNew_ShouldCreateProgress() {
        CreateLessonProgressRequest request = new CreateLessonProgressRequest();
        request.setStudentId(1L);
        request.setLessonId(1L);
        request.setCourseId(1L);
        request.setIsCompleted(false);
        request.setTimeSpent(30);

        when(progressRepository.findByStudentIdAndLessonId(1L, 1L)).thenReturn(Optional.empty());
        when(progressRepository.save(any(LessonProgress.class))).thenReturn(progress);

        LessonProgress result = progressService.createOrUpdateProgress(request);

        assertNotNull(result);
        verify(progressRepository, times(1)).save(any(LessonProgress.class));
    }

    @Test
    void createOrUpdateProgress_WhenCompleted_ShouldTriggerCourseCompletion() {
        CreateLessonProgressRequest request = new CreateLessonProgressRequest();
        request.setStudentId(1L);
        request.setLessonId(1L);
        request.setCourseId(1L);
        request.setIsCompleted(true);

        when(progressRepository.findByStudentIdAndLessonId(1L, 1L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(LessonProgress.class))).thenReturn(progress);
        doNothing().when(courseEnrollmentService).checkAndMarkCourseCompletion(1L, 1L, null);

        LessonProgress result = progressService.createOrUpdateProgress(request);

        assertNotNull(result);
        verify(courseEnrollmentService, times(1)).checkAndMarkCourseCompletion(1L, 1L, null);
    }

    @Test
    void countCompletedLessonsInCourse_ShouldReturnCount() {
        when(progressRepository.countCompletedLessonsByStudentAndCourse(1L, 1L)).thenReturn(5L);

        Long result = progressService.countCompletedLessonsInCourse(1L, 1L);

        assertEquals(5L, result);
        verify(progressRepository, times(1)).countCompletedLessonsByStudentAndCourse(1L, 1L);
    }

    @Test
    void startLesson_WhenNotExists_ShouldCreateProgress() {
        when(progressRepository.existsByStudentIdAndLessonId(1L, 1L)).thenReturn(false);
        when(progressRepository.save(any(LessonProgress.class))).thenReturn(progress);

        progressService.startLesson(1L, 1L);

        verify(progressRepository, times(1)).save(any(LessonProgress.class));
    }

    @Test
    void updateProgress_ShouldUpdateTimeSpent() {
        when(progressRepository.findByStudentIdAndLessonId(1L, 1L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(LessonProgress.class))).thenReturn(progress);

        progressService.updateProgress(1L, 1L, 50.0, 60);

        assertEquals(60, progress.getTimeSpent());
        verify(progressRepository, times(1)).save(progress);
    }

    @Test
    void completeLesson_ShouldMarkAsCompleted() {
        when(progressRepository.findByStudentIdAndLessonId(1L, 1L)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(LessonProgress.class))).thenReturn(progress);

        progressService.completeLesson(1L, 1L);

        assertTrue(progress.getIsCompleted());
        assertNotNull(progress.getCompletedAt());
        verify(progressRepository, times(1)).save(progress);
    }

    @Test
    void deleteProgressByStudentAndCourse_ShouldDeleteProgress() {
        doNothing().when(progressRepository).deleteByStudentIdAndCourseId(1L, 1L);

        progressService.deleteProgressByStudentAndCourse(1L, 1L);

        verify(progressRepository, times(1)).deleteByStudentIdAndCourseId(1L, 1L);
    }

    @Test
    void countCompletedLessonsInChapter_ShouldReturnCount() {
        progress.setIsCompleted(true);
        when(progressRepository.findByStudentId(1L)).thenReturn(Arrays.asList(progress));

        Long result = progressService.countCompletedLessonsInChapter(1L, 1L);

        assertEquals(1L, result);
    }
}
