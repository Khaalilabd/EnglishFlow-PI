package com.englishflow.courses.repository;

import com.englishflow.courses.entity.OnlineMeetingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineMeetingSessionRepository extends JpaRepository<OnlineMeetingSession, Long> {
    
    Optional<OnlineMeetingSession> findByLessonIdAndIsActiveTrue(Long lessonId);
    
    Optional<OnlineMeetingSession> findByRoomIdAndIsActiveTrue(String roomId);
    
    void deleteByLessonId(Long lessonId);
}
