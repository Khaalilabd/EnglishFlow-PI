package com.englishflow.community.controller;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.dto.PostDTO;
import com.englishflow.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostDTO post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<Page<PostDTO>> getPostsByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<PostDTO> posts = postService.getPostsByTopic(topicId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {
        PostDTO post = postService.updatePost(id, request);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
