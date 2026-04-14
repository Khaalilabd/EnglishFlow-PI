package com.englishflow.club.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_expense_club", columnList = "clubId"),
    @Index(name = "idx_expense_date", columnList = "expenseDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private Integer clubId;
    
    @Column(nullable = false, length = 200)
    private String designation; // Description de la dépense
    
    @Column(nullable = false)
    private Double amount; // Montant en DT
    
    @Column(nullable = false)
    private LocalDateTime expenseDate; // Date de la dépense
    
    @Column(nullable = false)
    private Long createdBy; // ID du trésorier qui a créé la dépense
    
    @Column(length = 500)
    private String notes; // Notes additionnelles

    @Column(length = 50)
    private String source; // REGISTRATION_FEE | SPONSORSHIP | OTHER
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
