package com.englishflow.courses.repository;

import com.englishflow.courses.entity.LessonTimeAssignment;
import com.englishflow.courses.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonTimeAssignmentRepository extends JpaRepository<LessonTimeAssignment, Long> {
    
    @Query("SELECT lta FROM LessonTimeAssignment lta JOIN FETCH lta.lesson WHERE lta.lesson.id = :lessonId")
    Optional<LessonTimeAssignment> findByLessonId(@Param("lessonId") Long lessonId);
    
    @Query("SELECT lta FROM LessonTimeAssignment lta JOIN FETCH lta.lesson WHERE lta.tutorId = :tutorId")
    List<LessonTimeAssignment> findByTutorId(@Param("tutorId") Long tutorId);
    
    @Query("SELECT lta FROM LessonTimeAssignment lta WHERE lta.tutorId = :tutorId " +
           "AND lta.dayOfWeek = :dayOfWeek AND lta.startTime = :startTime")
    Optional<LessonTimeAssignment> findByTutorIdAndDayAndTime(
        @Param("tutorId") Long tutorId,
        @Param("dayOfWeek") DayOfWeek dayOfWeek,
        @Param("startTime") LocalTime startTime
    );
    
    boolean existsByTutorIdAndDayOfWeekAndStartTime(Long tutorId, DayOfWeek dayOfWeek, LocalTime startTime);
    
    void deleteByLessonId(Long lessonId);
}
