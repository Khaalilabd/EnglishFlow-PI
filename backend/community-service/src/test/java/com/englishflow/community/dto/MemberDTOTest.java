package com.englishflow.community.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemberDTOTest {

    @Test
    void testNoArgsConstructor() {
        MemberDTO dto = new MemberDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        MemberDTO dto = new MemberDTO(1L, 100L, "PRESIDENT");
        assertEquals(1L, dto.getClubId());
        assertEquals(100L, dto.getUserId());
        assertEquals("PRESIDENT", dto.getRank());
    }

    @Test
    void testSettersAndGetters() {
        MemberDTO dto = new MemberDTO();
        dto.setClubId(2L);
        dto.setUserId(200L);
        dto.setRank("MEMBER");
        
        assertEquals(2L, dto.getClubId());
        assertEquals(200L, dto.getUserId());
        assertEquals("MEMBER", dto.getRank());
    }

    @Test
    void testToString() {
        MemberDTO dto = new MemberDTO(1L, 100L, "PRESIDENT");
        String toString = dto.toString();
        assertTrue(toString.contains("PRESIDENT"));
    }
}
