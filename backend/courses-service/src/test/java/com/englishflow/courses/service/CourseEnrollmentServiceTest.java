package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseEnrollmentDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.CourseEnrollment;
import com.englishflow.courses.repository.CourseEnrollmentRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseEnrollmentServiceTest {

    @Mock
    private CourseEnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private CourseEnrollmentService enrollmentService;

    private Course course;
    private CourseEnrollment enrollment;
    private Long courseId = 1L;
    private Long studentId = 100L;
    private Long enrollmentId = 1L;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(courseId);
        course.setTitle("English for Beginners");
        course.setMaxStudents(30);

        enrollment = new CourseEnrollment();
        enrollment.setId(enrollmentId);
        enrollment.setCourse(course);
        enrollment.setStudentId(studentId);
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setProgress(0.0);
        enrollment.setCompleted(false);
    }

    @Test
    void enrollStudent_WhenValid_ShouldCreateEnrollment() {
        // Given
        doNothing().when(userValidationService).validateStudentExists(studentId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)).thenReturn(false);
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // When
        CourseEnrollmentDTO result = enrollmentService.enrollStudent(courseId, studentId);

        // Then
        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());
        assertEquals(studentId, result.getStudentId());
        verify(userValidationService).validateStudentExists(studentId);
        verify(enrollmentRepository).save(any(CourseEnrollment.class));
    }

    @Test
    void enrollStudent_WhenAlreadyEnrolled_ShouldThrowException() {
        // Given
        doNothing().when(userValidationService).validateStudentExists(studentId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId)).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> enrollmentService.enrollStudent(courseId, studentId));
        verify(enrollmentRepository, never()).save(any(CourseEnrollment.class));
    }

    @Test
    void enrollStudent_WhenCourseNotFound_ShouldThrowException() {
        // Given
        doNothing().when(userValidationService).validateStudentExists(studentId);
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> enrollmentService.enrollStudent(courseId, studentId));
    }

    @Test
    void unenrollStudent_WhenEnrolled_ShouldRemoveEnrollment() {
        // Given
        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(Optional.of(enrollment));

        // When
        enrollmentService.unenrollStudent(courseId, studentId);

        // Then
        verify(enrollmentRepository).delete(enrollment);
    }

    @Test
    void unenrollStudent_WhenNotEnrolled_ShouldThrowException() {
        // Given
        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> enrollmentService.unenrollStudent(courseId, studentId));
    }

    @Test
    void getEnrollmentsByCourse_ShouldReturnEnrollments() {
        // Given
        when(enrollmentRepository.findByCourseId(courseId))
            .thenReturn(Arrays.asList(enrollment));

        // When
        List<CourseEnrollmentDTO> result = enrollmentService.getEnrollmentsByCourse(courseId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseId, result.get(0).getCourseId());
    }

    @Test
    void getEnrollmentsByStudent_ShouldReturnEnrollments() {
        // Given
        when(enrollmentRepository.findByStudentId(studentId))
            .thenReturn(Arrays.asList(enrollment));

        // When
        List<CourseEnrollmentDTO> result = enrollmentService.getEnrollmentsByStudent(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(studentId, result.get(0).getStudentId());
    }

    @Test
    void isStudentEnrolled_WhenEnrolled_ShouldReturnTrue() {
        // Given
        when(enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(true);

        // When
        boolean result = enrollmentService.isStudentEnrolled(courseId, studentId);

        // Then
        assertTrue(result);
    }

    @Test
    void isStudentEnrolled_WhenNotEnrolled_ShouldReturnFalse() {
        // Given
        when(enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(false);

        // When
        boolean result = enrollmentService.isStudentEnrolled(courseId, studentId);

        // Then
        assertFalse(result);
    }

    @Test
    void getEnrolledStudentCount_ShouldReturnCount() {
        // Given
        when(enrollmentRepository.countByCourseId(courseId)).thenReturn(15L);

        // When
        Long result = enrollmentService.getEnrolledStudentCount(courseId);

        // Then
        assertEquals(15L, result);
    }

    @Test
    void updateProgress_WhenEnrolled_ShouldUpdateProgress() {
        // Given
        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // When
        CourseEnrollmentDTO result = enrollmentService.updateProgress(courseId, studentId, 75.0);

        // Then
        assertNotNull(result);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void markAsCompleted_WhenEnrolled_ShouldMarkCompleted() {
        // Given
        when(enrollmentRepository.findByCourseIdAndStudentId(courseId, studentId))
            .thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // When
        CourseEnrollmentDTO result = enrollmentService.markAsCompleted(courseId, studentId);

        // Then
        assertNotNull(result);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void getCompletedCoursesByStudent_ShouldReturnCompletedCourses() {
        // Given
        enrollment.setCompleted(true);
        when(enrollmentRepository.findByStudentIdAndCompleted(studentId, true))
            .thenReturn(Arrays.asList(enrollment));

        // When
        List<CourseEnrollmentDTO> result = enrollmentService.getCompletedCoursesByStudent(studentId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }
}
