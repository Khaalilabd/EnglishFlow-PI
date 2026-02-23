package com.englishflow.community.controller;

import com.englishflow.community.dto.CreatePostRequest;
import com.englishflow.community.dto.PostDTO;
import com.englishflow.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/community/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Post management endpoints")
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    @Operation(summary = "Create post", description = "Create a new post in a topic")
    public ResponseEntity<PostDTO> createPost(@Valid @RequestBody CreatePostRequest request) {
        PostDTO post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
    
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "Get posts by topic", description = "Retrieve all posts for a specific topic with pagination")
    public ResponseEntity<Page<PostDTO>> getPostsByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<PostDTO> posts = postService.getPostsByTopic(topicId, pageable);
        return ResponseEntity.ok(posts);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update post", description = "Update an existing post")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        PostDTO post = postService.updatePost(id, userId, request);
        return ResponseEntity.ok(post);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post", description = "Delete a post by its ID")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }
}
