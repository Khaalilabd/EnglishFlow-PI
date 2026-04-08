package com.englishflow.event.repository;

import com.englishflow.event.entity.LiveQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LiveQuestionRepository extends JpaRepository<LiveQuestion, Long> {

    @Query("SELECT DISTINCT q FROM LiveQuestion q LEFT JOIN FETCH q.upvoterIds WHERE q.eventId = :eventId ORDER BY q.createdAt DESC")
    List<LiveQuestion> findByEventIdOrderByCreatedAtDesc(Integer eventId);
}
