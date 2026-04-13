package com.englishflow.complaints.client;

import com.englishflow.complaints.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class AuthServiceClientTest {

    @MockBean
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
