package com.englishflow.messaging.repository;

import com.englishflow.messaging.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m " +
           "WHERE m.conversation.id = :conversationId " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findMessagesByConversationId(@Param("conversationId") Long conversationId, 
                                                Pageable pageable);
    
    @Query("SELECT m FROM Message m " +
           "LEFT JOIN FETCH m.readStatuses " +
           "WHERE m.id = :messageId")
    Optional<Message> findByIdWithReadStatuses(@Param("messageId") Long messageId);
    
    @Query("SELECT COUNT(m) FROM Message m " +
           "JOIN m.conversation c " +
           "JOIN c.participants p " +
           "WHERE p.userId = :userId " +
           "AND m.senderId != :userId " +
           "AND (p.lastReadAt IS NULL OR m.createdAt > p.lastReadAt)")
    Long countUnreadMessages(@Param("userId") Long userId);
}
