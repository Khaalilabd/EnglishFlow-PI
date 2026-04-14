package com.englishflow.community.controller;

import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.LockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LockController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class LockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LockService lockService;

    @Test
    @WithMockUser
    void lockCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).lockCategory(1L, 100L);

        mockMvc.perform(post("/community/lock/category/1")
                        .with(csrf())
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).lockCategory(1L, 100L);
    }

    @Test
    @WithMockUser
    void unlockCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).unlockCategory(1L, 100L);

        mockMvc.perform(delete("/community/lock/category/1")
                        .with(csrf())
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).unlockCategory(1L, 100L);
    }

    @Test
    @WithMockUser
    void lockSubCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).lockSubCategory(1L, 100L);

        mockMvc.perform(post("/community/lock/subcategory/1")
                        .with(csrf())
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).lockSubCategory(1L, 100L);
    }

    @Test
    @WithMockUser
    void unlockSubCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).unlockSubCategory(1L, 100L);

        mockMvc.perform(delete("/community/lock/subcategory/1")
                        .with(csrf())
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).unlockSubCategory(1L, 100L);
    }
}
