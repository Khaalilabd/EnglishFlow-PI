package com.englishflow.sponsors.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberDTOTest {

    @Test
    void builder_ShouldCreateMemberDTOWithAllFields() {
        MemberDTO dto = MemberDTO.builder()
                .id(1)
                .clubId(10)
                .userId(100L)
                .rank("PRESIDENT")
                .userName("John Doe")
                .build();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClubId()).isEqualTo(10);
        assertThat(dto.getUserId()).isEqualTo(100L);
        assertThat(dto.getRank()).isEqualTo("PRESIDENT");
        assertThat(dto.getUserName()).isEqualTo("John Doe");
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        MemberDTO dto = new MemberDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getClubId()).isNull();
        assertThat(dto.getUserId()).isNull();
        assertThat(dto.getRank()).isNull();
        assertThat(dto.getUserName()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        MemberDTO dto = new MemberDTO(1, 10, 100L, "MEMBER", "Jane Smith");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClubId()).isEqualTo(10);
        assertThat(dto.getUserId()).isEqualTo(100L);
        assertThat(dto.getRank()).isEqualTo("MEMBER");
        assertThat(dto.getUserName()).isEqualTo("Jane Smith");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        MemberDTO dto = new MemberDTO();

        dto.setId(5);
        dto.setClubId(20);
        dto.setUserId(200L);
        dto.setRank("VICE_PRESIDENT");
        dto.setUserName("Bob Johnson");

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getClubId()).isEqualTo(20);
        assertThat(dto.getUserId()).isEqualTo(200L);
        assertThat(dto.getRank()).isEqualTo("VICE_PRESIDENT");
        assertThat(dto.getUserName()).isEqualTo("Bob Johnson");
    }
}
