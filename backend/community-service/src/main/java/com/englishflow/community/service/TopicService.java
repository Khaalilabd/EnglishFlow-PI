package com.englishflow.community.service;

import com.englishflow.community.dto.CreateTopicRequest;
import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.entity.SubCategory;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.repository.SubCategoryRepository;
import com.englishflow.community.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TopicService {
    
    private final TopicRepository topicRepository;
    private final SubCategoryRepository subCategoryRepository;
    
    @Transactional
    public TopicDTO createTopic(CreateTopicRequest request) {
        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("SubCategory not found"));
        
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
        return convertToDTO(savedTopic);
    }
    
    @Transactional(readOnly = true)
    public Page<TopicDTO> getTopicsBySubCategory(Long subCategoryId, Pageable pageable) {
        return topicRepository.findBySubCategoryId(subCategoryId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public TopicDTO getTopicById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        // Increment view count
        topic.setViewsCount(topic.getViewsCount() + 1);
        topicRepository.save(topic);
        
        return convertToDTO(topic);
    }
    
    @Transactional
    public TopicDTO updateTopic(Long id, CreateTopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        
        Topic updatedTopic = topicRepository.save(topic);
        return convertToDTO(updatedTopic);
    }
    
    @Transactional
    public void deleteTopic(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new RuntimeException("Topic not found");
        }
        topicRepository.deleteById(id);
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
        dto.setIsPinned(topic.getIsPinned());
        dto.setIsLocked(topic.getIsLocked());
        dto.setPostsCount(topic.getPosts().size());
        dto.setCreatedAt(topic.getCreatedAt());
        dto.setUpdatedAt(topic.getUpdatedAt());
        return dto;
    }
}
