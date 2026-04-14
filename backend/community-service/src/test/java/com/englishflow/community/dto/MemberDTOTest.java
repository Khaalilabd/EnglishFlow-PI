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
        MemberDTO dto = new MemberDTO(1, 100, 1000L, "PRESIDENT");
        assertEquals(1, dto.getId());
        assertEquals(100, dto.getClubId());
        assertEquals(1000L, dto.getUserId());
        assertEquals("PRESIDENT", dto.getRank());
    }

    @Test
    void testSettersAndGetters() {
        MemberDTO dto = new MemberDTO();
        dto.setId(2);
        dto.setClubId(200);
        dto.setUserId(2000L);
        dto.setRank("MEMBER");
        
        assertEquals(2, dto.getId());
        assertEquals(200, dto.getClubId());
        assertEquals(2000L, dto.getUserId());
        assertEquals("MEMBER", dto.getRank());
    }

    @Test
    void testToString() {
        MemberDTO dto = new MemberDTO(1, 100, 1000L, "PRESIDENT");
        String toString = dto.toString();
        assertTrue(toString.contains("PRESIDENT"));
    }
}
