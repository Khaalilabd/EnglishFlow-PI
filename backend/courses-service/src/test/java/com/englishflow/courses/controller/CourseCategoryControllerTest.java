package com.englishflow.courses.controller;

import com.englishflow.courses.dto.CourseCategoryDTO;
import com.englishflow.courses.service.ICourseCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICourseCategoryService categoryService;

    private CourseCategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        categoryDTO = new CourseCategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Grammar");
        categoryDTO.setDescription("Grammar courses");
        categoryDTO.setActive(true);
        categoryDTO.setDisplayOrder(1);
    }

    @Test
    void createCategory_ShouldReturnCreated() throws Exception {
        when(categoryService.createCategory(any(CourseCategoryDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Grammar"));

        verify(categoryService, times(1)).createCategory(any(CourseCategoryDTO.class));
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(CourseCategoryDTO.class))).thenReturn(categoryDTO);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CourseCategoryDTO.class));
    }

    @Test
    void getById_ShouldReturnCategory() throws Exception {
        when(categoryService.getById(1L)).thenReturn(categoryDTO);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Grammar"));

        verify(categoryService, times(1)).getById(1L);
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        List<CourseCategoryDTO> categories = Arrays.asList(categoryDTO);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getActiveCategories_ShouldReturnActiveCategories() throws Exception {
        List<CourseCategoryDTO> categories = Arrays.asList(categoryDTO);
        when(categoryService.getActiveCategories()).thenReturn(categories);

        mockMvc.perform(get("/categories/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));

        verify(categoryService, times(1)).getActiveCategories();
    }

    @Test
    void deleteCategory_ShouldReturnNoContent() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void toggleActive_ShouldReturnOk() throws Exception {
        doNothing().when(categoryService).toggleActive(1L);

        mockMvc.perform(put("/categories/1/toggle-active"))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).toggleActive(1L);
    }

    @Test
    void updateDisplayOrder_ShouldReturnOk() throws Exception {
        doNothing().when(categoryService).updateDisplayOrder(1L, 2);

        mockMvc.perform(put("/categories/1/order")
                .param("order", "2"))
                .andExpect(status().isOk());

        verify(categoryService, times(1)).updateDisplayOrder(1L, 2);
    }
}
