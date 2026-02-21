package com.englishflow.messaging.repository;

import com.englishflow.messaging.model.MessageReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {
    
    List<MessageReaction> findByMessageId(Long messageId);
    
    Optional<MessageReaction> findByMessageIdAndUserIdAndEmoji(Long messageId, Long userId, String emoji);
    
    @Query("SELECT r FROM MessageReaction r WHERE r.message.id IN :messageIds")
    List<MessageReaction> findByMessageIdIn(@Param("messageIds") List<Long> messageIds);
    
    void deleteByMessageIdAndUserIdAndEmoji(Long messageId, Long userId, String emoji);
    
    long countByMessageIdAndEmoji(Long messageId, String emoji);
}
