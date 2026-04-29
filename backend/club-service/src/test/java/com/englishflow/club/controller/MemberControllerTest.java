package com.englishflow.club.controller;

import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.enums.RankType;
import com.englishflow.club.security.JwtAuthenticationFilter;
import com.englishflow.club.security.JwtUtil;
import com.englishflow.club.service.MemberService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MemberService memberService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @MockBean
    private JwtUtil jwtUtil;
    
    private MemberDTO testMemberDTO;
    
    @BeforeEach
    void setUp() {
        testMemberDTO = MemberDTO.builder()
                .id(1)
                .clubId(1)
                .userId(100L)
                .rank(RankType.MEMBER)
                .userName("John Doe")
                .build();
    }
    
    @Test
    void getMembersByClub_ShouldReturnMembers() throws Exception {
        // Given
        List<MemberDTO> members = Arrays.asList(testMemberDTO);
        when(memberService.getMembersByClub(1)).thenReturn(members);
        
        // When & Then
        mockMvc.perform(get("/members/club/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(100));
        
        verify(memberService, times(1)).getMembersByClub(1);
    }
    
    @Test
    void getMembersByUser_ShouldReturnUserMemberships() throws Exception {
        // Given
        List<MemberDTO> members = Arrays.asList(testMemberDTO);
        when(memberService.getMembersByUser(100L)).thenReturn(members);
        
        // When & Then
        mockMvc.perform(get("/members/user/100"))
                .andExpect(status().isOk());
        
        verify(memberService, times(1)).getMembersByUser(100L);
    }
    
    @Test
    void getMemberByClubAndUser_WhenExists_ShouldReturnMember() throws Exception {
        // Given
        when(memberService.getMemberByClubAndUser(1, 100L)).thenReturn(Optional.of(testMemberDTO));
        
        // When & Then
        mockMvc.perform(get("/members/club/1/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100));
        
        verify(memberService, times(1)).getMemberByClubAndUser(1, 100L);
    }
    
    @Test
    void getMemberByClubAndUser_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(memberService.getMemberByClubAndUser(1, 999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/members/club/1/user/999"))
                .andExpect(status().isNotFound());
        
        verify(memberService, times(1)).getMemberByClubAndUser(1, 999L);
    }
    
    @Test
    void addMemberToClub_ShouldCreateAndReturnMember() throws Exception {
        // Given
        when(memberService.addMemberToClub(1, 100L)).thenReturn(testMemberDTO);
        
        // When & Then
        mockMvc.perform(post("/members/club/1/user/100"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(100));
        
        verify(memberService, times(1)).addMemberToClub(1, 100L);
    }
    
    @Test
    void updateMemberRank_ShouldUpdateAndReturnMember() throws Exception {
        // Given
        MemberDTO updatedMember = MemberDTO.builder()
                .id(1)
                .rank(RankType.VICE_PRESIDENT)
                .build();
        
        when(memberService.updateMemberRank(anyInt(), any(RankType.class), anyLong()))
                .thenReturn(updatedMember);
        
        // When & Then
        mockMvc.perform(put("/members/1/rank")
                .param("rank", "VICE_PRESIDENT")
                .param("requesterId", "200"))
                .andExpect(status().isOk());
        
        verify(memberService, times(1)).updateMemberRank(anyInt(), any(RankType.class), anyLong());
    }
    
    @Test
    void removeMember_ShouldRemoveMember() throws Exception {
        // Given
        doNothing().when(memberService).removeMemberFromClub(1, 200L);
        
        // When & Then
        mockMvc.perform(delete("/members/1")
                .param("requesterId", "200"))
                .andExpect(status().isNoContent());
        
        verify(memberService, times(1)).removeMemberFromClub(1, 200L);
    }
    
    @Test
    void removeMemberByUserAndClub_ShouldRemoveMember() throws Exception {
        // Given
        doNothing().when(memberService).removeMemberByUserAndClub(1, 100L);
        
        // When & Then
        mockMvc.perform(delete("/members/club/1/user/100"))
                .andExpect(status().isNoContent());
        
        verify(memberService, times(1)).removeMemberByUserAndClub(1, 100L);
    }
    
    @Test
    void getClubMemberCount_ShouldReturnCount() throws Exception {
        // Given
        when(memberService.getClubMemberCount(1)).thenReturn(15L);
        
        // When & Then
        mockMvc.perform(get("/members/club/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("15"));
        
        verify(memberService, times(1)).getClubMemberCount(1);
    }
    
    @Test
    void isPresident_ShouldReturnTrue() throws Exception {
        // Given
        when(memberService.isPresident(1, 100L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/members/club/1/user/100/is-president"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(memberService, times(1)).isPresident(1, 100L);
    }
    
    @Test
    void isMember_ShouldReturnTrue() throws Exception {
        // Given
        when(memberService.isMember(1, 100L)).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/members/club/1/user/100/is-member"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        
        verify(memberService, times(1)).isMember(1, 100L);
    }
}
