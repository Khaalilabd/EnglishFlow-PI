package com.englishflow.auth.service;

import com.englishflow.auth.dto.CreateTutorRequest;
import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.dto.UserLevelDTO;
import com.englishflow.auth.entity.ActivationToken;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.exception.EmailAlreadyExistsException;
import com.englishflow.auth.exception.UserNotFoundException;
import com.englishflow.auth.repository.ActivationTokenRepository;
import com.englishflow.auth.repository.ProfessionalDocumentRepository;
import com.englishflow.auth.repository.TutorApplicationRepository;
import com.englishflow.auth.repository.UserRepository;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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

    @Mock
    private ProfessionalDocumentRepository professionalDocumentRepository;

    @Mock
    private TutorApplicationRepository tutorApplicationRepository;

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

    @Test
    void testGetUsersByRole_Success() {
        // Given
        when(userRepository.findByRole(User.Role.STUDENT)).thenReturn(Arrays.asList(testUser));

        // When
        List<UserDTO> result = userService.getUsersByRole("STUDENT");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("STUDENT", result.get(0).getRole());
    }

    @Test
    void testDeactivateUser_Success() {
        // Given
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.deactivateUser(1L);

        // Then
        assertNotNull(result);
        assertFalse(testUser.isActive());
        verify(userRepository).save(testUser);
    }

    @Test
    void testEncodePassword_Success() {
        // Given
        String rawPassword = "password123";
        when(passwordEncoder.encode(rawPassword)).thenReturn("encodedPassword");

        // When
        String result = userService.encodePassword(rawPassword);

        // Then
        assertEquals("encodedPassword", result);
        verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    void testGetAllUsersPaginated_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(userRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<UserDTO> result = userService.getAllUsersPaginated(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUserById_WithGamificationLevel_Success() {
        // Given
        UserLevelDTO levelDTO = UserLevelDTO.builder()
                .userId(1L)
                .currentXP(1500)
                .totalXP(2000)
                .assessmentLevel("B2")
                .jungleCoins(100)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gamificationIntegrationService.getUserLevel(1L)).thenReturn(levelDTO);

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertNotNull(result.getGamificationLevel());
        assertEquals(1500, result.getGamificationLevel().getCurrentXP());
        verify(gamificationIntegrationService, times(1)).getUserLevel(1L);
    }

    @Test
    void testGetUserById_GamificationFails_StillReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gamificationIntegrationService.getUserLevel(1L)).thenThrow(new RuntimeException("Service unavailable"));

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertNull(result.getGamificationLevel());
        verify(gamificationIntegrationService, times(1)).getUserLevel(1L);
    }

    @Test
    void testGetUsersByRole_InvalidRole_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.getUsersByRole("INVALID_ROLE");
        });
    }

    @Test
    void testUpdateUser_PartialUpdate_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO updateDTO = new UserDTO();
        updateDTO.setFirstName("UpdatedName");
        updateDTO.setPhone("+1234567890");

        // When
        UserDTO result = userService.updateUser(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
        assertEquals("UpdatedName", testUser.getFirstName());
    }

    @Test
    void testUpdateUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(999L, new UserDTO());
        });
    }

    @Test
    void testUpdateUserByAdmin_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("AdminUpdated");
        request.setEmail("newemail@example.com");
        request.setCity("New City");

        // When
        UserDTO result = userService.updateUserByAdmin(1L, request);

        // Then
        assertNotNull(result);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeleteUser_NotFound_ThrowsException() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testToggleUserStatus_ActiveToInactive_Success() {
        // Given
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.toggleUserStatus(1L);

        // Then
        assertNotNull(result);
        assertFalse(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testToggleUserStatus_InactiveToActive_Success() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.toggleUserStatus(1L);

        // Then
        assertNotNull(result);
        assertTrue(testUser.isActive());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testCreateTutor_Success() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("tutor@example.com");
        request.setPassword("password123");
        request.setFirstName("John");
        request.setLastName("Tutor");
        request.setPhone("+1234567890");
        request.setCin("TU123456");
        request.setYearsOfExperience(5);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // When
        UserDTO result = userService.createTutor(request);

        // Then
        assertNotNull(result);
        assertEquals("tutor@example.com", result.getEmail());
        assertEquals("TUTOR", result.getRole());
        assertTrue(result.isActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateTutor_EmailAlreadyExists_ThrowsException() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createTutor(request);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testCreateTutor_MissingEmail_ThrowsException() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setPassword("password123");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createTutor(request);
        });
    }

    @Test
    void testCreateTutor_MissingPassword_ThrowsException() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("tutor@example.com");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createTutor(request);
        });
    }

    @Test
    void testCreateTutor_CinAlreadyExists_ThrowsException() {
        // Given
        User existingUser = new User();
        existingUser.setCin("AB123456");

        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("tutor@example.com");
        request.setPassword("password123");
        request.setCin("AB123456");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.findAll()).thenReturn(Arrays.asList(existingUser));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createTutor(request);
        });
    }

    @Test
    void testCreateUser_Success() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("student@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Student");
        request.setRole("STUDENT");
        request.setEnglishLevel("B1");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserDTO result = userService.createUser(request);

        // Then
        assertNotNull(result);
        assertEquals("student@example.com", result.getEmail());
        assertEquals("STUDENT", result.getRole());
        assertFalse(result.isActive()); // Should require activation
        verify(activationTokenRepository, times(1)).save(any(ActivationToken.class));
    }

    @Test
    void testCreateUser_MissingRole_ThrowsException() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUser_InvalidRole_ThrowsException() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        request.setRole("INVALID_ROLE");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUser_EmailSendFails_StillCreatesUser() {
        // Given
        CreateTutorRequest request = new CreateTutorRequest();
        request.setEmail("student@example.com");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setRole("STUDENT");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });
        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserDTO result = userService.createUser(request);

        // Then
        assertNotNull(result);
        assertEquals("student@example.com", result.getEmail());
    }

    @Test
    void testActivateUser_WasInactive_SendsWelcomeEmail() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(emailService.sendWelcomeEmail(anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertTrue(testUser.isActive());
        verify(emailService, times(1)).sendWelcomeEmail(eq(testUser.getEmail()), eq(testUser.getFirstName()));
    }

    @Test
    void testActivateUser_AlreadyActive_NoWelcomeEmail() {
        // Given
        testUser.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertTrue(testUser.isActive());
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    void testActivateUser_EmailFails_StillActivates() {
        // Given
        testUser.setActive(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(emailService.sendWelcomeEmail(anyString(), anyString()))
                .thenReturn(java.util.concurrent.CompletableFuture.failedFuture(new RuntimeException("Email service down")));

        // When
        UserDTO result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertTrue(testUser.isActive());
        verify(emailService, times(1)).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    void testGetProfessionalDocumentRepository_ReturnsRepository() {
        // When
        var result = userService.getProfessionalDocumentRepository();

        // Then
        assertNotNull(result);
        assertEquals(professionalDocumentRepository, result);
    }

    @Test
    void testGetTutorApplicationRepository_ReturnsRepository() {
        // When
        var result = userService.getTutorApplicationRepository();

        // Then
        assertNotNull(result);
        assertEquals(tutorApplicationRepository, result);
    }
}
