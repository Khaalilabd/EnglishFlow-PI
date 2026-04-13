package com.englishflow.community.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
    }

    @Test
    void canModifyResource_AdminRole_ReturnsTrue() {
        // Arrange
        Long userId = 1L;
        String userRole = "ADMIN";
        Long resourceOwnerId = 2L;

        // Act
        boolean canModify = securityService.canModifyResource(userId, userRole, resourceOwnerId);

        // Assert
        assertTrue(canModify);
    }

    @Test
    void canModifyResource_OwnerUser_ReturnsTrue() {
        // Arrange
        Long userId = 1L;
        String userRole = "STUDENT";
        Long resourceOwnerId = 1L;

        // Act
        boolean canModify = securityService.canModifyResource(userId, userRole, resourceOwnerId);

        // Assert
        assertTrue(canModify);
    }

    @Test
    void canModifyResource_DifferentUser_ReturnsFalse() {
        // Arrange
        Long userId = 1L;
        String userRole = "STUDENT";
        Long resourceOwnerId = 2L;

        // Act
        boolean canModify = securityService.canModifyResource(userId, userRole, resourceOwnerId);

        // Assert
        assertFalse(canModify);
    }

    @Test
    void canModifyResource_NullUserId_ReturnsFalse() {
        // Arrange
        Long userId = null;
        String userRole = "STUDENT";
        Long resourceOwnerId = 1L;

        // Act
        boolean canModify = securityService.canModifyResource(userId, userRole, resourceOwnerId);

        // Assert
        assertFalse(canModify);
    }

    @Test
    void canDeleteResource_AdminRole_ReturnsTrue() {
        // Arrange
        Long userId = 1L;
        String userRole = "ADMIN";
        Long resourceOwnerId = 2L;

        // Act
        boolean canDelete = securityService.canDeleteResource(userId, userRole, resourceOwnerId);

        // Assert
        assertTrue(canDelete);
    }

    @Test
    void isAdmin_AdminRole_ReturnsTrue() {
        // Act & Assert
        assertTrue(securityService.isAdmin("ADMIN"));
        assertFalse(securityService.isAdmin("TEACHER"));
        assertFalse(securityService.isAdmin("STUDENT"));
    }

    @Test
    void isTeacher_TeacherRole_ReturnsTrue() {
        // Act & Assert
        assertTrue(securityService.isTeacher("TEACHER"));
        assertFalse(securityService.isTeacher("ADMIN"));
        assertFalse(securityService.isTeacher("STUDENT"));
    }

    @Test
    void isStudent_StudentRole_ReturnsTrue() {
        // Act & Assert
        assertTrue(securityService.isStudent("STUDENT"));
        assertFalse(securityService.isStudent("ADMIN"));
        assertFalse(securityService.isStudent("TEACHER"));
    }

    @Test
    void canModerate_AdminOrTeacher_ReturnsTrue() {
        // Act & Assert
        assertTrue(securityService.canModerate("ADMIN"));
        assertTrue(securityService.canModerate("TEACHER"));
        assertFalse(securityService.canModerate("STUDENT"));
    }
}
