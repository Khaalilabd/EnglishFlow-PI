package com.englishflow.courses.repository;

import com.englishflow.courses.entity.LessonMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonMediaRepository extends JpaRepository<LessonMedia, Long> {
    List<LessonMedia> findByLessonIdOrderByPositionAsc(Long lessonId);
    void deleteByLessonId(Long lessonId);
}
