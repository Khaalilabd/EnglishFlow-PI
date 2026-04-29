package com.englishflow.club.controller;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.enums.TaskStatus;
import com.englishflow.club.security.JwtAuthenticationFilter;
import com.englishflow.club.security.JwtUtil;
import com.englishflow.club.service.TaskService;
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

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
class TaskControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private TaskService taskService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private TaskDTO testTaskDTO;
    
    @BeforeEach
    void setUp() {
        testTaskDTO = TaskDTO.builder()
                .id(1)
                .text("Complete project documentation")
                .status(TaskStatus.TODO)
                .clubId(1)
                .createdBy(100)
                .build();
    }
    
    @Test
    void getTasksByClubId_ShouldReturnTasks() throws Exception {
        // Given
        List<TaskDTO> tasks = Arrays.asList(testTaskDTO);
        when(taskService.getTasksByClubId(eq(1), eq(100L), isNull())).thenReturn(tasks);
        
        // When & Then
        mockMvc.perform(get("/tasks/club/1")
                .param("userId", "100"))
                .andExpect(status().isOk());
        
        verify(taskService, times(1)).getTasksByClubId(eq(1), eq(100L), isNull());
    }
    
    @Test
    void getTasksByClubIdAndStatus_ShouldReturnFilteredTasks() throws Exception {
        // Given
        List<TaskDTO> tasks = Arrays.asList(testTaskDTO);
        when(taskService.getTasksByClubIdAndStatus(1, TaskStatus.TODO)).thenReturn(tasks);
        
        // When & Then
        mockMvc.perform(get("/tasks/club/1/status/TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("TODO"));
        
        verify(taskService, times(1)).getTasksByClubIdAndStatus(1, TaskStatus.TODO);
    }
    
    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        // Given
        when(taskService.getTaskById(1)).thenReturn(testTaskDTO);
        
        // When & Then
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Complete project documentation"));
        
        verify(taskService, times(1)).getTaskById(1);
    }
    
    @Test
    void createTask_ShouldCreateAndReturnTask() throws Exception {
        // Given
        when(taskService.createTask(any(TaskDTO.class), anyLong())).thenReturn(testTaskDTO);
        
        // When & Then
        mockMvc.perform(post("/tasks")
                .param("userId", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTaskDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Complete project documentation"));
        
        verify(taskService, times(1)).createTask(any(TaskDTO.class), anyLong());
    }
    
    @Test
    void updateTask_ShouldUpdateAndReturnTask() throws Exception {
        // Given
        TaskDTO updatedTask = TaskDTO.builder()
                .id(1)
                .text("Updated task")
                .status(TaskStatus.IN_PROGRESS)
                .clubId(1)
                .build();
        
        when(taskService.updateTask(anyInt(), any(TaskDTO.class), anyLong())).thenReturn(updatedTask);
        
        // When & Then
        mockMvc.perform(put("/tasks/1")
                .param("userId", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated task"));
        
        verify(taskService, times(1)).updateTask(anyInt(), any(TaskDTO.class), anyLong());
    }
    
    @Test
    void deleteTask_ShouldDeleteTask() throws Exception {
        // Given
        doNothing().when(taskService).deleteTask(anyInt(), anyLong());
        
        // When & Then
        mockMvc.perform(delete("/tasks/1")
                .param("userId", "100"))
                .andExpect(status().isNoContent());
        
        verify(taskService, times(1)).deleteTask(anyInt(), anyLong());
    }
    
    @Test
    void countTasksByStatus_ShouldReturnCount() throws Exception {
        // Given
        when(taskService.countTasksByStatus(1, TaskStatus.TODO)).thenReturn(5L);
        
        // When & Then
        mockMvc.perform(get("/tasks/club/1/count/TODO"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
        
        verify(taskService, times(1)).countTasksByStatus(1, TaskStatus.TODO);
    }
}
