package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.repository.CourseRepository;
import com.englishflow.courses.repository.ChapterRepository;
import com.englishflow.courses.repository.LessonRepository;
import com.englishflow.courses.repository.CourseEnrollmentRepository;
import com.englishflow.courses.repository.LessonProgressRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private CourseDTO testCourseDTO;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setCategory("English");
        testCourse.setLevel("Beginner");
        testCourse.setMaxStudents(30);
        testCourse.setSchedule(LocalDateTime.now());
        testCourse.setDuration(60);
        testCourse.setTutorId(1L);
        testCourse.setPrice(BigDecimal.valueOf(100.0));
        testCourse.setStatus(CourseStatus.PUBLISHED);
        testCourse.setIsFeatured(false);
        testCourse.setCreatedAt(LocalDateTime.now());
        testCourse.setUpdatedAt(LocalDateTime.now());

        testCourseDTO = new CourseDTO();
        testCourseDTO.setId(1L);
        testCourseDTO.setTitle("Test Course");
        testCourseDTO.setDescription("Test Description");
        testCourseDTO.setCategory("English");
        testCourseDTO.setLevel("Beginner");
        testCourseDTO.setMaxStudents(30);
        testCourseDTO.setSchedule(LocalDateTime.now());
        testCourseDTO.setDuration(60);
        testCourseDTO.setTutorId(1L);
        testCourseDTO.setPrice(BigDecimal.valueOf(100.0));
        testCourseDTO.setStatus(CourseStatus.PUBLISHED);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        List<CourseDTO> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Course", result.get(0).getTitle());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getAllCoursesPaginated_ShouldReturnPagedCourses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> coursePage = new PageImpl<>(Arrays.asList(testCourse));
        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        Page<CourseDTO> result = courseService.getAllCoursesPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Course", result.getContent().get(0).getTitle());
        verify(courseRepository, times(1)).findAll(pageable);
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        CourseDTO result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getTitle());
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    void getCourseById_WhenCourseNotExists_ShouldThrowException() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.getCourseById(999L));
        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    void getPublishedCourses_ShouldReturnOnlyPublishedCourses() {
        List<Course> publishedCourses = Arrays.asList(testCourse);
        when(courseRepository.findByStatus(CourseStatus.PUBLISHED)).thenReturn(publishedCourses);

        List<CourseDTO> result = courseService.getPublishedCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CourseStatus.PUBLISHED, result.get(0).getStatus());
        verify(courseRepository, times(1)).findByStatus(CourseStatus.PUBLISHED);
    }

    @Test
    void getCoursesByLevel_ShouldReturnCoursesOfSpecificLevel() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByLevel("Beginner")).thenReturn(courses);

        List<CourseDTO> result = courseService.getCoursesByLevel("Beginner");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Beginner", result.get(0).getLevel());
        verify(courseRepository, times(1)).findByLevel("Beginner");
    }

    @Test
    void getCoursesByStatus_ShouldReturnCoursesWithStatus() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByStatus(CourseStatus.PUBLISHED)).thenReturn(courses);

        List<CourseDTO> result = courseService.getCoursesByStatus(CourseStatus.PUBLISHED);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findByStatus(CourseStatus.PUBLISHED);
    }

    @Test
    void createCourse_WithValidData_ShouldCreateCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
        doNothing().when(userValidationService).validateTutorExists(anyLong());

        CourseDTO result = courseService.createCourse(testCourseDTO);

        assertNotNull(result);
        assertEquals("Test Course", result.getTitle());
        verify(userValidationService, times(1)).validateTutorExists(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_WithoutTutorId_ShouldCreateCourse() {
        testCourseDTO.setTutorId(null);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        CourseDTO result = courseService.createCourse(testCourseDTO);

        assertNotNull(result);
        verify(userValidationService, never()).validateTutorExists(anyLong());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_WhenCourseExists_ShouldUpdateCourse() {
        testCourseDTO.setTitle("Updated Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        CourseDTO result = courseService.updateCourse(1L, testCourseDTO);

        assertNotNull(result);
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void updateCourse_WhenCourseNotExists_ShouldThrowException() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> courseService.updateCourse(999L, testCourseDTO));
        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void updateCourse_WithDifferentTutorId_ShouldValidateNewTutor() {
        testCourse.setTutorId(2L);
        testCourseDTO.setTutorId(3L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
        doNothing().when(userValidationService).validateTutorExists(3L);

        courseService.updateCourse(1L, testCourseDTO);

        verify(userValidationService, times(1)).validateTutorExists(3L);
    }

    @Test
    void deleteCourse_WhenCourseExists_ShouldDeleteCourse() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(chapterRepository.findByCourseIdOrderByOrderIndexAsc(1L)).thenReturn(Collections.emptyList());
        doNothing().when(lessonProgressRepository).deleteByCourseId(1L);
        doNothing().when(courseEnrollmentRepository).deleteByCourseId(1L);
        doNothing().when(courseRepository).deleteById(1L);

        courseService.deleteCourse(1L);

        verify(courseRepository, times(1)).existsById(1L);
        verify(lessonProgressRepository, times(1)).deleteByCourseId(1L);
        verify(courseEnrollmentRepository, times(1)).deleteByCourseId(1L);
        verify(chapterRepository, times(1)).findByCourseIdOrderByOrderIndexAsc(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCourse_WhenCourseNotExists_ShouldThrowException() {
        when(courseRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(999L));
        verify(courseRepository, times(1)).existsById(999L);
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void existsById_WhenCourseExists_ShouldReturnTrue() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        boolean result = courseService.existsById(1L);

        assertTrue(result);
        verify(courseRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WhenCourseNotExists_ShouldReturnFalse() {
        when(courseRepository.existsById(anyLong())).thenReturn(false);

        boolean result = courseService.existsById(999L);

        assertFalse(result);
        verify(courseRepository, times(1)).existsById(999L);
    }

    @Test
    void getCoursesByTutor_ShouldReturnTutorCourses() {
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByTutorId(1L)).thenReturn(courses);

        List<CourseDTO> result = courseService.getCoursesByTutor(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTutorId());
        verify(courseRepository, times(1)).findByTutorId(1L);
    }
}
