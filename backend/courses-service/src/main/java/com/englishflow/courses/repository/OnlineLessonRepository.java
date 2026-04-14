package com.englishflow.courses.repository;

import com.englishflow.courses.entity.OnlineLesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnlineLessonRepository extends JpaRepository<OnlineLesson, Long> {
    Optional<OnlineLesson> findByLessonId(Long lessonId);
    
    @Query("SELECT ol FROM OnlineLesson ol WHERE ol.startDate <= :date AND (ol.endDate IS NULL OR ol.endDate >= :date)")
    List<OnlineLesson> findActiveLessonsOnDate(LocalDate date);
}
