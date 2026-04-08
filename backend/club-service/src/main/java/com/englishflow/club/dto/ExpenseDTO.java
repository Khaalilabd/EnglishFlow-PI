package com.englishflow.club.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDTO {
    private Integer id;
    private Integer clubId;
    private String designation;
    private Double amount;
    private LocalDateTime expenseDate;
    private Long createdBy;
    private String createdByName; // Enriched field
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
