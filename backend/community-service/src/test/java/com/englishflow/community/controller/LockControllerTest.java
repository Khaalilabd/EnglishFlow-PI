package com.englishflow.community.controller;

import com.englishflow.community.service.LockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LockController.class)
class LockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LockService lockService;

    @Test
    void lockCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).lockCategory(1L, 100L);

        mockMvc.perform(post("/community/lock/category/1")
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).lockCategory(1L, 100L);
    }

    @Test
    void unlockCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).unlockCategory(1L, 100L);

        mockMvc.perform(delete("/community/lock/category/1")
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).unlockCategory(1L, 100L);
    }

    @Test
    void lockSubCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).lockSubCategory(1L, 100L);

        mockMvc.perform(post("/community/lock/subcategory/1")
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).lockSubCategory(1L, 100L);
    }

    @Test
    void unlockSubCategory_ShouldReturnOk() throws Exception {
        doNothing().when(lockService).unlockSubCategory(1L, 100L);

        mockMvc.perform(delete("/community/lock/subcategory/1")
                        .param("userId", "100"))
                .andExpect(status().isOk());

        verify(lockService).unlockSubCategory(1L, 100L);
    }
}
