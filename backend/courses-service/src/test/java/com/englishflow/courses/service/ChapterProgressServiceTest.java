package com.englishflow.courses.service;

import com.englishflow.courses.dto.ChapterProgressDTO;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.ChapterProgress;
import com.englishflow.courses.repository.ChapterProgressRepository;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ChapterProgressServiceTest {

    @Mock
    private ChapterProgressRepository chapterProgressRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonProgressService lessonProgressService;

    private ChapterProgressService chapterProgressService;

    private Chapter chapter;
    private ChapterProgress progress;

    @BeforeEach
    void setUp() {
        chapterProgressService = new ChapterProgressService(
                chapterProgressRepository,
                chapterRepository,
                lessonRepository,
                lessonProgressService
        );

        chapter = new Chapter();
        chapter.setId(1L);
        chapter.setTitle("Chapter 1");

        progress = new ChapterProgress();
        progress.setId(1L);
        progress.setStudentId(1L);
        progress.setChapter(chapter);
        progress.setIsCompleted(false);
        progress.setCompletedLessons(0);
        progress.setTotalLessons(10);
        progress.setProgressPercentage(0.0);
        progress.setStartedAt(LocalDateTime.now());
    }

    @Test
    void startChapter_WhenNew_ShouldCreateProgress() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterProgressRepository.findByStudentIdAndChapterId(1L, 1L)).thenReturn(Optional.empty());
        when(lessonRepository.countByChapterId(1L)).thenReturn(10L);
        when(chapterProgressRepository.save(any(ChapterProgress.class))).thenReturn(progress);

        ChapterProgressDTO result = chapterProgressService.startChapter(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals(1L, result.getChapterId());
        verify(chapterProgressRepository, times(1)).save(any(ChapterProgress.class));
    }

    @Test
    void startChapter_WhenExists_ShouldUpdateLastAccessed() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(chapterProgressRepository.findByStudentIdAndChapterId(1L, 1L)).thenReturn(Optional.of(progress));
        when(chapterProgressRepository.save(any(ChapterProgress.class))).thenReturn(progress);

        ChapterProgressDTO result = chapterProgressService.startChapter(1L, 1L);

        assertNotNull(result);
        assertNotNull(progress.getLastAccessedAt());
        verify(chapterProgressRepository, times(1)).save(progress);
    }

    @Test
    void updateChapterProgress_ShouldUpdateProgress() {
        when(chapterProgressRepository.findByStudentIdAndChapterId(1L, 1L)).thenReturn(Optional.of(progress));
        when(chapterProgressRepository.save(any(ChapterProgress.class))).thenReturn(progress);

        ChapterProgressDTO result = chapterProgressService.updateChapterProgress(1L, 1L);

        assertNotNull(result);
        verify(chapterProgressRepository, times(1)).save(progress);
    }

    @Test
    void getChapterProgress_ShouldReturnProgress() {
        when(chapterProgressRepository.findByStudentIdAndChapterId(1L, 1L)).thenReturn(Optional.of(progress));

        ChapterProgressDTO result = chapterProgressService.getChapterProgress(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals(1L, result.getChapterId());
    }

    @Test
    void getStudentChapterProgress_ShouldReturnProgressList() {
        when(chapterProgressRepository.findByStudentId(1L)).thenReturn(Arrays.asList(progress));

        List<ChapterProgressDTO> result = chapterProgressService.getStudentChapterProgress(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(chapterProgressRepository, times(1)).findByStudentId(1L);
    }

    @Test
    void getStudentCourseChapterProgress_ShouldReturnProgressList() {
        when(chapterProgressRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Arrays.asList(progress));

        List<ChapterProgressDTO> result = chapterProgressService.getStudentCourseChapterProgress(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(chapterProgressRepository, times(1)).findByStudentIdAndCourseId(1L, 1L);
    }

    @Test
    void hasStartedChapter_ShouldReturnTrue() {
        when(chapterProgressRepository.existsByStudentIdAndChapterId(1L, 1L)).thenReturn(true);

        boolean result = chapterProgressService.hasStartedChapter(1L, 1L);

        assertTrue(result);
        verify(chapterProgressRepository, times(1)).existsByStudentIdAndChapterId(1L, 1L);
    }

    @Test
    void countCompletedChaptersInCourse_ShouldReturnCount() {
        when(chapterProgressRepository.countCompletedChaptersByStudentAndCourse(1L, 1L)).thenReturn(5L);

        Long result = chapterProgressService.countCompletedChaptersInCourse(1L, 1L);

        assertEquals(5L, result);
        verify(chapterProgressRepository, times(1)).countCompletedChaptersByStudentAndCourse(1L, 1L);
    }

    @Test
    void updateCourseChapterProgress_ShouldUpdateAllChapters() {
        when(chapterProgressRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Arrays.asList(progress));
        when(chapterProgressRepository.findByStudentIdAndChapterId(1L, 1L)).thenReturn(Optional.of(progress));
        when(chapterProgressRepository.save(any(ChapterProgress.class))).thenReturn(progress);

        chapterProgressService.updateCourseChapterProgress(1L, 1L);

        verify(chapterProgressRepository, times(1)).findByStudentIdAndCourseId(1L, 1L);
        verify(chapterProgressRepository, times(1)).save(any(ChapterProgress.class));
    }
}
