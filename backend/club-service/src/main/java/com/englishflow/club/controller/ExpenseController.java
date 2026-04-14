package com.englishflow.club.controller;

import com.englishflow.club.dto.ExpenseDTO;
import com.englishflow.club.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
@Slf4j
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByClub(@PathVariable Integer clubId) {
        log.info("GET /expenses/club/{} - Fetching expenses for club", clubId);
        List<ExpenseDTO> expenses = expenseService.getExpensesByClub(clubId);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/club/{clubId}/range")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByClubAndDateRange(
            @PathVariable Integer clubId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /expenses/club/{}/range - Fetching expenses between dates", clubId);
        List<ExpenseDTO> expenses = expenseService.getExpensesByClubAndDateRange(clubId, startDate, endDate);
        return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Integer id) {
        log.info("GET /expenses/{} - Fetching expense by id", id);
        ExpenseDTO expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(expense);
    }
    
    @GetMapping("/club/{clubId}/total")
    public ResponseEntity<Double> getTotalExpenses(@PathVariable Integer clubId) {
        log.info("GET /expenses/club/{}/total - Calculating total expenses", clubId);
        Double total = expenseService.getTotalExpensesByClub(clubId);
        return ResponseEntity.ok(total);
    }
    
    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        log.info("POST /expenses - Creating new expense");
        ExpenseDTO createdExpense = expenseService.createExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @PostMapping("/income")
    public ResponseEntity<ExpenseDTO> createIncomeEntry(@RequestBody ExpenseDTO expenseDTO) {
        log.info("POST /expenses/income - Creating income entry for club {}", expenseDTO.getClubId());
        ExpenseDTO created = expenseService.createIncomeEntry(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(
            @PathVariable Integer id,
            @RequestBody ExpenseDTO expenseDTO) {
        log.info("PUT /expenses/{} - Updating expense", id);
        ExpenseDTO updatedExpense = expenseService.updateExpense(id, expenseDTO);
        return ResponseEntity.ok(updatedExpense);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer id) {
        log.info("DELETE /expenses/{} - Deleting expense", id);
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
