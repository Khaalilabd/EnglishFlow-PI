package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseCategoryDTO;
import com.englishflow.courses.service.ICourseCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CourseCategoryController {
    
    private final ICourseCategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CourseCategoryDTO categoryDTO) {
        try {
            log.info("Creating category: {}", categoryDTO.getName());
            CourseCategoryDTO created = categoryService.createCategory(categoryDTO);
            log.info("Category created successfully with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create category: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id, 
            @RequestBody CourseCategoryDTO categoryDTO) {
        try {
            log.info("Updating category: {}", id);
            CourseCategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating category: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update category: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            CourseCategoryDTO category = categoryService.getById(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            log.error("Error getting category by ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get category: " + e.getMessage()));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CourseCategoryDTO> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error getting all categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get categories: " + e.getMessage()));
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveCategories() {
        try {
            List<CourseCategoryDTO> categories = categoryService.getActiveCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error getting active categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to get categories: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            log.info("Deleting category: {}", id);
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting category: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete category: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            log.info("Toggling active status for category: {}", id);
            categoryService.toggleActive(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error toggling active status for category: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to toggle category status: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/order")
    public ResponseEntity<?> updateDisplayOrder(
            @PathVariable Long id, 
            @RequestParam Integer order) {
        try {
            log.info("Updating display order for category: {} to {}", id, order);
            categoryService.updateDisplayOrder(id, order);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error updating display order for category: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to update display order: " + e.getMessage()));
        }
    }
}
