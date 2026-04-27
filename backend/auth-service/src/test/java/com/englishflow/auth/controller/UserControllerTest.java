package com.englishflow.auth.controller;

import com.englishflow.auth.dto.UpdateUserRequest;
import com.englishflow.auth.dto.UserDTO;
import com.englishflow.auth.dto.AuthResponse;
import com.englishflow.auth.dto.UserIdsRequest;
import com.englishflow.auth.dto.UserDetailsResponse;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.UserRepository;
import com.englishflow.auth.service.FileStorageService;
import com.englishflow.auth.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

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
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        UserDTO userDTO = UserDTO.fromEntity(testUser);
        Page<UserDTO> page = new PageImpl<>(Arrays.asList(userDTO));
        when(userService.getAllUsersPaginated(any(Pageable.class))).thenReturn(page);

        // Act
        ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(0, 20, "id", "DESC");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(userService).getAllUsersPaginated(any(Pageable.class));
    }

    @Test
    void getAllUsers_Exception() {
        // Arrange
        when(userService.getAllUsersPaginated(any(Pageable.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Page<UserDTO>> response = userController.getAllUsers(0, 20, "id", "DESC");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getStudentsForMessaging_Success() {
        // Arrange
        when(userRepository.findByRoleAndIsActive(User.Role.STUDENT, true))
            .thenReturn(Arrays.asList(testUser));

        // Act
        ResponseEntity<List<java.util.Map<String, Object>>> response = userController.getStudentsForMessaging();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateUser_Success() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<AuthResponse> response = userController.updateUser(1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    void getUsersByRole_Success() {
        // Arrange
        when(userRepository.findByRole(User.Role.STUDENT)).thenReturn(Arrays.asList(testUser));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByRole("STUDENT");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUsersByRole_InvalidRole() {
        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByRole("INVALID_ROLE");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getPublicTutors_Success() {
        // Arrange
        testUser.setRole(User.Role.TUTOR);
        when(userRepository.findByRoleAndIsActive(User.Role.TUTOR, true))
            .thenReturn(Arrays.asList(testUser));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getPublicTutors();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUsersByRolePublic_Success() {
        // Arrange
        when(userRepository.findByRoleAndIsActive(User.Role.STUDENT, true))
            .thenReturn(Arrays.asList(testUser));

        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByRolePublic("STUDENT");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getUserByIdPublic_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        ResponseEntity<UserDTO> response = userController.getUserByIdPublic(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void deleteProfilePhoto_Success() {
        // Arrange
        testUser.setProfilePhoto("/uploads/photo.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        doNothing().when(fileStorageService).deleteFile(anyString());

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteProfilePhoto(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileStorageService).deleteFile(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteProfilePhoto_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteProfilePhoto(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void deleteProfilePhoto_NoPhoto() {
        // Arrange
        testUser.setProfilePhoto(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteProfilePhoto(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_UserNotFound() {
        // Arrange
        UpdateUserRequest request = new UpdateUserRequest();
        request.setFirstName("Jane");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(com.englishflow.auth.exception.UserNotFoundException.class, () -> {
            userController.updateUser(1L, request);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(com.englishflow.auth.exception.UserNotFoundException.class, () -> {
            userController.getUserById(1L);
        });
    }

    @Test
    void getUserByIdPublic_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(com.englishflow.auth.exception.UserNotFoundException.class, () -> {
            userController.getUserByIdPublic(1L);
        });
    }

    @Test
    void getUsersByRolePublic_InvalidRole() {
        // Act
        ResponseEntity<List<UserDTO>> response = userController.getUsersByRolePublic("INVALID_ROLE");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUsersByIds_Success() {
        // Arrange
        UserIdsRequest request = new UserIdsRequest();
        request.setUserIds(Arrays.asList(1L, 2L));
        
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole(User.Role.TUTOR);
        
        when(userRepository.findAllById(Arrays.asList(1L, 2L)))
            .thenReturn(Arrays.asList(testUser, user2));

        // Act
        ResponseEntity<List<UserDetailsResponse>> response = userController.getUsersByIds(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userRepository).findAllById(Arrays.asList(1L, 2L));
    }

    @Test
    void getUsersByIds_EmptyList() {
        // Arrange
        UserIdsRequest request = new UserIdsRequest();
        request.setUserIds(Arrays.asList());

        // Act
        ResponseEntity<List<UserDetailsResponse>> response = userController.getUsersByIds(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void getUsersByIds_NullList() {
        // Arrange
        UserIdsRequest request = new UserIdsRequest();
        request.setUserIds(null);

        // Act
        ResponseEntity<List<UserDetailsResponse>> response = userController.getUsersByIds(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void getUserDocuments_Success() {
        // Arrange
        com.englishflow.auth.repository.ProfessionalDocumentRepository docRepo = 
            mock(com.englishflow.auth.repository.ProfessionalDocumentRepository.class);
        com.englishflow.auth.entity.ProfessionalDocument doc = new com.englishflow.auth.entity.ProfessionalDocument();
        doc.setId(1L);
        doc.setFileName("cv.pdf");
        doc.setFilePath("/uploads/cv.pdf");
        doc.setDocumentType("CV");
        doc.setFileSize(1024L);
        doc.setUploadedAt(java.time.LocalDateTime.now());
        
        when(userService.getProfessionalDocumentRepository()).thenReturn(docRepo);
        when(docRepo.findByUserId(1L)).thenReturn(Arrays.asList(doc));

        // Act
        ResponseEntity<List<java.util.Map<String, Object>>> response = userController.getUserDocuments(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(docRepo).findByUserId(1L);
    }

    @Test
    void getUserDocuments_Exception() {
        // Arrange
        when(userService.getProfessionalDocumentRepository()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<List<java.util.Map<String, Object>>> response = userController.getUserDocuments(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void deleteDocument_Success() {
        // Arrange
        com.englishflow.auth.repository.ProfessionalDocumentRepository docRepo = 
            mock(com.englishflow.auth.repository.ProfessionalDocumentRepository.class);
        com.englishflow.auth.entity.ProfessionalDocument doc = new com.englishflow.auth.entity.ProfessionalDocument();
        doc.setId(1L);
        doc.setFilePath("/uploads/cv.pdf");
        
        when(userService.getProfessionalDocumentRepository()).thenReturn(docRepo);
        when(docRepo.findById(1L)).thenReturn(Optional.of(doc));
        doNothing().when(fileStorageService).deleteFile(anyString());
        doNothing().when(docRepo).delete(any());

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteDocument(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("message"));
        verify(fileStorageService).deleteFile("/uploads/cv.pdf");
        verify(docRepo).delete(doc);
    }

    @Test
    void deleteDocument_NotFound() {
        // Arrange
        com.englishflow.auth.repository.ProfessionalDocumentRepository docRepo = 
            mock(com.englishflow.auth.repository.ProfessionalDocumentRepository.class);
        
        when(userService.getProfessionalDocumentRepository()).thenReturn(docRepo);
        when(docRepo.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteDocument(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void deleteDocument_Exception() {
        // Arrange
        when(userService.getProfessionalDocumentRepository()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.deleteDocument(1L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void uploadProfilePhoto_Success() throws Exception {
        // Arrange
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.isValidImageFile(file)).thenReturn(true);
        when(fileStorageService.isValidFileSize(file, 5 * 1024 * 1024)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fileStorageService.storeFile(file)).thenReturn("/uploads/photo.jpg");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.uploadProfilePhoto(1L, file);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("profilePhoto"));
        verify(fileStorageService).storeFile(file);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void uploadProfilePhoto_EmptyFile() throws Exception {
        // Arrange
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.uploadProfilePhoto(1L, file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    void uploadProfilePhoto_InvalidImageType() throws Exception {
        // Arrange
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.isValidImageFile(file)).thenReturn(false);

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.uploadProfilePhoto(1L, file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        verify(fileStorageService, never()).storeFile(any());
    }

    @Test
    void uploadProfilePhoto_FileTooLarge() throws Exception {
        // Arrange
        org.springframework.web.multipart.MultipartFile file = mock(org.springframework.web.multipart.MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(fileStorageService.isValidImageFile(file)).thenReturn(true);
        when(fileStorageService.isValidFileSize(file, 5 * 1024 * 1024)).thenReturn(false);

        // Act
        ResponseEntity<java.util.Map<String, String>> response = userController.uploadProfilePhoto(1L, file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
        verify(fileStorageService, never()).storeFile(any());
    }
}
