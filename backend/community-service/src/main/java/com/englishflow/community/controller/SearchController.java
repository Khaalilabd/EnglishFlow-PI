package com.englishflow.community.controller;

import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/community/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Search endpoints for topics and content")
public class SearchController {
    
    private final SearchService searchService;
    
    @GetMapping("/topics")
    @Operation(summary = "Search topics", description = "Search topics by keyword in title or content")
    public ResponseEntity<Page<TopicDTO>> searchTopics(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TopicDTO> topics = searchService.searchTopics(keyword, pageable);
        return ResponseEntity.ok(topics);
    }
}
