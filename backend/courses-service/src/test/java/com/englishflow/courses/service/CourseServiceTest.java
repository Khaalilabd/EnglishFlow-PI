package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.repository.CourseRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDTO courseDTO;
    private Long courseId = 1L;
    private Long tutorId = 100L;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(courseId);
        course.setTitle("English for Beginners");
        course.setDescription("Learn English from scratch");
        course.setCategory("LANGUAGE");
        course.setLevel("A1");
        course.setMaxStudents(30);
        course.setSchedule("Mon, Wed, Fri 10:00-12:00");
        course.setDuration(60);
        course.setTutorId(tutorId);
        course.setPrice(99.99);
        course.setStatus(CourseStatus.PUBLISHED);
        course.setIsFeatured(true);
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        courseDTO = new CourseDTO();
        courseDTO.setId(courseId);
        courseDTO.setTitle("English for Beginners");
        courseDTO.setDescription("Learn English from scratch");
        courseDTO.setCategory("LANGUAGE");
        courseDTO.setLevel("A1");
        courseDTO.setMaxStudents(30);
        courseDTO.setSchedule("Mon, Wed, Fri 10:00-12:00");
        courseDTO.setDuration(60);
        courseDTO.setTutorId(tutorId);
        courseDTO.setPrice(99.99);
        courseDTO.setStatus(CourseStatus.PUBLISHED);
        courseDTO.setIsFeatured(true);
    }

    @Test
    void getAllCourses_ShouldReturnAllCourses() {
        // Given
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        // When
        List<CourseDTO> result = courseService.getAllCourses();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English for Beginners", result.get(0).getTitle());
        verify(courseRepository).findAll();
    }

    @Test
    void getAllCoursesPaginated_ShouldReturnPagedCourses() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Course> coursePage = new PageImpl<>(Arrays.asList(course));
        when(courseRepository.findAll(pageable)).thenReturn(coursePage);

        // When
        Page<CourseDTO> result = courseService.getAllCoursesPaginated(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("English for Beginners", result.getContent().get(0).getTitle());
    }

    @Test
    void getCourseById_WhenExists_ShouldReturnCourse() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // When
        CourseDTO result = courseService.getCourseById(courseId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getId());
        assertEquals("English for Beginners", result.getTitle());
        verify(courseRepository).findById(courseId);
    }

    @Test
    void getCourseById_WhenNotExists_ShouldThrowException() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    void getPublishedCourses_ShouldReturnOnlyPublished() {
        // Given
        when(courseRepository.findByStatus(CourseStatus.PUBLISHED))
            .thenReturn(Arrays.asList(course));

        // When
        List<CourseDTO> result = courseService.getPublishedCourses();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CourseStatus.PUBLISHED, result.get(0).getStatus());
    }

    @Test
    void getCoursesByLevel_ShouldReturnFilteredCourses() {
        // Given
        when(courseRepository.findByLevel("A1")).thenReturn(Arrays.asList(course));

        // When
        List<CourseDTO> result = courseService.getCoursesByLevel("A1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A1", result.get(0).getLevel());
    }

    @Test
    void getCoursesByStatus_ShouldReturnFilteredCourses() {
        // Given
        when(courseRepository.findByStatus(CourseStatus.PUBLISHED))
            .thenReturn(Arrays.asList(course));

        // When
        List<CourseDTO> result = courseService.getCoursesByStatus(CourseStatus.PUBLISHED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(CourseStatus.PUBLISHED, result.get(0).getStatus());
    }

    @Test
    void createCourse_WithValidTutor_ShouldCreateCourse() {
        // Given
        doNothing().when(userValidationService).validateTutorExists(tutorId);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // When
        CourseDTO result = courseService.createCourse(courseDTO);

        // Then
        assertNotNull(result);
        assertEquals("English for Beginners", result.getTitle());
        verify(userValidationService).validateTutorExists(tutorId);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_WithInvalidTutor_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("Tutor not found"))
            .when(userValidationService).validateTutorExists(tutorId);

        // When & Then
        assertThrows(RuntimeException.class, () -> courseService.createCourse(courseDTO));
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void updateCourse_WhenExists_ShouldUpdateCourse() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseDTO updateDTO = new CourseDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setCategory("LANGUAGE");
        updateDTO.setLevel("A2");
        updateDTO.setMaxStudents(25);
        updateDTO.setSchedule("Tue, Thu 14:00-16:00");
        updateDTO.setDuration(90);
        updateDTO.setTutorId(tutorId);
        updateDTO.setPrice(149.99);
        updateDTO.setStatus(CourseStatus.PUBLISHED);
        updateDTO.setIsFeatured(false);

        // When
        CourseDTO result = courseService.updateCourse(courseId, updateDTO);

        // Then
        assertNotNull(result);
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_WhenNotExists_ShouldThrowException() {
        // Given
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> courseService.updateCourse(courseId, courseDTO));
    }

    @Test
    void deleteCourse_WhenExists_ShouldDelete() {
        // Given
        when(courseRepository.existsById(courseId)).thenReturn(true);

        // When
        courseService.deleteCourse(courseId);

        // Then
        verify(courseRepository).deleteById(courseId);
    }

    @Test
    void deleteCourse_WhenNotExists_ShouldThrowException() {
        // Given
        when(courseRepository.existsById(courseId)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> courseService.deleteCourse(courseId));
        verify(courseRepository, never()).deleteById(courseId);
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Given
        when(courseRepository.existsById(courseId)).thenReturn(true);

        // When
        boolean result = courseService.existsById(courseId);

        // Then
        assertTrue(result);
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Given
        when(courseRepository.existsById(courseId)).thenReturn(false);

        // When
        boolean result = courseService.existsById(courseId);

        // Then
        assertFalse(result);
    }

    @Test
    void getCoursesByTutor_ShouldReturnTutorCourses() {
        // Given
        when(courseRepository.findByTutorId(tutorId)).thenReturn(Arrays.asList(course));

        // When
        List<CourseDTO> result = courseService.getCoursesByTutor(tutorId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tutorId, result.get(0).getTutorId());
    }
}
