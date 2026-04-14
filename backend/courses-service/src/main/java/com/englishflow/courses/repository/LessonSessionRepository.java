package com.englishflow.courses.repository;

import com.englishflow.courses.entity.LessonSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LessonSessionRepository extends JpaRepository<LessonSession, Long> {
    List<LessonSession> findByOnlineLessonId(Long onlineLessonId);
    
    @Query("SELECT ls FROM LessonSession ls WHERE ls.onlineLesson.id = :onlineLessonId AND ls.sessionDate >= :date ORDER BY ls.sessionDate, ls.sessionTime")
    List<LessonSession> findUpcomingSessionsForLesson(Long onlineLessonId, LocalDate date);
    
    @Query("SELECT ls FROM LessonSession ls WHERE ls.sessionDate = :date AND ls.status = 'scheduled'")
    List<LessonSession> findScheduledSessionsForDate(LocalDate date);
}
