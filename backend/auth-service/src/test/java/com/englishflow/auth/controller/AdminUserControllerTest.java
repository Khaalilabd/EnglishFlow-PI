package com.englishflow.auth.controller;

import com.englishflow.auth.dto.CreateTutorRequest;
import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    private UserDTO userDTO;
    private CreateTutorRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setRole("TUTOR");
        userDTO.setActive(true);

        createRequest = new CreateTutorRequest();
        createRequest.setEmail("newtutor@example.com");
        createRequest.setFirstName("New");
        createRequest.setLastName("Tutor");
        createRequest.setPassword("Password123!");

        updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("Name");
    }

    @Test
    void testGetAllUsers_Success() {
        // Arrange
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = adminUserController.getAllUsers();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void testGetUsersByRole_Success() {
        // Arrange
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getUsersByRole("TUTOR")).thenReturn(users);

        // Act
        ResponseEntity<List<UserDTO>> response = adminUserController.getUsersByRole("TUTOR");

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userService).getUsersByRole("TUTOR");
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.getUserById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(userService).getUserById(1L);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        when(userService.createUser(any(CreateTutorRequest.class))).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.createUser(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService).createUser(any(CreateTutorRequest.class));
    }

    @Test
    void testUpdateUser_Success() {
        // Arrange
        when(userService.updateUserByAdmin(eq(1L), any(UpdateUserRequest.class)))
            .thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.updateUser(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService).updateUserByAdmin(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void testDeleteUser_Success() {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act
        ResponseEntity<Void> response = adminUserController.deleteUser(1L);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(userService).deleteUser(1L);
    }

    @Test
    void testToggleUserStatus_Success() {
        // Arrange
        when(userService.toggleUserStatus(1L)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.toggleUserStatus(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService).toggleUserStatus(1L);
    }

    @Test
    void testActivateUser_Success() {
        // Arrange
        when(userService.activateUser(1L)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.activateUser(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService).activateUser(1L);
    }

    @Test
    void testDeactivateUser_Success() {
        // Arrange
        when(userService.deactivateUser(1L)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserDTO> response = adminUserController.deactivateUser(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(userService).deactivateUser(1L);
    }
}
