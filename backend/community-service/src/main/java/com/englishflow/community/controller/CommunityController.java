package com.englishflow.community.controller;

import com.englishflow.community.dto.CategoryDTO;
import com.englishflow.community.service.CategoryService;
import com.englishflow.community.service.DataInitializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommunityController {
    
    private final CategoryService categoryService;
    private final DataInitializationService dataInitializationService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Community service is running");
    }
    
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeData() {
        dataInitializationService.initializeCategories();
        return ResponseEntity.ok("Categories initialized successfully");
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
}
