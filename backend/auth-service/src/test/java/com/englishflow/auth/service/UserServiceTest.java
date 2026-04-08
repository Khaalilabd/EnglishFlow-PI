package com.englishflow.auth.service;

import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.UserNotFoundException;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.repository.ActivationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private GamificationIntegrationService gamificationIntegrationService;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);
        testUser.setActive(true);
        testUser.setCin("AB123456");
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole(User.Role.TUTOR);

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        // When
        List<UserDTO> users = userService.getAllUsers();

        // Then
        assertNotNull(users);
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        doNothing().when(activationTokenRepository).deleteByUserId(1L);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(activationTokenRepository, times(1)).deleteByUserId(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testActivateUser_Success() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertTrue(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
        // Verify welcome email was attempted (it's in a try-catch so it won't fail the test)
        verify(emailService, times(1)).sendWelcomeEmail(eq(testUser.getEmail()), eq(testUser.getFirstName()));
    }
}
