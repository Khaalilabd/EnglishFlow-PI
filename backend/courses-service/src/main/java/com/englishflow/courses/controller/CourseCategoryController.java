package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseCategoryDTO;
import com.englishflow.courses.service.ICourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CourseCategoryController {
    
    private final ICourseCategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<CourseCategoryDTO> createCategory(@RequestBody CourseCategoryDTO categoryDTO) {
        CourseCategoryDTO created = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CourseCategoryDTO> updateCategory(
            @PathVariable Long id, 
            @RequestBody CourseCategoryDTO categoryDTO) {
        CourseCategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseCategoryDTO> getById(@PathVariable Long id) {
        CourseCategoryDTO category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }
    
    @GetMapping
    public ResponseEntity<List<CourseCategoryDTO>> getAllCategories() {
        List<CourseCategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CourseCategoryDTO>> getActiveCategories() {
        List<CourseCategoryDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<Void> toggleActive(@PathVariable Long id) {
        categoryService.toggleActive(id);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/order")
    public ResponseEntity<Void> updateDisplayOrder(
            @PathVariable Long id, 
            @RequestParam Integer order) {
        categoryService.updateDisplayOrder(id, order);
        return ResponseEntity.ok().build();
    }
}
