package com.englishflow.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ActivationViewController.class)
class ActivationViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void activationPending_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/activation-pending"))
                .andExpect(status().isOk())
                .andExpect(view().name("activation-pending"));
    }

    @Test
    void activationSuccess_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/activation-success"))
                .andExpect(status().isOk())
                .andExpect(view().name("activation-success"));
    }

    @Test
    void activationError_ShouldReturnView() throws Exception {
        mockMvc.perform(get("/activation-error"))
                .andExpect(status().isOk())
                .andExpect(view().name("activation-error"));
    }
}
