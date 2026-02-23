package com.englishflow.community.service;

import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.entity.Topic;
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
public class SearchService {
    
    private final TopicRepository topicRepository;
    
    @Transactional(readOnly = true)
    public Page<TopicDTO> searchTopics(String keyword, Pageable pageable) {
        log.info("Searching topics with keyword: {}", keyword);
        Page<Topic> topics = topicRepository.searchByKeyword(keyword, pageable);
        return topics.map(this::convertToDTO);
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
}
