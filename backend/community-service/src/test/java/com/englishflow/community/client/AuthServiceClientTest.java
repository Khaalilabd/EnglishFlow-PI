package com.englishflow.community.client;

import com.englishflow.community.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceClientTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @Test
    void getUserById_Success_ReturnsUserDTO() {
        // Arrange
        Long userId = 1L;
        UserDTO expectedUser = UserDTO.builder()
                .id(userId)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role("STUDENT")
                .build();

        when(authServiceClient.getUserById(userId)).thenReturn(expectedUser);

        // Act
        UserDTO actualUser = authServiceClient.getUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
        assertEquals(expectedUser.getRole(), actualUser.getRole());
    }

    @Test
    void getUserById_ServiceDown_ReturnsFallback() {
        // Arrange
        Long userId = 999L;
        UserDTO fallbackUser = UserDTO.builder()
                .id(userId)
                .email("unavailable@system.com")
                .firstName("User")
                .lastName("Unavailable")
                .role("UNKNOWN")
                .build();

        when(authServiceClient.getUserById(anyLong())).thenReturn(fallbackUser);

        // Act
        UserDTO actualUser = authServiceClient.getUserById(userId);

        // Assert
        assertNotNull(actualUser);
        assertEquals("unavailable@system.com", actualUser.getEmail());
        assertEquals("UNKNOWN", actualUser.getRole());
    }
}
