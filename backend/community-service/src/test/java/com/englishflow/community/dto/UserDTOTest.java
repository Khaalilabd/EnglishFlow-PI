package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    @Test
    void testNoArgsConstructor() {
        UserDTO dto = new UserDTO();
        assertNotNull(dto);
    }

    @Test
    void testSettersAndGetters() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setEmail("test@example.com");
        dto.setRole("STUDENT");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        
        assertEquals(1L, dto.getId());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("STUDENT", dto.getRole());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
    }

    @Test
    void testToString() {
        UserDTO dto = new UserDTO();
        dto.setEmail("test@example.com");
        String toString = dto.toString();
        assertTrue(toString.contains("test@example.com"));
    }
}
