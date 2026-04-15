package com.englishflow.club.controller;

import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    private MemberDTO memberDTO;
    private ClubWithRoleDTO clubWithRoleDTO;

    @BeforeEach
    void setUp() {
        memberDTO = new MemberDTO();
        memberDTO.setId(1L);
        memberDTO.setUserId(100L);
        memberDTO.setUserName("John Doe");
        memberDTO.setRank("MEMBER");

        clubWithRoleDTO = new ClubWithRoleDTO();
        clubWithRoleDTO.setClubId(1L);
        clubWithRoleDTO.setClubName("English Club");
        clubWithRoleDTO.setRole("MEMBER");
    }

    @Test
    @WithMockUser
    void getClubMembers_ShouldReturnMembers() throws Exception {
        // Given
        List<MemberDTO> members = Arrays.asList(memberDTO);
        when(memberService.getClubMembers(1L)).thenReturn(members);

        // When & Then
        mockMvc.perform(get("/members/club/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(100))
                .andExpect(jsonPath("$[0].userName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getUserClubs_ShouldReturnUserClubs() throws Exception {
        // Given
        List<ClubWithRoleDTO> clubs = Arrays.asList(clubWithRoleDTO);
        when(memberService.getUserClubs(100L)).thenReturn(clubs);

        // When & Then
        mockMvc.perform(get("/members/user/100")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clubId").value(1))
                .andExpect(jsonPath("$[0].clubName").value("English Club"));
    }

    @Test
    @WithMockUser
    void getUserClubsWithStatus_ShouldReturnClubsWithStatus() throws Exception {
        // Given
        List<ClubWithRoleDTO> clubs = Arrays.asList(clubWithRoleDTO);
        when(memberService.getUserClubsWithStatus(100L)).thenReturn(clubs);

        // When & Then
        mockMvc.perform(get("/members/user/100/clubs-with-status")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("MEMBER"));
    }

    @Test
    @WithMockUser
    void getMemberByClubAndUser_ShouldReturnMember() throws Exception {
        // Given
        when(memberService.getMemberByClubAndUser(1L, 100L)).thenReturn(memberDTO);

        // When & Then
        mockMvc.perform(get("/members/club/1/user/100")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(100));
    }
}
