package com.englishflow.courses.service;

import com.englishflow.courses.dto.LessonDTO;
import com.englishflow.courses.entity.Chapter;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.enums.LessonType;
import com.englishflow.courses.repository.ChapterRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson testLesson;
    private LessonDTO testLessonDTO;
    private Chapter testChapter;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");

        testChapter = new Chapter();
        testChapter.setId(1L);
        testChapter.setTitle("Test Chapter");
        testChapter.setCourse(testCourse);

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle("Test Lesson");
        testLesson.setDescription("Test Description");
        testLesson.setContent("Test Content");
        testLesson.setLessonType(LessonType.VIDEO);
        testLesson.setOrderIndex(1);
        testLesson.setDuration(30);
        testLesson.setIsPreview(false);
        testLesson.setIsPublished(true);
        testLesson.setChapter(testChapter);
        testLesson.setCreatedAt(LocalDateTime.now());
        testLesson.setUpdatedAt(LocalDateTime.now());

        testLessonDTO = new LessonDTO();
        testLessonDTO.setId(1L);
        testLessonDTO.setTitle("Test Lesson");
        testLessonDTO.setDescription("Test Description");
        testLessonDTO.setContent("Test Content");
        testLessonDTO.setLessonType(LessonType.VIDEO);
        testLessonDTO.setOrderIndex(1);
        testLessonDTO.setDuration(30);
        testLessonDTO.setIsPreview(false);
        testLessonDTO.setIsPublished(true);
        testLessonDTO.setChapterId(1L);
    }

    @Test
    void getAllLessons_ShouldReturnAllLessons() {
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findAll()).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getAllLessons();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Lesson", result.get(0).getTitle());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    void getLessonById_WhenLessonExists_ShouldReturnLesson() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        LessonDTO result = lessonService.getLessonById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Lesson", result.getTitle());
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void getLessonById_WhenLessonNotExists_ShouldThrowException() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lessonService.getLessonById(999L));
        verify(lessonRepository, times(1)).findById(999L);
    }

    @Test
    void getLessonsByChapter_ShouldReturnLessonsOrderedByIndex() {
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByChapterIdOrderByOrderIndexAsc(1L)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getLessonsByChapter(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getChapterId());
        verify(lessonRepository, times(1)).findByChapterIdOrderByOrderIndexAsc(1L);
    }

    @Test
    void getPublishedLessonsByChapter_ShouldReturnOnlyPublishedLessons() {
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByChapterIdAndIsPublished(1L, true)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getPublishedLessonsByChapter(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPublished());
        verify(lessonRepository, times(1)).findByChapterIdAndIsPublished(1L, true);
    }

    @Test
    void getLessonsByCourse_ShouldReturnCourseLessons() {
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByCourseId(1L)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getLessonsByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lessonRepository, times(1)).findByCourseId(1L);
    }

    @Test
    void getLessonsByType_ShouldReturnLessonsOfSpecificType() {
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByLessonType(LessonType.VIDEO)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getLessonsByType(LessonType.VIDEO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(LessonType.VIDEO, result.get(0).getLessonType());
        verify(lessonRepository, times(1)).findByLessonType(LessonType.VIDEO);
    }

    @Test
    void getPreviewLessonsByCourse_ShouldReturnPreviewLessons() {
        testLesson.setIsPreview(true);
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByCourseIdAndIsPreview(1L, true)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getPreviewLessonsByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lessonRepository, times(1)).findByCourseIdAndIsPreview(1L, true);
    }

    @Test
    void createLesson_WithValidData_ShouldCreateLesson() {
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);

        LessonDTO result = lessonService.createLesson(testLessonDTO);

        assertNotNull(result);
        assertEquals("Test Lesson", result.getTitle());
        verify(chapterRepository, times(1)).findById(1L);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void createLesson_WhenChapterNotExists_ShouldThrowException() {
        when(chapterRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lessonService.createLesson(testLessonDTO));
        verify(chapterRepository, times(1)).findById(1L);
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void updateLesson_WhenLessonExists_ShouldUpdateLesson() {
        testLessonDTO.setTitle("Updated Lesson");
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);

        LessonDTO result = lessonService.updateLesson(1L, testLessonDTO);

        assertNotNull(result);
        verify(lessonRepository, times(1)).findById(1L);
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void updateLesson_WhenLessonNotExists_ShouldThrowException() {
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> lessonService.updateLesson(999L, testLessonDTO));
        verify(lessonRepository, times(1)).findById(999L);
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void deleteLesson_WhenLessonExists_ShouldDeleteLesson() {
        when(lessonRepository.existsById(1L)).thenReturn(true);
        doNothing().when(lessonRepository).deleteById(1L);

        lessonService.deleteLesson(1L);

        verify(lessonRepository, times(1)).existsById(1L);
        verify(lessonRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteLesson_WhenLessonNotExists_ShouldThrowException() {
        when(lessonRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> lessonService.deleteLesson(999L));
        verify(lessonRepository, times(1)).existsById(999L);
        verify(lessonRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_WhenLessonExists_ShouldReturnTrue() {
        when(lessonRepository.existsById(1L)).thenReturn(true);

        boolean result = lessonService.existsById(1L);

        assertTrue(result);
        verify(lessonRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenLessonNotExists_ShouldReturnFalse() {
        when(lessonRepository.existsById(anyLong())).thenReturn(false);

        boolean result = lessonService.existsById(999L);

        assertFalse(result);
        verify(lessonRepository, times(1)).existsById(999L);
    }

    @Test
    void belongsToChapter_WhenLessonBelongsToChapter_ShouldReturnTrue() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        boolean result = lessonService.belongsToChapter(1L, 1L);

        assertTrue(result);
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void belongsToChapter_WhenLessonNotBelongsToChapter_ShouldReturnFalse() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        boolean result = lessonService.belongsToChapter(1L, 999L);

        assertFalse(result);
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void belongsToCourse_WhenLessonBelongsToCourse_ShouldReturnTrue() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        boolean result = lessonService.belongsToCourse(1L, 1L);

        assertTrue(result);
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void belongsToCourse_WhenLessonNotBelongsToCourse_ShouldReturnFalse() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));

        boolean result = lessonService.belongsToCourse(1L, 999L);

        assertFalse(result);
        verify(lessonRepository, times(1)).findById(1L);
    }

    @Test
    void getPreviewLessons_ShouldReturnAllPreviewLessons() {
        testLesson.setIsPreview(true);
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByIsPreview(true)).thenReturn(lessons);

        List<LessonDTO> result = lessonService.getPreviewLessons();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lessonRepository, times(1)).findByIsPreview(true);
    }

    @Test
    void publishAllLessonsByCourse_ShouldPublishAllLessons() {
        testLesson.setIsPublished(false);
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByCourseId(1L)).thenReturn(lessons);
        when(lessonRepository.saveAll(anyList())).thenReturn(lessons);

        List<LessonDTO> result = lessonService.publishAllLessonsByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lessonRepository, times(1)).findByCourseId(1L);
        verify(lessonRepository, times(1)).saveAll(anyList());
    }

    @Test
    void unpublishAllLessonsByCourse_ShouldUnpublishAllLessons() {
        testLesson.setIsPublished(true);
        List<Lesson> lessons = Arrays.asList(testLesson);
        when(lessonRepository.findByCourseId(1L)).thenReturn(lessons);
        when(lessonRepository.saveAll(anyList())).thenReturn(lessons);

        List<LessonDTO> result = lessonService.unpublishAllLessonsByCourse(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(lessonRepository, times(1)).findByCourseId(1L);
        verify(lessonRepository, times(1)).saveAll(anyList());
    }
}
