package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Lesson;
import com.englishflow.courses.enums.LessonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByChapterIdOrderByOrderIndexAsc(Long chapterId);
    List<Lesson> findByChapterIdAndIsPublished(Long chapterId, Boolean isPublished);
    List<Lesson> findByLessonType(LessonType lessonType);
    List<Lesson> findByIsPreview(Boolean isPreview);
    
    @Query("SELECT l FROM Lesson l JOIN l.chapter c WHERE c.course.id = :courseId ORDER BY c.orderIndex, l.orderIndex")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT l FROM Lesson l JOIN l.chapter c WHERE c.course.id = :courseId AND l.isPreview = :isPreview ORDER BY c.orderIndex, l.orderIndex")
    List<Lesson> findByCourseIdAndIsPreview(@Param("courseId") Long courseId, @Param("isPreview") Boolean isPreview);
    
    Long countByChapterId(Long chapterId);
    
    @Query("SELECT COUNT(l) FROM Lesson l JOIN l.chapter c WHERE c.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(l) FROM Lesson l JOIN l.chapter c WHERE c.course.id = :courseId AND l.isPublished = true")
    Long countPublishedByCourseId(@Param("courseId") Long courseId);
}
