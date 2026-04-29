package com.englishflow.club.controller;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.security.JwtAuthenticationFilter;
import com.englishflow.club.security.JwtUtil;
import com.englishflow.club.service.SkillService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SkillController.class)
@AutoConfigureMockMvc(addFilters = false)
class SkillControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private SkillService skillService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private SkillDTO testSkillDTO;
    
    @BeforeEach
    void setUp() {
        testSkillDTO = SkillDTO.builder()
                .id(1)
                .name("Java Programming")
                .description("Advanced Java skills")
                .clubId(1)
                .build();
    }
    
    @Test
    void getSkillsByClub_ShouldReturnSkills() throws Exception {
        // Given
        List<SkillDTO> skills = Arrays.asList(testSkillDTO);
        when(skillService.getSkillsByClubId(1)).thenReturn(skills);
        
        // When & Then
        mockMvc.perform(get("/clubs/1/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java Programming"));
        
        verify(skillService, times(1)).getSkillsByClubId(1);
    }
    
    @Test
    void addSkillToClub_ShouldCreateSkill() throws Exception {
        // Given
        when(skillService.addSkillToClub(anyInt(), any(SkillDTO.class))).thenReturn(testSkillDTO);
        
        // When & Then
        mockMvc.perform(post("/clubs/1/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSkillDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Java Programming"));
        
        verify(skillService, times(1)).addSkillToClub(anyInt(), any(SkillDTO.class));
    }
    
    @Test
    void updateClubSkills_ShouldUpdateSkills() throws Exception {
        // Given
        List<SkillDTO> skills = Arrays.asList(testSkillDTO);
        doNothing().when(skillService).updateClubSkills(anyInt(), anyList());
        
        // When & Then
        mockMvc.perform(put("/clubs/1/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(skills)))
                .andExpect(status().isOk());
        
        verify(skillService, times(1)).updateClubSkills(anyInt(), anyList());
    }
    
    @Test
    void deleteSkill_ShouldDeleteSkill() throws Exception {
        // Given
        doNothing().when(skillService).deleteSkill(1);
        
        // When & Then
        mockMvc.perform(delete("/clubs/1/skills/1"))
                .andExpect(status().isNoContent());
        
        verify(skillService, times(1)).deleteSkill(1);
    }
}
