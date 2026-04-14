package com.englishflow.community.controller;

import com.englishflow.community.dto.CategoryDTO;
import com.englishflow.community.dto.CreateCategoryRequest;
import com.englishflow.community.dto.CreateSubCategoryRequest;
import com.englishflow.community.dto.SubCategoryDTO;
import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CommunityController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser
    void health_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/community/categories/health")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Community service is running"));
    }

    @Test
    @WithMockUser
    void getAllCategories_ShouldReturnCategoryList() throws Exception {
        CategoryDTO category1 = new CategoryDTO();
        category1.setId(1L);
        category1.setName("General");

        CategoryDTO category2 = new CategoryDTO();
        category2.setId(2L);
        category2.setName("Discussions");

        List<CategoryDTO> categories = Arrays.asList(category1, category2);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/community/categories")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("General"));

        verify(categoryService).getAllCategories();
    }

    @Test
    @WithMockUser
    void getCategoryById_ShouldReturnCategory() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setId(1L);
        category.setName("General");

        when(categoryService.getCategoryById(1L)).thenReturn(category);

        mockMvc.perform(get("/community/categories/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("General"));

        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @WithMockUser
    void createCategory_ShouldReturnCreatedCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("New Category");
        request.setDescription("Description");
        request.setIcon("icon-name");
        request.setColor("#FF0000");

        CategoryDTO created = new CategoryDTO();
        created.setId(1L);
        created.setName("New Category");

        when(categoryService.createCategory(any(CreateCategoryRequest.class))).thenReturn(created);

        mockMvc.perform(post("/community/categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Category"));

        verify(categoryService).createCategory(any(CreateCategoryRequest.class));
    }

    @Test
    @WithMockUser
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Updated Category");
        request.setDescription("Updated Description");
        request.setIcon("updated-icon");
        request.setColor("#00FF00");

        CategoryDTO updated = new CategoryDTO();
        updated.setId(1L);
        updated.setName("Updated Category");

        when(categoryService.updateCategory(eq(1L), any(CreateCategoryRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/community/categories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Category"));

        verify(categoryService).updateCategory(eq(1L), any(CreateCategoryRequest.class));
    }

    @Test
    @WithMockUser
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/community/categories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    @WithMockUser
    void getAllSubCategories_ShouldReturnSubCategoryList() throws Exception {
        SubCategoryDTO sub1 = new SubCategoryDTO();
        sub1.setId(1L);
        sub1.setName("Sub1");

        SubCategoryDTO sub2 = new SubCategoryDTO();
        sub2.setId(2L);
        sub2.setName("Sub2");

        List<SubCategoryDTO> subCategories = Arrays.asList(sub1, sub2);
        when(categoryService.getAllSubCategories()).thenReturn(subCategories);

        mockMvc.perform(get("/community/categories/subcategories")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(categoryService).getAllSubCategories();
    }

    @Test
    @WithMockUser
    void getSubCategoryById_ShouldReturnSubCategory() throws Exception {
        SubCategoryDTO subCategory = new SubCategoryDTO();
        subCategory.setId(1L);
        subCategory.setName("SubCategory");

        when(categoryService.getSubCategoryById(1L)).thenReturn(subCategory);

        mockMvc.perform(get("/community/categories/subcategories/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("SubCategory"));

        verify(categoryService).getSubCategoryById(1L);
    }

    @Test
    @WithMockUser
    void createSubCategory_ShouldReturnCreatedSubCategory() throws Exception {
        CreateSubCategoryRequest request = new CreateSubCategoryRequest();
        request.setName("New SubCategory");
        request.setCategoryId(1L);
        request.setDescription("SubCategory Description");

        SubCategoryDTO created = new SubCategoryDTO();
        created.setId(1L);
        created.setName("New SubCategory");

        when(categoryService.createSubCategory(any(CreateSubCategoryRequest.class))).thenReturn(created);

        mockMvc.perform(post("/community/categories/subcategories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New SubCategory"));

        verify(categoryService).createSubCategory(any(CreateSubCategoryRequest.class));
    }

    @Test
    @WithMockUser
    void updateSubCategory_ShouldReturnUpdatedSubCategory() throws Exception {
        CreateSubCategoryRequest request = new CreateSubCategoryRequest();
        request.setName("Updated SubCategory");
        request.setCategoryId(1L);
        request.setDescription("Updated Description");

        SubCategoryDTO updated = new SubCategoryDTO();
        updated.setId(1L);
        updated.setName("Updated SubCategory");

        when(categoryService.updateSubCategory(eq(1L), any(CreateSubCategoryRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/community/categories/subcategories/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated SubCategory"));

        verify(categoryService).updateSubCategory(eq(1L), any(CreateSubCategoryRequest.class));
    }

    @Test
    @WithMockUser
    void deleteSubCategory_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteSubCategory(1L);

        mockMvc.perform(delete("/community/categories/subcategories/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteSubCategory(1L);
    }
}
