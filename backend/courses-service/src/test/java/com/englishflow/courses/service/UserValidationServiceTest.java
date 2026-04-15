package com.englishflow.courses.service;

import com.englishflow.courses.client.AuthServiceClient;
import com.englishflow.courses.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private UserValidationService userValidationService;

    private UserDTO tutorUser;
    private UserDTO studentUser;
    private Long tutorId = 1L;
    private Long studentId = 2L;

    @BeforeEach
    void setUp() {
        tutorUser = new UserDTO();
        tutorUser.setId(tutorId);
        tutorUser.setUsername("tutor1");
        tutorUser.setRoles(new HashSet<>(Arrays.asList("TUTOR")));

        studentUser = new UserDTO();
        studentUser.setId(studentId);
        studentUser.setUsername("student1");
        studentUser.setRoles(new HashSet<>(Arrays.asList("STUDENT")));
    }

    @Test
    void validateTutorExists_WhenValidTutor_ShouldPass() {
        // Given
        when(authServiceClient.getUserById(tutorId)).thenReturn(tutorUser);

        // When & Then
        assertDoesNotThrow(() -> userValidationService.validateTutorExists(tutorId));
        verify(authServiceClient).getUserById(tutorId);
    }

    @Test
    void validateTutorExists_WhenNotTutor_ShouldThrowException() {
        // Given
        when(authServiceClient.getUserById(studentId)).thenReturn(studentUser);

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> userValidationService.validateTutorExists(studentId));
    }

    @Test
    void validateTutorExists_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(authServiceClient.getUserById(tutorId))
            .thenThrow(new RuntimeException("User not found"));

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> userValidationService.validateTutorExists(tutorId));
    }

    @Test
    void validateStudentExists_WhenValidStudent_ShouldPass() {
        // Given
        when(authServiceClient.getUserById(studentId)).thenReturn(studentUser);

        // When & Then
        assertDoesNotThrow(() -> userValidationService.validateStudentExists(studentId));
        verify(authServiceClient).getUserById(studentId);
    }

    @Test
    void validateStudentExists_WhenNotStudent_ShouldThrowException() {
        // Given
        when(authServiceClient.getUserById(tutorId)).thenReturn(tutorUser);

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> userValidationService.validateStudentExists(tutorId));
    }

    @Test
    void isTutor_WhenTutor_ShouldReturnTrue() {
        // Given
        when(authServiceClient.getUserById(tutorId)).thenReturn(tutorUser);

        // When
        boolean result = userValidationService.isTutor(tutorId);

        // Then
        assertTrue(result);
    }

    @Test
    void isTutor_WhenNotTutor_ShouldReturnFalse() {
        // Given
        when(authServiceClient.getUserById(studentId)).thenReturn(studentUser);

        // When
        boolean result = userValidationService.isTutor(studentId);

        // Then
        assertFalse(result);
    }

    @Test
    void isStudent_WhenStudent_ShouldReturnTrue() {
        // Given
        when(authServiceClient.getUserById(studentId)).thenReturn(studentUser);

        // When
        boolean result = userValidationService.isStudent(studentId);

        // Then
        assertTrue(result);
    }

    @Test
    void isStudent_WhenNotStudent_ShouldReturnFalse() {
        // Given
        when(authServiceClient.getUserById(tutorId)).thenReturn(tutorUser);

        // When
        boolean result = userValidationService.isStudent(tutorId);

        // Then
        assertFalse(result);
    }

    @Test
    void hasRole_WhenHasRole_ShouldReturnTrue() {
        // Given
        when(authServiceClient.getUserById(tutorId)).thenReturn(tutorUser);

        // When
        boolean result = userValidationService.hasRole(tutorId, "TUTOR");

        // Then
        assertTrue(result);
    }

    @Test
    void hasRole_WhenDoesNotHaveRole_ShouldReturnFalse() {
        // Given
        when(authServiceClient.getUserById(studentId)).thenReturn(studentUser);

        // When
        boolean result = userValidationService.hasRole(studentId, "TUTOR");

        // Then
        assertFalse(result);
    }
}
