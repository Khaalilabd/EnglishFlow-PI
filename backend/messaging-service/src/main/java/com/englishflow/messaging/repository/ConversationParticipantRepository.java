package com.englishflow.messaging.repository;

import com.englishflow.messaging.model.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    
    List<ConversationParticipant> findByConversationId(Long conversationId);
    
    @Query("SELECT p FROM ConversationParticipant p " +
           "WHERE p.conversation.id = :conversationId AND p.userId = :userId")
    Optional<ConversationParticipant> findByConversationIdAndUserId(
        @Param("conversationId") Long conversationId, 
        @Param("userId") Long userId);
    
    boolean existsByConversationIdAndUserId(Long conversationId, Long userId);
}
