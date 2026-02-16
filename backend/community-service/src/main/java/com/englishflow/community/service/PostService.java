package com.englishflow.community.service;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.dto.PostDTO;
import com.englishflow.community.entity.Post;
import com.englishflow.community.entity.Topic;
import com.englishflow.community.repository.PostRepository;
import com.englishflow.community.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    
    @Transactional
    public PostDTO createPost(CreatePostRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        if (topic.getIsLocked()) {
            throw new RuntimeException("Topic is locked");
        }
        
        Post post = new Post();
        post.setContent(request.getContent());
        post.setUserId(request.getUserId());
        post.setUserName(request.getUserName());
        post.setTopic(topic);
        
        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }
    
    @Transactional(readOnly = true)
    public Page<PostDTO> getPostsByTopic(Long topicId, Pageable pageable) {
        return postRepository.findByTopicId(topicId, pageable)
                .map(this::convertToDTO);
    }
    
    @Transactional
    public PostDTO updatePost(Long id, CreatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.setContent(request.getContent());
        
        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }
    
    @Transactional
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found");
        }
        postRepository.deleteById(id);
    }
    
    private PostDTO convertToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setUserId(post.getUserId());
        dto.setUserName(post.getUserName());
        dto.setTopicId(post.getTopic().getId());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}
