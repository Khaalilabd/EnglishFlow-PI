package com.englishflow.courses.service;

import com.englishflow.courses.dto.ChapterDTO;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.CourseRepository;
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
class ChapterServiceTest {

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private ChapterService chapterService;

    private Chapter testChapter;
    private ChapterDTO testChapterDTO;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");

        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setTitle("Test Chapter");
        testChapter.setDescription("Test Description");
        testChapter.setObjectives(Arrays.asList("Objective 1", "Objective 2"));
        testChapter.setOrderIndex(1);
        testChapter.setEstimatedDuration(60);
        testChapter.setIsPublished(true);
        testChapter.setCourse(testCourse);
        testChapter.setCreatedAt(LocalDateTime.now());
        testChapter.setUpdatedAt(LocalDateTime.now());

        testChapterDTO = new ChapterDTO();
        testChapterDTO.setId(1L);
        testChapterDTO.setTitle("Test Chapter");
        testChapterDTO.setDescription("Test Description");
        testChapterDTO.setObjectives(Arrays.asList("Objective 1", "Objective 2"));
        testChapterDTO.setOrderIndex(1);
        testChapterDTO.setEstimatedDuration(60);
        testChapterDTO.setIsPublished(true);
        testChapterDTO.setCourseId(1L);
    }

    @Test
    void getAllChapters_ShouldReturnAllChapters() {
        List<Chapter> chapters = Arrays.asList(testChapter);
        when(chapterRepository.findAll()).thenReturn(chapters);

        List<ChapterDTO> result = chapterService.getAllChapters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Chapter", result.get(0).getTitle());
        verify(chapterRepository, times(1)).findAll();
    }

    @Test
    void getChapterById_WhenChapterExists_ShouldReturnChapter() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));

        ChapterDTO result = chapterService.getChapterById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Chapter", result.getTitle());
        verify(chapterRepository, times(1)).findById(1L);
    }

    @Test
    void getChapterById_WhenChapterNotExists_ShouldThrowException() {
        when(chapterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chapterService.getChapterById(999L));
        verify(chapterRepository, times(1)).findById(999L);
    }

    @Test
    void getChaptersByCourse_ShouldReturnChaptersOrderedByIndex() {
        List<Chapter> chapters = Arrays.asList(testChapter);
        when(chapterRepository.findByCourseIdOrderByOrderIndexAsc(1L)).thenReturn(chapters);

        List<ChapterDTO> result = chapterService.getChaptersByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCourseId());
        verify(chapterRepository, times(1)).findByCourseIdOrderByOrderIndexAsc(1L);
    }

    @Test
    void getPublishedChaptersByCourse_ShouldReturnOnlyPublishedChapters() {
        List<Chapter> chapters = Arrays.asList(testChapter);
        when(chapterRepository.findByCourseIdAndIsPublished(1L, true)).thenReturn(chapters);

        List<ChapterDTO> result = chapterService.getPublishedChaptersByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPublished());
        verify(chapterRepository, times(1)).findByCourseIdAndIsPublished(1L, true);
    }

    @Test
    void createChapter_WithValidData_ShouldCreateChapter() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(chapterRepository.save(any(Chapter.class))).thenReturn(testChapter);

        ChapterDTO result = chapterService.createChapter(testChapterDTO);

        assertNotNull(result);
        assertEquals("Test Chapter", result.getTitle());
        verify(courseRepository, times(1)).findById(1L);
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }

    @Test
    void createChapter_WhenCourseNotExists_ShouldThrowException() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chapterService.createChapter(testChapterDTO));
        verify(courseRepository, times(1)).findById(1L);
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void updateChapter_WhenChapterExists_ShouldUpdateChapter() {
        testChapterDTO.setTitle("Updated Chapter");
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));
        when(chapterRepository.save(any(Chapter.class))).thenReturn(testChapter);

        ChapterDTO result = chapterService.updateChapter(1L, testChapterDTO);

        assertNotNull(result);
        verify(chapterRepository, times(1)).findById(1L);
        verify(chapterRepository, times(1)).save(any(Chapter.class));
    }

    @Test
    void updateChapter_WhenChapterNotExists_ShouldThrowException() {
        when(chapterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chapterService.updateChapter(999L, testChapterDTO));
        verify(chapterRepository, times(1)).findById(999L);
        verify(chapterRepository, never()).save(any(Chapter.class));
    }

    @Test
    void deleteChapter_WhenChapterExists_ShouldDeleteChapter() {
        when(chapterRepository.existsById(1L)).thenReturn(true);
        doNothing().when(chapterRepository).deleteById(1L);

        chapterService.deleteChapter(1L);

        verify(chapterRepository, times(1)).existsById(1L);
        verify(chapterRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteChapter_WhenChapterNotExists_ShouldThrowException() {
        when(chapterRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> chapterService.deleteChapter(999L));
        verify(chapterRepository, times(1)).existsById(999L);
        verify(chapterRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_WhenChapterExists_ShouldReturnTrue() {
        when(chapterRepository.existsById(1L)).thenReturn(true);

        boolean result = chapterService.existsById(1L);

        assertTrue(result);
        verify(chapterRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenChapterNotExists_ShouldReturnFalse() {
        when(chapterRepository.existsById(anyLong())).thenReturn(false);

        boolean result = chapterService.existsById(999L);

        assertFalse(result);
        verify(chapterRepository, times(1)).existsById(999L);
    }

    @Test
    void belongsToCourse_WhenChapterBelongsToCourse_ShouldReturnTrue() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));

        boolean result = chapterService.belongsToCourse(1L, 1L);

        assertTrue(result);
        verify(chapterRepository, times(1)).findById(1L);
    }

    @Test
    void belongsToCourse_WhenChapterNotBelongsToCourse_ShouldReturnFalse() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));

        boolean result = chapterService.belongsToCourse(1L, 999L);

        assertFalse(result);
        verify(chapterRepository, times(1)).findById(1L);
    }

    @Test
    void belongsToCourse_WhenChapterNotExists_ShouldReturnFalse() {
        when(chapterRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = chapterService.belongsToCourse(999L, 1L);

        assertFalse(result);
        verify(chapterRepository, times(1)).findById(999L);
    }
}
