package com.englishflow.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {
    private Integer clubId;
    private String designation;
    private Double amount;
    private LocalDateTime expenseDate;
    private Long createdBy;
    private String notes;
    private String source;
}
