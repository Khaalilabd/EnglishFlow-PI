package com.englishflow.courses.service;

import com.englishflow.courses.dto.CourseCategoryDTO;

import java.util.List;

public interface ICourseCategoryService {
    
    CourseCategoryDTO createCategory(CourseCategoryDTO categoryDTO);
    
    CourseCategoryDTO updateCategory(Long id, CourseCategoryDTO categoryDTO);
    
    CourseCategoryDTO getById(Long id);
    
    List<CourseCategoryDTO> getAllCategories();
    
    List<CourseCategoryDTO> getActiveCategories();
    
    void deleteCategory(Long id);
    
    void toggleActive(Long id);
    
    void updateDisplayOrder(Long id, Integer newOrder);
}
