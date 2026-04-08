package com.englishflow.event.repository;

import com.englishflow.event.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByEventIdOrderByCreatedAtDesc(Integer eventId);
    Optional<Poll> findByEventIdAndActiveTrue(Integer eventId);
}
