package com.englishflow.community.repository;

import com.englishflow.community.entity.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    
    Optional<Reaction> findByUserIdAndPostId(Long userId, Long postId);
    
    Optional<Reaction> findByUserIdAndTopicId(Long userId, Long topicId);
    
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post.id = :postId")
    Long countByPostId(Long postId);
    
    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.topic.id = :topicId")
    Long countByTopicId(Long topicId);
    
    void deleteByUserIdAndPostId(Long userId, Long postId);
    
    void deleteByUserIdAndTopicId(Long userId, Long topicId);
}
