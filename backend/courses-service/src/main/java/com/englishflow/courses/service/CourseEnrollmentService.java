package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseEnrollmentDTO;
import com.englishflow.courses.entity.Course;
import com.englishflow.courses.entity.CourseEnrollment;
import com.englishflow.courses.repository.CourseEnrollmentRepository;
import com.englishflow.courses.repository.CourseRepository;
import com.englishflow.courses.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService implements ICourseEnrollmentService {
    
    private final CourseEnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressService lessonProgressService;
    private final UserValidationService userValidationService;
    
    @Override
    @Transactional
    public CourseEnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        // Validate student exists and has STUDENT role
        userValidationService.validateStudentExists(studentId);
        
        // Check if course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        
        // Check if already enrolled
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(studentId, courseId, true)) {
            throw new RuntimeException("Student is already enrolled in this course");
        }
        
        // Check course capacity
        Long currentEnrollments = enrollmentRepository.countActiveByCourseId(courseId);
        if (course.getMaxStudents() != null && currentEnrollments >= course.getMaxStudents()) {
            throw new RuntimeException("Course is full. Maximum capacity reached.");
        }
        
        // Create enrollment
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourse(course);
        enrollment.setIsActive(true);
        enrollment.setProgress(0.0);
        enrollment.setCompletedLessons(0);
        
        // Calculate total PUBLISHED lessons for this course
        Long totalLessons = lessonRepository.countPublishedByCourseId(courseId);
        enrollment.setTotalLessons(totalLessons.intValue());
        
        CourseEnrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(savedEnrollment);
    }
    
    @Override
    @Transactional
    public void unenrollStudent(Long studentId, Long courseId) {
        CourseEnrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setIsActive(false);
        enrollmentRepository.save(enrollment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseEnrollmentDTO> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndIsActive(studentId, true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseEnrollmentDTO> getCourseEnrollments(Long courseId) {
        return enrollmentRepository.findActiveByCourseIdOrderByEnrolledAt(courseId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseIdAndIsActive(studentId, courseId, true);
    }
    
    @Override
    @Transactional
    public CourseEnrollmentDTO updateProgress(Long studentId, Long courseId, Double progress, Integer completedLessons) {
        CourseEnrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        enrollment.setProgress(progress);
        enrollment.setCompletedLessons(completedLessons);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        
        // Mark as completed if progress is 100%
        if (progress >= 100.0 && enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        
        CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(updatedEnrollment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseEnrollmentDTO getEnrollment(Long studentId, Long courseId) {
        CourseEnrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return mapToDTO(enrollment);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getCourseEnrollmentCount(Long courseId) {
        return enrollmentRepository.countActiveByCourseId(courseId);
    }
    
    /**
     * Calculate and update course progress based on lesson completions
     */
    @Transactional
    public CourseEnrollmentDTO calculateAndUpdateProgress(Long studentId, Long courseId) {
        CourseEnrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        // Count completed lessons for this student in this course
        Long completedLessons = lessonProgressService.countCompletedLessonsInCourse(studentId, courseId);
        enrollment.setCompletedLessons(completedLessons.intValue());
        
        // Calculate progress percentage
        if (enrollment.getTotalLessons() > 0) {
            double progress = (completedLessons.doubleValue() / enrollment.getTotalLessons()) * 100.0;
            enrollment.setProgress(progress);
            
            // Mark as completed if progress is 100%
            if (progress >= 100.0 && enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(LocalDateTime.now());
            }
        }
        
        enrollment.setLastAccessedAt(LocalDateTime.now());
        CourseEnrollment updatedEnrollment = enrollmentRepository.save(enrollment);
        return mapToDTO(updatedEnrollment);
    }
    
    private CourseEnrollmentDTO mapToDTO(CourseEnrollment enrollment) {
        CourseEnrollmentDTO dto = new CourseEnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseTitle(enrollment.getCourse().getTitle());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setCompletedAt(enrollment.getCompletedAt());
        dto.setIsActive(enrollment.getIsActive());
        dto.setProgress(enrollment.getProgress());
        dto.setCompletedLessons(enrollment.getCompletedLessons());
        dto.setTotalLessons(enrollment.getTotalLessons());
        dto.setLastAccessedAt(enrollment.getLastAccessedAt());
        return dto;
    }
}