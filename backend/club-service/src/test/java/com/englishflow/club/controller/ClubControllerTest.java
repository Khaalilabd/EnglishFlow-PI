package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.service.ClubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClubService clubService;

    private ClubDTO clubDTO;

    @BeforeEach
    void setUp() {
        clubDTO = new ClubDTO();
        clubDTO.setId(1L);
        clubDTO.setName("English Club");
        clubDTO.setDescription("Learn English together");
        clubDTO.setCategory("LANGUAGE");
        clubDTO.setMaxMembers(50);
    }

    @Test
    @WithMockUser
    void getAllClubs_ShouldReturnClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(clubDTO);
        when(clubService.getAllClubs()).thenReturn(clubs);

        // When & Then
        mockMvc.perform(get("/clubs")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("English Club"));
    }

    @Test
    @WithMockUser
    void getClubsByCategory_ShouldReturnFilteredClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(clubDTO);
        when(clubService.getClubsByCategory("LANGUAGE")).thenReturn(clubs);

        // When & Then
        mockMvc.perform(get("/clubs/category/LANGUAGE")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("LANGUAGE"));
    }

    @Test
    @WithMockUser
    void searchClubs_ShouldReturnMatchingClubs() throws Exception {
        // Given
        List<ClubDTO> clubs = Arrays.asList(clubDTO);
        when(clubService.searchClubs("English")).thenReturn(clubs);

        // When & Then
        mockMvc.perform(get("/clubs/search")
                .param("query", "English")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("English Club"));
    }

    @Test
    @WithMockUser
    void getClubById_ShouldReturnClub() throws Exception {
        // Given
        when(clubService.getClubById(1L)).thenReturn(clubDTO);

        // When & Then
        mockMvc.perform(get("/clubs/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("English Club"));
    }
}
