package com.englishflow.community.controller;

import com.englishflow.community.dto.CategoryDTO;
import com.englishflow.community.dto.CreateCategoryRequest;
import com.englishflow.community.dto.CreateSubCategoryRequest;
import com.englishflow.community.dto.SubCategoryDTO;
import com.englishflow.community.service.CategoryService;
import com.englishflow.community.service.DataInitializationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community/categories")
@RequiredArgsConstructor
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
    
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequest request) {
        CategoryDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(category);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    // SubCategory endpoints
    @PostMapping("/subcategories")
    public ResponseEntity<SubCategoryDTO> createSubCategory(@Valid @RequestBody CreateSubCategoryRequest request) {
        SubCategoryDTO subCategory = categoryService.createSubCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(subCategory);
    }
    
    @PutMapping("/subcategories/{id}")
    public ResponseEntity<SubCategoryDTO> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubCategoryRequest request) {
        SubCategoryDTO subCategory = categoryService.updateSubCategory(id, request);
        return ResponseEntity.ok(subCategory);
    }
    
    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        categoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }
}
