package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.security.JwtAuthenticationFilter;
import com.englishflow.club.security.JwtUtil;
import com.englishflow.club.service.ClubService;
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

@WebMvcTest(ClubController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClubControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ClubService clubService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private ClubDTO testClubDTO;
    
    @BeforeEach
    void setUp() {
        testClubDTO = ClubDTO.builder()
                .id(1)
                .name("English Conversation Club")
                .description("Practice English conversation")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .build();
    }
    
    @Test
    void getAllClubs_ShouldReturnAllClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(testClubDTO);
        when(clubService.getAllClubs()).thenReturn(clubs);
        
        // When & Then
        mockMvc.perform(get("/clubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("English Conversation Club"));
        
        verify(clubService, times(1)).getAllClubs();
    }
    
    @Test
    void getClubsByCategory_ShouldReturnFilteredClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(testClubDTO);
        when(clubService.getClubsByCategory(ClubCategory.CONVERSATION)).thenReturn(clubs);
        
        // When & Then
        mockMvc.perform(get("/clubs/category/CONVERSATION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("CONVERSATION"));
        
        verify(clubService, times(1)).getClubsByCategory(ClubCategory.CONVERSATION);
    }
    
    @Test
    void searchClubsByName_ShouldReturnMatchingClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(testClubDTO);
        when(clubService.searchClubsByName("English")).thenReturn(clubs);
        
        // When & Then
        mockMvc.perform(get("/clubs/search")
                .param("name", "English"))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).searchClubsByName("English");
    }
    
    @Test
    void getClubById_ShouldReturnClub() throws Exception {
        // Given
        when(clubService.getClubById(1)).thenReturn(testClubDTO);
        
        // When & Then
        mockMvc.perform(get("/clubs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("English Conversation Club"));
        
        verify(clubService, times(1)).getClubById(1);
    }
    
    @Test
    void createClub_ShouldCreateAndReturnClub() throws Exception {
        // Given
        when(clubService.createClub(any(ClubDTO.class))).thenReturn(testClubDTO);
        
        // When & Then
        mockMvc.perform(post("/clubs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClubDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("English Conversation Club"));
        
        verify(clubService, times(1)).createClub(any(ClubDTO.class));
    }
    
    @Test
    void updateClub_ShouldUpdateAndReturnClub() throws Exception {
        // Given
        when(clubService.updateClub(anyInt(), any(ClubDTO.class), anyLong())).thenReturn(testClubDTO);
        
        // When & Then
        mockMvc.perform(put("/clubs/1")
                .param("requesterId", "100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testClubDTO)))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).updateClub(anyInt(), any(ClubDTO.class), anyLong());
    }
    
    @Test
    void deleteClub_ShouldDeleteClub() throws Exception {
        // Given
        doNothing().when(clubService).deleteClub(1);
        
        // When & Then
        mockMvc.perform(delete("/clubs/1"))
                .andExpect(status().isNoContent());
        
        verify(clubService, times(1)).deleteClub(1);
    }
    
    @Test
    void getPendingClubs_ShouldReturnPendingClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(testClubDTO);
        when(clubService.getPendingClubs()).thenReturn(clubs);
        
        // When & Then
        mockMvc.perform(get("/clubs/pending"))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).getPendingClubs();
    }
    
    @Test
    void getApprovedClubs_ShouldReturnApprovedClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(testClubDTO);
        when(clubService.getApprovedClubs()).thenReturn(clubs);
        
        // When & Then
        mockMvc.perform(get("/clubs/approved"))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).getApprovedClubs();
    }
    
    @Test
    void approveClub_ShouldApproveAndReturnClub() throws Exception {
        // Given
        when(clubService.approveClub(anyInt(), anyInt(), anyString())).thenReturn(testClubDTO);
        
        // When & Then
        mockMvc.perform(post("/clubs/1/approve")
                .param("reviewerId", "100")
                .param("comment", "Approved"))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).approveClub(anyInt(), anyInt(), anyString());
    }
    
    @Test
    void rejectClub_ShouldRejectAndReturnClub() throws Exception {
        // Given
        when(clubService.rejectClub(anyInt(), anyInt(), anyString())).thenReturn(testClubDTO);
        
        // When & Then
        mockMvc.perform(post("/clubs/1/reject")
                .param("reviewerId", "100")
                .param("comment", "Rejected"))
                .andExpect(status().isOk());
        
        verify(clubService, times(1)).rejectClub(anyInt(), anyInt(), anyString());
    }
}
