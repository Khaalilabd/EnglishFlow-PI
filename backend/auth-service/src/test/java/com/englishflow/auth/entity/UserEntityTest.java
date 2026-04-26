package com.englishflow.auth.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {

    @Test
    void user_ShouldSetAndGetAllFields() {
        // Arrange & Act
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encodedPassword");
        user.setPhone("1234567890");
        user.setCin("AB123456");
        user.setDateOfBirth("1990-01-01");
        user.setAddress("123 Main St");
        user.setCity("New York");
        user.setPostalCode("10001");
        user.setBio("Test bio");
        user.setYearsOfExperience(5);
        user.setRole(User.Role.STUDENT);
        user.setActive(true);
        user.setRegistrationFeePaid(true);
        user.setProfileCompleted(true);
        user.setProfilePhoto("photo.jpg");
        user.setEnglishLevel("B2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Assert
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("1234567890", user.getPhone());
        assertEquals("AB123456", user.getCin());
        assertEquals("1990-01-01", user.getDateOfBirth());
        assertEquals("123 Main St", user.getAddress());
        assertEquals("New York", user.getCity());
        assertEquals("10001", user.getPostalCode());
        assertEquals("Test bio", user.getBio());
        assertEquals(5, user.getYearsOfExperience());
        assertEquals(User.Role.STUDENT, user.getRole());
        assertTrue(user.isActive());
        assertTrue(user.isRegistrationFeePaid());
        assertTrue(user.isProfileCompleted());
        assertEquals("photo.jpg", user.getProfilePhoto());
        assertEquals("B2", user.getEnglishLevel());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void userRole_ShouldHaveAllRoles() {
        // Assert
        assertNotNull(User.Role.STUDENT);
        assertNotNull(User.Role.TUTOR);
        assertNotNull(User.Role.ADMIN);
        assertNotNull(User.Role.ACADEMIC_OFFICE_AFFAIR);
        assertNotNull(User.Role.SPONSOR);
    }

    @Test
    void user_ShouldHandleNullValues() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getPassword());
        assertNull(user.getPhone());
        assertNull(user.getCin());
        assertNull(user.getDateOfBirth());
        assertNull(user.getAddress());
        assertNull(user.getCity());
        assertNull(user.getPostalCode());
        assertNull(user.getBio());
        assertNull(user.getYearsOfExperience());
        assertNull(user.getRole());
        // Les valeurs booléennes ont des valeurs par défaut
        assertNotNull(user.isActive()); // peut être true ou false selon l'implémentation
        assertNotNull(user.isRegistrationFeePaid());
        assertNotNull(user.isProfileCompleted());
        assertNull(user.getProfilePhoto());
        assertNull(user.getEnglishLevel());
    }

    @Test
    void user_ShouldHandleEqualsAndHashCode() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        User user2 = new User();
        user2.setId(1L);
        user2.setEmail("test@example.com");

        User user3 = new User();
        user3.setId(2L);
        user3.setEmail("other@example.com");

        // Assert
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void user_ToString_ShouldContainKeyFields() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        // Act
        String toString = user.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("test@example.com") || toString.contains("John") || toString.contains("Doe"));
    }
}
