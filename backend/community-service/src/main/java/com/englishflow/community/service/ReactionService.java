package com.englishflow.community.service;

import com.englishflow.community.dto.ReactionDTO;
import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.Reaction;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.ReactionRepository;
import com.englishflow.community.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactionService {
    
    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    
    @Transactional
    public ReactionDTO addReactionToPost(Long postId, Long userId, Reaction.ReactionType type) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
        
        Optional<Reaction> existing = reactionRepository.findByUserIdAndPostId(userId, postId);
        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            reaction.setType(type);
            Reaction updated = reactionRepository.save(reaction);
            log.info("Updated reaction for user {} on post {}", userId, postId);
            return convertToDTO(updated);
        }
        
        Reaction reaction = new Reaction();
        reaction.setUserId(userId);
        reaction.setPost(post);
        reaction.setType(type);
        
        post.setReactionsCount(post.getReactionsCount() + 1);
        postRepository.save(post);
        
        Reaction saved = reactionRepository.save(reaction);
        log.info("Added reaction for user {} on post {}", userId, postId);
        return convertToDTO(saved);
    }
    
    @Transactional
    public ReactionDTO addReactionToTopic(Long topicId, Long userId, Reaction.ReactionType type) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", topicId));
        
        Optional<Reaction> existing = reactionRepository.findByUserIdAndTopicId(userId, topicId);
        if (existing.isPresent()) {
            Reaction reaction = existing.get();
            reaction.setType(type);
            Reaction updated = reactionRepository.save(reaction);
            log.info("Updated reaction for user {} on topic {}", userId, topicId);
            return convertToDTO(updated);
        }
        
        Reaction reaction = new Reaction();
        reaction.setUserId(userId);
        reaction.setTopic(topic);
        reaction.setType(type);
        
        topic.setReactionsCount(topic.getReactionsCount() + 1);
        topicRepository.save(topic);
        
        Reaction saved = reactionRepository.save(reaction);
        log.info("Added reaction for user {} on topic {}", userId, topicId);
        return convertToDTO(saved);
    }
    
    @Transactional
    public void removeReactionFromPost(Long postId, Long userId) {
        Reaction reaction = reactionRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found"));
        
        Post post = reaction.getPost();
        post.setReactionsCount(Math.max(0, post.getReactionsCount() - 1));
        postRepository.save(post);
        
        reactionRepository.delete(reaction);
        log.info("Removed reaction for user {} from post {}", userId, postId);
    }
    
    @Transactional
    public void removeReactionFromTopic(Long topicId, Long userId) {
        Reaction reaction = reactionRepository.findByUserIdAndTopicId(userId, topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Reaction not found"));
        
        Topic topic = reaction.getTopic();
        topic.setReactionsCount(Math.max(0, topic.getReactionsCount() - 1));
        topicRepository.save(topic);
        
        reactionRepository.delete(reaction);
        log.info("Removed reaction for user {} from topic {}", userId, topicId);
    }
    
    @Transactional(readOnly = true)
    public Long getPostReactionsCount(Long postId) {
        return reactionRepository.countByPostId(postId);
    }
    
    @Transactional(readOnly = true)
    public Long getTopicReactionsCount(Long topicId) {
        return reactionRepository.countByTopicId(topicId);
    }
    
    private ReactionDTO convertToDTO(Reaction reaction) {
        ReactionDTO dto = new ReactionDTO();
        dto.setId(reaction.getId());
        dto.setUserId(reaction.getUserId());
        dto.setType(reaction.getType());
        if (reaction.getPost() != null) {
            dto.setPostId(reaction.getPost().getId());
        }
        if (reaction.getTopic() != null) {
            dto.setTopicId(reaction.getTopic().getId());
        }
        return dto;
    }
}
