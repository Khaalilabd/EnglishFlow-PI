package com.englishflow.community.controller;

import com.englishflow.community.security.JwtAuthenticationFilter;
import com.englishflow.community.security.InternalServiceAuthenticationFilter;
import com.englishflow.community.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PermissionController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class, InternalServiceAuthenticationFilter.class}))
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PermissionService permissionService;

    @Test
    @WithMockUser
    void getPermissions_ShouldReturnPermissionInfo() throws Exception {
        PermissionService.PermissionInfo permissionInfo = 
                new PermissionService.PermissionInfo(true, true, false, false);

        when(permissionService.getPermissionInfo(1L, "STUDENT")).thenReturn(permissionInfo);

        mockMvc.perform(get("/community/permissions/subcategory/1")
                        .with(csrf())
                        .param("userRole", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canCreateTopic").value(true))
                .andExpect(jsonPath("$.canReply").value(true))
                .andExpect(jsonPath("$.categoryLocked").value(false))
                .andExpect(jsonPath("$.subCategoryLocked").value(false));

        verify(permissionService).getPermissionInfo(1L, "STUDENT");
    }

    @Test
    @WithMockUser
    void getPermissions_LockedCategory_ShouldReturnRestrictedPermissions() throws Exception {
        PermissionService.PermissionInfo permissionInfo = 
                new PermissionService.PermissionInfo(false, false, true, false);

        when(permissionService.getPermissionInfo(1L, "STUDENT")).thenReturn(permissionInfo);

        mockMvc.perform(get("/community/permissions/subcategory/1")
                        .with(csrf())
                        .param("userRole", "STUDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canCreateTopic").value(false))
                .andExpect(jsonPath("$.canReply").value(false))
                .andExpect(jsonPath("$.categoryLocked").value(true));

        verify(permissionService).getPermissionInfo(1L, "STUDENT");
    }

    @Test
    @WithMockUser
    void getPermissions_AdminRole_ShouldReturnFullPermissions() throws Exception {
        PermissionService.PermissionInfo permissionInfo = 
                new PermissionService.PermissionInfo(true, true, false, false);

        when(permissionService.getPermissionInfo(1L, "ADMIN")).thenReturn(permissionInfo);

        mockMvc.perform(get("/community/permissions/subcategory/1")
                        .with(csrf())
                        .param("userRole", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.canCreateTopic").value(true))
                .andExpect(jsonPath("$.canReply").value(true));

        verify(permissionService).getPermissionInfo(1L, "ADMIN");
    }
}
