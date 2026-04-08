package com.englishflow.event.repository;

import com.englishflow.event.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByEventIdAndModeratedFalseOrderBySentAtAsc(Integer eventId);
}
