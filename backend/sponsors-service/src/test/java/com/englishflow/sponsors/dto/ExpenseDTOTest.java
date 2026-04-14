package com.englishflow.sponsors.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ExpenseDTOTest {

    @Test
    void builder_ShouldCreateExpenseDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        
        ExpenseDTO dto = ExpenseDTO.builder()
                .id(1)
                .clubId(10)
                .designation("Sponsorship Payment")
                .amount(1500.0)
                .expenseDate(now)
                .createdBy(100L)
                .notes("Payment from Gold Sponsor")
                .source("SPONSOR")
                .build();

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClubId()).isEqualTo(10);
        assertThat(dto.getDesignation()).isEqualTo("Sponsorship Payment");
        assertThat(dto.getAmount()).isEqualTo(1500.0);
        assertThat(dto.getExpenseDate()).isEqualTo(now);
        assertThat(dto.getCreatedBy()).isEqualTo(100L);
        assertThat(dto.getNotes()).isEqualTo("Payment from Gold Sponsor");
        assertThat(dto.getSource()).isEqualTo("SPONSOR");
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyDTO() {
        ExpenseDTO dto = new ExpenseDTO();

        assertThat(dto.getId()).isNull();
        assertThat(dto.getClubId()).isNull();
        assertThat(dto.getDesignation()).isNull();
        assertThat(dto.getAmount()).isNull();
        assertThat(dto.getExpenseDate()).isNull();
        assertThat(dto.getCreatedBy()).isNull();
        assertThat(dto.getNotes()).isNull();
        assertThat(dto.getSource()).isNull();
    }

    @Test
    void allArgsConstructor_ShouldCreateDTOWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        
        ExpenseDTO dto = new ExpenseDTO(1, 10, "Test Expense", 500.0, now, 100L, "Test notes", "SPONSOR");

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getClubId()).isEqualTo(10);
        assertThat(dto.getDesignation()).isEqualTo("Test Expense");
        assertThat(dto.getAmount()).isEqualTo(500.0);
        assertThat(dto.getExpenseDate()).isEqualTo(now);
        assertThat(dto.getCreatedBy()).isEqualTo(100L);
        assertThat(dto.getNotes()).isEqualTo("Test notes");
        assertThat(dto.getSource()).isEqualTo("SPONSOR");
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        ExpenseDTO dto = new ExpenseDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(5);
        dto.setClubId(20);
        dto.setDesignation("Updated Expense");
        dto.setAmount(750.0);
        dto.setExpenseDate(now);
        dto.setCreatedBy(200L);
        dto.setNotes("Updated notes");
        dto.setSource("DONATION");

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getClubId()).isEqualTo(20);
        assertThat(dto.getDesignation()).isEqualTo("Updated Expense");
        assertThat(dto.getAmount()).isEqualTo(750.0);
        assertThat(dto.getExpenseDate()).isEqualTo(now);
        assertThat(dto.getCreatedBy()).isEqualTo(200L);
        assertThat(dto.getNotes()).isEqualTo("Updated notes");
        assertThat(dto.getSource()).isEqualTo("DONATION");
    }
}
