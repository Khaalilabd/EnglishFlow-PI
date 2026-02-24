package com.englishflow.courses.repository;

import com.englishflow.courses.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    
    /**
     * Find all enrollments for a specific student
     */
    List<CourseEnrollment> findByStudentIdAndIsActive(Long studentId, Boolean isActive);
    
    /**
     * Find all enrollments for a specific course
     */
    List<CourseEnrollment> findByCourseIdAndIsActive(Long courseId, Boolean isActive);
    
    /**
     * Find specific enrollment by student and course
     */
    Optional<CourseEnrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    /**
     * Check if student is enrolled in course
     */
    boolean existsByStudentIdAndCourseIdAndIsActive(Long studentId, Long courseId, Boolean isActive);
    
    /**
     * Count active enrollments for a course
     */
    @Query("SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course.id = :courseId AND e.isActive = true")
    Long countActiveByCourseId(@Param("courseId") Long courseId);
    
    /**
     * Count active enrollments for a student
     */
    Long countByStudentIdAndIsActive(Long studentId, Boolean isActive);
    
    /**
     * Find enrollments by course with progress info
     */
    @Query("SELECT e FROM CourseEnrollment e WHERE e.course.id = :courseId AND e.isActive = true ORDER BY e.enrolledAt DESC")
    List<CourseEnrollment> findActiveByCourseIdOrderByEnrolledAt(@Param("courseId") Long courseId);
}