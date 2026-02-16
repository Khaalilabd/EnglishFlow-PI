package com.englishflow.community.service;

import com.englishflow.community.dto.CategoryDTO;
import com.englishflow.community.dto.SubCategoryDTO;
import com.englishflow.community.entity.Category;
import com.englishflow.community.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return convertToDTO(category);
    }
    
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setIcon(category.getIcon());
        dto.setColor(category.getColor());
        
        List<SubCategoryDTO> subCategoryDTOs = category.getSubCategories().stream()
                .map(sub -> {
                    SubCategoryDTO subDto = new SubCategoryDTO();
                    subDto.setId(sub.getId());
                    subDto.setName(sub.getName());
                    subDto.setDescription(sub.getDescription());
                    subDto.setCategoryId(category.getId());
                    return subDto;
                })
                .collect(Collectors.toList());
        
        dto.setSubCategories(subCategoryDTOs);
        return dto;
    }
}
