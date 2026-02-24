package com.englishflow.courses.repository;

import com.englishflow.courses.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    
    /**
     * Find lesson progress by student and lesson
     */
    Optional<LessonProgress> findByStudentIdAndLessonId(Long studentId, Long lessonId);
    
    /**
     * Find all lesson progress for a student
     */
    List<LessonProgress> findByStudentId(Long studentId);
    
    /**
     * Find all lesson progress for a specific lesson
     */
    List<LessonProgress> findByLessonId(Long lessonId);
    
    /**
     * Find completed lessons for a student
     */
    List<LessonProgress> findByStudentIdAndIsCompleted(Long studentId, Boolean isCompleted);
    
    /**
     * Count completed lessons for a student in a specific chapter
     */
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.studentId = :studentId AND lp.lesson.chapter.id = :chapterId AND lp.isCompleted = true")
    Long countCompletedLessonsByStudentAndChapter(@Param("studentId") Long studentId, @Param("chapterId") Long chapterId);
    
    /**
     * Count completed lessons for a student in a specific course
     */
    @Query("SELECT COUNT(lp) FROM LessonProgress lp WHERE lp.studentId = :studentId AND lp.lesson.chapter.course.id = :courseId AND lp.isCompleted = true")
    Long countCompletedLessonsByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * Find lesson progress for a student in a specific chapter
     */
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.studentId = :studentId AND lp.lesson.chapter.id = :chapterId")
    List<LessonProgress> findByStudentIdAndChapterId(@Param("studentId") Long studentId, @Param("chapterId") Long chapterId);
    
    /**
     * Find lesson progress for a student in a specific course
     */
    @Query("SELECT lp FROM LessonProgress lp WHERE lp.studentId = :studentId AND lp.lesson.chapter.course.id = :courseId")
    List<LessonProgress> findByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    
    /**
     * Check if student has started a lesson
     */
    boolean existsByStudentIdAndLessonId(Long studentId, Long lessonId);
    
    /**
     * Get average progress percentage for a student in a course
     */
    @Query("SELECT AVG(lp.progressPercentage) FROM LessonProgress lp WHERE lp.studentId = :studentId AND lp.lesson.chapter.course.id = :courseId")
    Double getAverageProgressByStudentAndCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}