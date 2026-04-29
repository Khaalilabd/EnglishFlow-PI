package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseEnrollmentDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.CourseEnrollment;
import com.englishflow.courses.repository.CourseEnrollmentRepository;
import com.englishflow.courses.repository.CourseRepository;
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
class CourseEnrollmentServiceTest {

    @Mock
    private CourseEnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonProgressService lessonProgressService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private CourseEnrollmentService enrollmentService;

    private Course course;
    private CourseEnrollment enrollment;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        course.setMaxStudents(30);

        enrollment = new CourseEnrollment();
        enrollment.setId(1L);
        enrollment.setStudentId(1L);
        enrollment.setCourse(course);
        enrollment.setIsActive(true);
        enrollment.setTotalLessons(10);
        enrollment.setEnrolledAt(LocalDateTime.now());
    }

    @Test
    void enrollStudent_ShouldCreateEnrollment() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(1L, 1L, true)).thenReturn(false);
        when(enrollmentRepository.countActiveByCourseId(1L)).thenReturn(10L);
        when(lessonRepository.countPublishedByCourseId(1L)).thenReturn(10L);
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(0L);
        doNothing().when(userValidationService).validateStudentExists(1L);

        CourseEnrollmentDTO result = enrollmentService.enrollStudent(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals(1L, result.getCourseId());
        verify(enrollmentRepository, times(1)).save(any(CourseEnrollment.class));
    }

    @Test
    void enrollStudent_WhenAlreadyEnrolled_ShouldThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(1L, 1L, true)).thenReturn(true);
        doNothing().when(userValidationService).validateStudentExists(1L);

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(1L, 1L));
    }

    @Test
    void enrollStudent_WhenCourseFull_ShouldThrowException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(1L, 1L, true)).thenReturn(false);
        when(enrollmentRepository.countActiveByCourseId(1L)).thenReturn(30L);
        doNothing().when(userValidationService).validateStudentExists(1L);

        assertThrows(RuntimeException.class, () -> enrollmentService.enrollStudent(1L, 1L));
    }

    @Test
    void unenrollStudent_ShouldDeactivateEnrollment() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);
        doNothing().when(lessonProgressService).deleteProgressByStudentAndCourse(1L, 1L);

        enrollmentService.unenrollStudent(1L, 1L);

        assertFalse(enrollment.getIsActive());
        verify(enrollmentRepository, times(1)).save(enrollment);
        verify(lessonProgressService, times(1)).deleteProgressByStudentAndCourse(1L, 1L);
    }

    @Test
    void getStudentEnrollments_ShouldReturnEnrollments() {
        when(enrollmentRepository.findByStudentIdAndIsActive(1L, true)).thenReturn(Arrays.asList(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        List<CourseEnrollmentDTO> result = enrollmentService.getStudentEnrollments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStudentId());
        verify(enrollmentRepository, times(1)).findByStudentIdAndIsActive(1L, true);
    }

    @Test
    void getCourseEnrollments_ShouldReturnEnrollments() {
        when(enrollmentRepository.findActiveByCourseIdOrderByEnrolledAt(1L)).thenReturn(Arrays.asList(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        List<CourseEnrollmentDTO> result = enrollmentService.getCourseEnrollments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCourseId());
        verify(enrollmentRepository, times(1)).findActiveByCourseIdOrderByEnrolledAt(1L);
    }

    @Test
    void isStudentEnrolled_ShouldReturnTrue() {
        when(enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(1L, 1L, true)).thenReturn(true);

        boolean result = enrollmentService.isStudentEnrolled(1L, 1L);

        assertTrue(result);
        verify(enrollmentRepository, times(1)).existsByStudentIdAndCourseIdAndIsActive(1L, 1L, true);
    }

    @Test
    void getEnrollment_ShouldReturnEnrollment() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        CourseEnrollmentDTO result = enrollmentService.getEnrollment(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getStudentId());
        assertEquals(1L, result.getCourseId());
        verify(enrollmentRepository, times(1)).findByStudentIdAndCourseId(1L, 1L);
    }

    @Test
    void getCourseEnrollmentCount_ShouldReturnCount() {
        when(enrollmentRepository.countActiveByCourseId(1L)).thenReturn(15L);

        Long result = enrollmentService.getCourseEnrollmentCount(1L);

        assertEquals(15L, result);
        verify(enrollmentRepository, times(1)).countActiveByCourseId(1L);
    }

    @Test
    void calculateCourseProgress_ShouldReturnProgress() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        double result = enrollmentService.calculateCourseProgress(1L, 1L);

        assertEquals(50.0, result);
    }

    @Test
    void getCompletedLessonsCount_ShouldReturnCount() {
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        int result = enrollmentService.getCompletedLessonsCount(1L, 1L);

        assertEquals(5, result);
    }

    @Test
    void isCourseCompleted_WhenAllLessonsCompleted_ShouldReturnTrue() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(10L);

        boolean result = enrollmentService.isCourseCompleted(1L, 1L);

        assertTrue(result);
    }

    @Test
    void isCourseCompleted_WhenNotAllLessonsCompleted_ShouldReturnFalse() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        boolean result = enrollmentService.isCourseCompleted(1L, 1L);

        assertFalse(result);
    }

    @Test
    void calculateAndUpdateProgress_ShouldUpdateEnrollment() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);
        when(lessonProgressService.countCompletedLessonsInCourse(1L, 1L)).thenReturn(5L);

        CourseEnrollmentDTO result = enrollmentService.calculateAndUpdateProgress(1L, 1L);

        assertNotNull(result);
        assertNotNull(enrollment.getLastAccessedAt());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }
}
