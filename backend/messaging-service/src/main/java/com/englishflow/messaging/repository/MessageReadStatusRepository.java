package com.englishflow.messaging.repository;

import com.englishflow.messaging.model.MessageReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReadStatusRepository extends JpaRepository<MessageReadStatus, Long> {
    
    List<MessageReadStatus> findByMessageId(Long messageId);
    
    Optional<MessageReadStatus> findByMessageIdAndUserId(Long messageId, Long userId);
    
    boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}
