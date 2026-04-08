package com.englishflow.event.repository;

import com.englishflow.event.entity.HandRaise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HandRaiseRepository extends JpaRepository<HandRaise, Long> {
    List<HandRaise> findByEventIdAndDismissedFalseOrderByRaisedAtAsc(Integer eventId);
    Optional<HandRaise> findByEventIdAndUserIdAndDismissedFalse(Integer eventId, Long userId);
}
