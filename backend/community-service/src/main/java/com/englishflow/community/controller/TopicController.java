package com.englishflow.community.controller;

import com.englishflow.community.dto.CreateTopicRequest;
import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.service.TopicService;
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
@RequestMapping("/api/community/topics")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TopicController {
    
    private final TopicService topicService;
    
    @PostMapping
    public ResponseEntity<TopicDTO> createTopic(@Valid @RequestBody CreateTopicRequest request) {
        TopicDTO topic = topicService.createTopic(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }
    
    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<Page<TopicDTO>> getTopicsBySubCategory(
            @PathVariable Long subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TopicDTO> topics = topicService.getTopicsBySubCategory(subCategoryId, pageable);
        return ResponseEntity.ok(topics);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TopicDTO> getTopicById(@PathVariable Long id) {
        TopicDTO topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TopicDTO> updateTopic(
            @PathVariable Long id,
            @Valid @RequestBody CreateTopicRequest request) {
        TopicDTO topic = topicService.updateTopic(id, request);
        return ResponseEntity.ok(topic);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/pin")
    public ResponseEntity<TopicDTO> pinTopic(@PathVariable Long id) {
        TopicDTO topic = topicService.pinTopic(id);
        return ResponseEntity.ok(topic);
    }
    
    @PutMapping("/{id}/unpin")
    public ResponseEntity<TopicDTO> unpinTopic(@PathVariable Long id) {
        TopicDTO topic = topicService.unpinTopic(id);
        return ResponseEntity.ok(topic);
    }
    
    @PutMapping("/{id}/lock")
    public ResponseEntity<TopicDTO> lockTopic(@PathVariable Long id) {
        TopicDTO topic = topicService.lockTopic(id);
        return ResponseEntity.ok(topic);
    }
    
    @PutMapping("/{id}/unlock")
    public ResponseEntity<TopicDTO> unlockTopic(@PathVariable Long id) {
        TopicDTO topic = topicService.unlockTopic(id);
        return ResponseEntity.ok(topic);
    }
}
