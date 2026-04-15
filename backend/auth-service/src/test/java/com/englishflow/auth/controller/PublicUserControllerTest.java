package com.englishflow.auth.controller;

import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicUserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PublicUserController publicUserController;

    private User student1;
    private User student2;
    private User tutor;

    @BeforeEach
    void setUp() {
        student1 = new User();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");
        student1.setEmail("john@example.com");
        student1.setRole(User.Role.STUDENT);
        student1.setProfilePhoto("photo1.jpg");

        student2 = new User();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");
        student2.setEmail("jane@example.com");
        student2.setRole(User.Role.STUDENT);

        tutor = new User();
        tutor.setId(3L);
        tutor.setFirstName("Bob");
        tutor.setLastName("Teacher");
        tutor.setEmail("bob@example.com");
        tutor.setRole(User.Role.TUTOR);
    }

    @Test
    void testGetAllStudents_Success() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(student1, student2, tutor));

        // Act
        ResponseEntity<?> response = publicUserController.getAllStudents();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) response.getBody();
        assertNotNull(students);
        assertEquals(2, students.size());
        
        Map<String, Object> firstStudent = students.get(0);
        assertEquals(1L, firstStudent.get("id"));
        assertEquals("John", firstStudent.get("firstName"));
        assertEquals("Doe", firstStudent.get("lastName"));
        assertEquals("john@example.com", firstStudent.get("email"));
        assertEquals("photo1.jpg", firstStudent.get("profilePhotoUrl"));
        
        verify(userRepository).findAll();
    }

    @Test
    void testGetAllStudents_EmptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> response = publicUserController.getAllStudents();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) response.getBody();
        assertNotNull(students);
        assertEquals(0, students.size());
    }

    @Test
    void testGetAllStudents_OnlyTutors() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(tutor));

        // Act
        ResponseEntity<?> response = publicUserController.getAllStudents();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) response.getBody();
        assertNotNull(students);
        assertEquals(0, students.size());
    }
}
