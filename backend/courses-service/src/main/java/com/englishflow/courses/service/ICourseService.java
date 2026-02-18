package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseDTO;
import com.englishflow.courses.enums.CourseStatus;
import com.englishflow.courses.enums.EnglishLevel;

import java.util.List;

public interface ICourseService {
    
    /**
     * Get all courses
     */
    List<CourseDTO> getAllCourses();
    
    /**
     * Get course by ID
     */
    CourseDTO getCourseById(Long id);
    
    /**
     * Get published courses only
     */
    List<CourseDTO> getPublishedCourses();
    
    /**
     * Get courses by level
     */
    List<CourseDTO> getCoursesByLevel(EnglishLevel level);
    
    /**
     * Get courses by status
     */
    List<CourseDTO> getCoursesByStatus(CourseStatus status);
    
    /**
     * Create a new course
     */
    CourseDTO createCourse(CourseDTO courseDTO);
    
    /**
     * Update an existing course
     */
    CourseDTO updateCourse(Long id, CourseDTO courseDTO);
    
    /**
     * Delete a course
     */
    void deleteCourse(Long id);
    
    /**
     * Check if course exists
     */
    boolean existsById(Long id);
    
    /**
     * Get courses by tutor ID
     */
    List<CourseDTO> getCoursesByTutor(Long tutorId);
}