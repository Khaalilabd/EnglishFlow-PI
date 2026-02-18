package com.englishflow.courses.service;

import com.englishflow.courses.dto.ChapterProgressDTO;

import java.util.List;

public interface IChapterProgressService {
    
    /**
     * Start a chapter (create initial progress record)
     */
    ChapterProgressDTO startChapter(Long studentId, Long chapterId);
    
    /**
     * Update chapter progress based on lesson completions
     */
    ChapterProgressDTO updateChapterProgress(Long studentId, Long chapterId);
    
    /**
     * Get chapter progress for a student
     */
    ChapterProgressDTO getChapterProgress(Long studentId, Long chapterId);
    
    /**
     * Get all chapter progress for a student
     */
    List<ChapterProgressDTO> getStudentChapterProgress(Long studentId);
    
    /**
     * Get chapter progress for a student in a specific course
     */
    List<ChapterProgressDTO> getStudentCourseChapterProgress(Long studentId, Long courseId);
    
    /**
     * Check if student has started a chapter
     */
    boolean hasStartedChapter(Long studentId, Long chapterId);
    
    /**
     * Count completed chapters for student in course
     */
    Long countCompletedChaptersInCourse(Long studentId, Long courseId);
    
    /**
     * Calculate and update progress for all chapters in a course for a student
     */
    void updateCourseChapterProgress(Long studentId, Long courseId);
}