package com.englishflow.courses.repository;

import com.englishflow.courses.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    List<Chapter> findByCourseIdAndIsPublished(Long courseId, Boolean isPublished);
}
