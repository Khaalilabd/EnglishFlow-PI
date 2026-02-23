package com.englishflow.community.service;

import com.englishflow.community.dto.CreateTopicRequest;
import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.exception.ResourceNotFoundException;
import com.englishflow.community.exception.UnauthorizedException;
import com.englishflow.community.repository.SubCategoryRepository;
import com.englishflow.community.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    
    private final TopicRepository topicRepository;
    private final SubCategoryRepository subCategoryRepository;
    
    @Transactional
    public TopicDTO createTopic(CreateTopicRequest request) {
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("SubCategory", request.getSubCategoryId()));
        
        Topic topic = new Topic();
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setUserId(request.getUserId());
        topic.setUserName(request.getUserName());
        topic.setSubCategory(subCategory);
        topic.setViewsCount(0);
        topic.setIsPinned(false);
        topic.setIsLocked(false);
        
        Topic savedTopic = topicRepository.save(topic);
        log.info("Created topic {} in subcategory {} by user {}", savedTopic.getId(), request.getSubCategoryId(), request.getUserId());
        return convertToDTO(savedTopic);
    }
    
    @Transactional(readOnly = true)
    public Page<TopicDTO> getTopicsBySubCategory(Long subCategoryId, Pageable pageable) {
        return topicRepository.findBySubCategoryId(subCategoryId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public TopicDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));
        
        // Increment view count
        topic.setViewsCount(topic.getViewsCount() + 1);
        topicRepository.save(topic);
        
        return convertToDTO(topic);
    }
    
    @Transactional
    public TopicDTO updateTopic(Long id, Long userId, CreateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));

        // Vérifier que l'utilisateur est le propriétaire
        if (!topic.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this topic");
        }

        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());

        Topic updatedTopic = topicRepository.save(topic);
        log.info("Updated topic {} by user {}", id, userId);
        return convertToDTO(updatedTopic);
    }

    
    @Transactional
    public void deleteTopic(Long id, Long userId) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));

        // Vérifier que l'utilisateur est le propriétaire
        if (!topic.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this topic");
        }

        topicRepository.delete(topic);
        log.info("Deleted topic {} by user {}", id, userId);
    }

    
    @Transactional
    public TopicDTO pinTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));
        topic.setIsPinned(true);
        Topic updatedTopic = topicRepository.save(topic);
        log.info("Pinned topic {}", id);
        return convertToDTO(updatedTopic);
    }
    
    @Transactional
    public TopicDTO unpinTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));
        topic.setIsPinned(false);
        Topic updatedTopic = topicRepository.save(topic);
        log.info("Unpinned topic {}", id);
        return convertToDTO(updatedTopic);
    }
    
    @Transactional
    public TopicDTO lockTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));
        topic.setIsLocked(true);
        Topic updatedTopic = topicRepository.save(topic);
        log.info("Locked topic {}", id);
        return convertToDTO(updatedTopic);
    }
    
    @Transactional
    public TopicDTO unlockTopic(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", id));
        topic.setIsLocked(false);
        Topic updatedTopic = topicRepository.save(topic);
        log.info("Unlocked topic {}", id);
        return convertToDTO(updatedTopic);
    }
    
    private TopicDTO convertToDTO(Topic topic) {
        TopicDTO dto = new TopicDTO();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setContent(topic.getContent());
        dto.setUserId(topic.getUserId());
        dto.setUserName(topic.getUserName());
        dto.setSubCategoryId(topic.getSubCategory().getId());
        dto.setViewsCount(topic.getViewsCount());
        dto.setReactionsCount(topic.getReactionsCount());
        dto.setIsPinned(topic.getIsPinned());
        dto.setIsLocked(topic.getIsLocked());
        dto.setPostsCount(topic.getPosts().size());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        return dto;
    }
    
    // Moderation methods
    @Transactional(readOnly = true)
    public Page<TopicDTO> getAllTopicsForModeration(Long categoryId, String status, String search, Pageable pageable) {
        // TODO: Implement filtering logic
        // For now, return all topics
        return topicRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    @Transactional
    public int bulkPinTopics(java.util.List<Long> topicIds) {
        int count = 0;
        for (Long id : topicIds) {
            try {
                Topic topic = topicRepository.findById(id).orElse(null);
                if (topic != null) {
                    topic.setIsPinned(true);
                    topicRepository.save(topic);
                    count++;
                }
            } catch (Exception e) {
                log.error("Error pinning topic {}: {}", id, e.getMessage());
            }
        }
        log.info("Bulk pinned {} topics", count);
        return count;
    }
    
    @Transactional
    public int bulkUnpinTopics(java.util.List<Long> topicIds) {
        int count = 0;
        for (Long id : topicIds) {
            try {
                Topic topic = topicRepository.findById(id).orElse(null);
                if (topic != null) {
                    topic.setIsPinned(false);
                    topicRepository.save(topic);
                    count++;
                }
            } catch (Exception e) {
                log.error("Error unpinning topic {}: {}", id, e.getMessage());
            }
        }
        log.info("Bulk unpinned {} topics", count);
        return count;
    }
    
    @Transactional
    public int bulkLockTopics(java.util.List<Long> topicIds) {
        int count = 0;
        for (Long id : topicIds) {
            try {
                Topic topic = topicRepository.findById(id).orElse(null);
                if (topic != null) {
                    topic.setIsLocked(true);
                    topicRepository.save(topic);
                    count++;
                }
            } catch (Exception e) {
                log.error("Error locking topic {}: {}", id, e.getMessage());
            }
        }
        log.info("Bulk locked {} topics", count);
        return count;
    }
    
    @Transactional
    public int bulkUnlockTopics(java.util.List<Long> topicIds) {
        int count = 0;
        for (Long id : topicIds) {
            try {
                Topic topic = topicRepository.findById(id).orElse(null);
                if (topic != null) {
                    topic.setIsLocked(false);
                    topicRepository.save(topic);
                    count++;
                }
            } catch (Exception e) {
                log.error("Error unlocking topic {}: {}", id, e.getMessage());
            }
        }
        log.info("Bulk unlocked {} topics", count);
        return count;
    }
    
    @Transactional
    public int bulkDeleteTopics(java.util.List<Long> topicIds, Long userId) {
        int count = 0;
        for (Long id : topicIds) {
            try {
                Topic topic = topicRepository.findById(id).orElse(null);
                if (topic != null) {
                    // For moderation, allow deletion without ownership check
                    // In production, add role check here
                    topicRepository.delete(topic);
                    count++;
                }
            } catch (Exception e) {
                log.error("Error deleting topic {}: {}", id, e.getMessage());
            }
        }
        log.info("Bulk deleted {} topics by user {}", count, userId);
        return count;
    }
    
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getModerationStats() {
        long totalTopics = topicRepository.count();
        long pinnedTopics = topicRepository.countByIsPinned(true);
        long lockedTopics = topicRepository.countByIsLocked(true);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalTopics", totalTopics);
        stats.put("pinnedTopics", pinnedTopics);
        stats.put("lockedTopics", lockedTopics);
        stats.put("normalTopics", totalTopics - pinnedTopics - lockedTopics);
        
        return stats;
    }
}
