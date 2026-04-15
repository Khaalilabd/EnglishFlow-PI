package com.englishflow.club.service;

import com.englishflow.club.dto.ExpenseDTO;
import com.englishflow.club.entity.Expense;
import com.englishflow.club.enums.ClubHistoryType;
import com.englishflow.club.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final ClubHistoryService clubHistoryService;
    
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByClub(Integer clubId) {
        log.info("Fetching expenses for club: {}", clubId);
        return expenseRepository.findByClubIdOrderByExpenseDateDesc(clubId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ExpenseDTO> getExpensesByClubAndDateRange(
            Integer clubId, 
            LocalDateTime startDate, 
            LocalDateTime endDate) {
        log.info("Fetching expenses for club {} between {} and {}", clubId, startDate, endDate);
        return expenseRepository.findByClubIdAndExpenseDateBetweenOrderByExpenseDateDesc(
                clubId, startDate, endDate)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ExpenseDTO getExpenseById(Integer id) {
        log.info("Fetching expense by id: {}", id);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        return toDTO(expense);
    }
    
    @Transactional
    public ExpenseDTO createIncomeEntry(ExpenseDTO expenseDTO) {
        log.info("Creating income entry for club: {} source: {}", expenseDTO.getClubId(), expenseDTO.getSource());
        Expense expense = Expense.builder()
                .clubId(expenseDTO.getClubId())
                .designation(expenseDTO.getDesignation())
                .amount(expenseDTO.getAmount())
                .expenseDate(expenseDTO.getExpenseDate())
                .createdBy(expenseDTO.getCreatedBy())
                .notes(expenseDTO.getNotes())
                .source(expenseDTO.getSource())
                .build();
        Expense saved = expenseRepository.save(expense);
        log.info("Income entry created with id: {}", saved.getId());
        return toDTO(saved);
    }
    
    @Transactional
    public ExpenseDTO createExpense(ExpenseDTO expenseDTO) {
        log.info("Creating expense for club: {}", expenseDTO.getClubId());
        
        Expense expense = Expense.builder()
                .clubId(expenseDTO.getClubId())
                .designation(expenseDTO.getDesignation())
                .amount(expenseDTO.getAmount())
                .expenseDate(expenseDTO.getExpenseDate())
                .createdBy(expenseDTO.getCreatedBy())
                .notes(expenseDTO.getNotes())
                .source(expenseDTO.getSource())
                .build();
        
        Expense savedExpense = expenseRepository.save(expense);
        log.info("Expense created with id: {}", savedExpense.getId());

        // Log history
        clubHistoryService.logHistory(
            expenseDTO.getClubId().longValue(),
            expenseDTO.getCreatedBy().longValue(),
            ClubHistoryType.EXPENSE_ADDED,
            "Expense Added",
            "Added expense: " + expenseDTO.getDesignation() + " — " + expenseDTO.getAmount() + " DT",
            null,
            String.valueOf(expenseDTO.getAmount()),
            expenseDTO.getCreatedBy().longValue()
        );

        return toDTO(savedExpense);
    }
    
    @Transactional
    public ExpenseDTO updateExpense(Integer id, ExpenseDTO expenseDTO) {
        log.info("Updating expense: {}", id);
        
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
        
        expense.setDesignation(expenseDTO.getDesignation());
        expense.setAmount(expenseDTO.getAmount());
        expense.setExpenseDate(expenseDTO.getExpenseDate());
        expense.setNotes(expenseDTO.getNotes());
        
        Expense updatedExpense = expenseRepository.save(expense);
        log.info("Expense updated: {}", id);
        return toDTO(updatedExpense);
    }
    
    @Transactional
    public void deleteExpense(Integer id) {
        log.info("Deleting expense: {}", id);
        expenseRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsIncomeEntryForToken(Integer clubId, String token) {
        return expenseRepository.findByClubIdOrderByExpenseDateDesc(clubId).stream()
                .anyMatch(e -> e.getNotes() != null && e.getNotes().contains(token));
    }

    @Transactional(readOnly = true)
    public Double getTotalExpensesByClub(Integer clubId) {
        List<Expense> expenses = expenseRepository.findByClubIdOrderByExpenseDateDesc(clubId);
        return expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
    
    private ExpenseDTO toDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .clubId(expense.getClubId())
                .designation(expense.getDesignation())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .createdBy(expense.getCreatedBy())
                .notes(expense.getNotes())
                .source(expense.getSource())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
