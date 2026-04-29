package com.englishflow.courses.controller;

import com.englishflow.courses.dto.PackDTO;
import com.englishflow.courses.enums.PackStatus;
import com.englishflow.courses.service.IPackService;
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

@WebMvcTest(PackController.class)
@AutoConfigureMockMvc(addFilters = false)
class PackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IPackService packService;

    private PackDTO packDTO;

    @BeforeEach
    void setUp() {
        packDTO = new PackDTO();
        packDTO.setId(1L);
        packDTO.setName("English Pack");
        packDTO.setDescription("Complete English Pack");
        packDTO.setCategory("Language");
        packDTO.setLevel("Beginner");
        packDTO.setStatus(PackStatus.ACTIVE);
        packDTO.setTutorId(1L);
    }

    @Test
    void createPack_ShouldReturnCreated() throws Exception {
        when(packService.createPack(any(PackDTO.class))).thenReturn(packDTO);

        mockMvc.perform(post("/packs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(packDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("English Pack"));

        verify(packService, times(1)).createPack(any(PackDTO.class));
    }

    @Test
    void updatePack_ShouldReturnUpdatedPack() throws Exception {
        when(packService.updatePack(eq(1L), any(PackDTO.class))).thenReturn(packDTO);

        mockMvc.perform(put("/packs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(packDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(packService, times(1)).updatePack(eq(1L), any(PackDTO.class));
    }

    @Test
    void getById_ShouldReturnPack() throws Exception {
        when(packService.getById(1L)).thenReturn(packDTO);

        mockMvc.perform(get("/packs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("English Pack"));

        verify(packService, times(1)).getById(1L);
    }

    @Test
    void getAllPacks_ShouldReturnAllPacks() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getAllPacks()).thenReturn(packs);

        mockMvc.perform(get("/packs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(packService, times(1)).getAllPacks();
    }

    @Test
    void getByTutorId_ShouldReturnPacksByTutor() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getByTutorId(1L)).thenReturn(packs);

        mockMvc.perform(get("/packs/tutor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tutorId").value(1));

        verify(packService, times(1)).getByTutorId(1L);
    }

    @Test
    void getByStatus_ShouldReturnPacksByStatus() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getByStatus(PackStatus.ACTIVE)).thenReturn(packs);

        mockMvc.perform(get("/packs/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));

        verify(packService, times(1)).getByStatus(PackStatus.ACTIVE);
    }

    @Test
    void searchPacks_ShouldReturnPacksByCategoryAndLevel() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getByCategoryAndLevel("Language", "Beginner")).thenReturn(packs);

        mockMvc.perform(get("/packs/search")
                .param("category", "Language")
                .param("level", "Beginner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Language"));

        verify(packService, times(1)).getByCategoryAndLevel("Language", "Beginner");
    }

    @Test
    void getAvailablePacks_WithCategoryAndLevel_ShouldReturnFilteredPacks() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getAvailablePacksByCategoryAndLevel("Language", "Beginner")).thenReturn(packs);

        mockMvc.perform(get("/packs/available")
                .param("category", "Language")
                .param("level", "Beginner"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(packService, times(1)).getAvailablePacksByCategoryAndLevel("Language", "Beginner");
    }

    @Test
    void getAvailablePacks_WithoutFilters_ShouldReturnAllAvailablePacks() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getAllAvailablePacks()).thenReturn(packs);

        mockMvc.perform(get("/packs/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(packService, times(1)).getAllAvailablePacks();
    }

    @Test
    void getByCreatedBy_ShouldReturnPacksByAcademic() throws Exception {
        List<PackDTO> packs = Arrays.asList(packDTO);
        when(packService.getByCreatedBy(1L)).thenReturn(packs);

        mockMvc.perform(get("/packs/academic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(packService, times(1)).getByCreatedBy(1L);
    }

    @Test
    void deletePack_ShouldReturnNoContent() throws Exception {
        doNothing().when(packService).deletePack(1L);

        mockMvc.perform(delete("/packs/1"))
                .andExpect(status().isNoContent());

        verify(packService, times(1)).deletePack(1L);
    }
}
