package com.englishflow.club.controller;

import com.englishflow.club.dto.ExpenseDTO;
import com.englishflow.club.security.JwtAuthenticationFilter;
import com.englishflow.club.security.JwtUtil;
import com.englishflow.club.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ExpenseService expenseService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private ExpenseDTO testExpenseDTO;
    
    @BeforeEach
    void setUp() {
        testExpenseDTO = ExpenseDTO.builder()
                .id(1)
                .clubId(1)
                .designation("Office supplies")
                .amount(50.0)
                .expenseDate(LocalDateTime.now())
                .createdBy(100L)
                .build();
    }
    
    @Test
    void getExpensesByClub_ShouldReturnExpenses() throws Exception {
        // Given
        List<ExpenseDTO> expenses = Arrays.asList(testExpenseDTO);
        when(expenseService.getExpensesByClub(1)).thenReturn(expenses);
        
        // When & Then
        mockMvc.perform(get("/expenses/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].designation").value("Office supplies"));
        
        verify(expenseService, times(1)).getExpensesByClub(1);
    }
    
    @Test
    void getExpenseById_ShouldReturnExpense() throws Exception {
        // Given
        when(expenseService.getExpenseById(1)).thenReturn(testExpenseDTO);
        
        // When & Then
        mockMvc.perform(get("/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.designation").value("Office supplies"));
        
        verify(expenseService, times(1)).getExpenseById(1);
    }
    
    @Test
    void getTotalExpenses_ShouldReturnTotal() throws Exception {
        // Given
        when(expenseService.getTotalExpensesByClub(1)).thenReturn(150.0);
        
        // When & Then
        mockMvc.perform(get("/expenses/club/1/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.0"));
        
        verify(expenseService, times(1)).getTotalExpensesByClub(1);
    }
    
    @Test
    void createExpense_ShouldCreateAndReturnExpense() throws Exception {
        // Given
        when(expenseService.createExpense(any(ExpenseDTO.class))).thenReturn(testExpenseDTO);
        
        // When & Then
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testExpenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.designation").value("Office supplies"));
        
        verify(expenseService, times(1)).createExpense(any(ExpenseDTO.class));
    }
    
    @Test
    void createIncomeEntry_ShouldCreateAndReturnIncome() throws Exception {
        // Given
        ExpenseDTO incomeDTO = ExpenseDTO.builder()
                .clubId(1)
                .designation("Membership fee")
                .amount(100.0)
                .build();
        
        when(expenseService.createIncomeEntry(any(ExpenseDTO.class))).thenReturn(incomeDTO);
        
        // When & Then
        mockMvc.perform(post("/expenses/income")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incomeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.designation").value("Membership fee"));
        
        verify(expenseService, times(1)).createIncomeEntry(any(ExpenseDTO.class));
    }
    
    @Test
    void updateExpense_ShouldUpdateAndReturnExpense() throws Exception {
        // Given
        ExpenseDTO updatedExpense = ExpenseDTO.builder()
                .id(1)
                .designation("Updated expense")
                .amount(75.0)
                .build();
        
        when(expenseService.updateExpense(anyInt(), any(ExpenseDTO.class))).thenReturn(updatedExpense);
        
        // When & Then
        mockMvc.perform(put("/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedExpense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.designation").value("Updated expense"));
        
        verify(expenseService, times(1)).updateExpense(anyInt(), any(ExpenseDTO.class));
    }
    
    @Test
    void deleteExpense_ShouldDeleteExpense() throws Exception {
        // Given
        doNothing().when(expenseService).deleteExpense(1);
        
        // When & Then
        mockMvc.perform(delete("/expenses/1"))
                .andExpect(status().isNoContent());
        
        verify(expenseService, times(1)).deleteExpense(1);
    }
}
