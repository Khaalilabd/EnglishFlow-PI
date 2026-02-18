package com.englishflow.courses.service;

import com.englishflow.courses.dto.LessonProgressDTO;

import java.util.List;

public interface ILessonProgressService {
    
    /**
     * Start a lesson (create initial progress record)
     */
    LessonProgressDTO startLesson(Long studentId, Long lessonId);
    
    /**
     * Update lesson progress
     */
    LessonProgressDTO updateProgress(Long studentId, Long lessonId, Double progressPercentage, Integer timeSpentMinutes);
    
    /**
     * Mark lesson as completed
     */
    LessonProgressDTO completeLesson(Long studentId, Long lessonId);
    
    /**
     * Add notes to lesson
     */
    LessonProgressDTO addNotes(Long studentId, Long lessonId, String notes);
    
    /**
     * Get lesson progress for a student
     */
    LessonProgressDTO getLessonProgress(Long studentId, Long lessonId);
    
    /**
     * Get all lesson progress for a student
     */
    List<LessonProgressDTO> getStudentLessonProgress(Long studentId);
    
    /**
     * Get lesson progress for a student in a specific chapter
     */
    List<LessonProgressDTO> getStudentChapterLessonProgress(Long studentId, Long chapterId);
    
    /**
     * Get lesson progress for a student in a specific course
     */
    List<LessonProgressDTO> getStudentCourseLessonProgress(Long studentId, Long courseId);
    
    /**
     * Check if student has started a lesson
     */
    boolean hasStartedLesson(Long studentId, Long lessonId);
    
    /**
     * Count completed lessons for student in chapter
     */
    Long countCompletedLessonsInChapter(Long studentId, Long chapterId);
    
    /**
     * Count completed lessons for student in course
     */
    Long countCompletedLessonsInCourse(Long studentId, Long courseId);
}