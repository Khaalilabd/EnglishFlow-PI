package com.englishflow.courses.repository;

import com.englishflow.courses.entity.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, Long> {
    
    /**
     * Find chapter progress by student and chapter
     */
    Optional<ChapterProgress> findByStudentIdAndChapterId(Long studentId, Long chapterId);
    
    /**
     * Find all chapter progress for a student
     */
    List<ChapterProgress> findByStudentId(Long studentId);
    
    /**
     * Find all chapter progress for a specific chapter
     */
    List<ChapterProgress> findByChapterId(Long chapterId);
    
    /**
     * Find completed chapters for a student
     */
    List<ChapterProgress> findByStudentIdAndIsCompleted(Long studentId, Boolean isCompleted);
    
    /**
     * Find chapter progress for a student in a specific course
     */
    @Query("SELECT cp FROM ChapterProgress cp WHERE cp.studentId = :studentId AND cp.chapter.course.id = :courseId")
    List<ChapterProgress> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * Count completed chapters for a student in a specific course
     */
    @Query("SELECT COUNT(cp) FROM ChapterProgress cp WHERE cp.studentId = :studentId AND cp.chapter.course.id = :courseId AND cp.isCompleted = true")
    Long countCompletedChaptersByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * Check if student has started a chapter
     */
    boolean existsByStudentIdAndChapterId(Long studentId, Long chapterId);
    
    /**
     * Get average progress percentage for a student in a course
     */
    @Query("SELECT AVG(cp.progressPercentage) FROM ChapterProgress cp WHERE cp.studentId = :studentId AND cp.chapter.course.id = :courseId")
    Double getAverageProgressByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}