package com.englishflow.courses.repository;

import com.englishflow.courses.entity.SessionAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAttendanceRepository extends JpaRepository<SessionAttendance, Long> {
    Optional<SessionAttendance> findBySessionIdAndStudentId(Long sessionId, Long studentId);
    
    List<SessionAttendance> findBySessionId(Long sessionId);
    
    List<SessionAttendance> findByStudentId(Long studentId);
}
