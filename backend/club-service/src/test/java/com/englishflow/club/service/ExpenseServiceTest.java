package com.englishflow.club.service;

import com.englishflow.club.dto.ExpenseDTO;
import com.englishflow.club.entity.Expense;
import com.englishflow.club.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ClubHistoryService clubHistoryService;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;

    @BeforeEach
    void setUp() {
        testExpense = Expense.builder()
                .id(1)
                .clubId(1)
                .designation("Office Supplies")
                .amount(150.0)
                .expenseDate(LocalDateTime.now())
                .createdBy(100L)
                .notes("Test notes")
                .source("CASH")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testExpenseDTO = ExpenseDTO.builder()
                .id(1)
                .clubId(1)
                .designation("Office Supplies")
                .amount(150.0)
                .expenseDate(LocalDateTime.now())
                .createdBy(100L)
                .notes("Test notes")
                .source("CASH")
                .build();
    }

    @Test
    void getExpensesByClub_ShouldReturnExpenses() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByClubIdOrderByExpenseDateDesc(1)).thenReturn(expenses);

        // Act
        List<ExpenseDTO> result = expenseService.getExpensesByClub(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Office Supplies", result.get(0).getDesignation());
        verify(expenseRepository, times(1)).findByClubIdOrderByExpenseDateDesc(1);
    }

    @Test
    void getExpensesByClubAndDateRange_ShouldReturnFilteredExpenses() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByClubIdAndExpenseDateBetweenOrderByExpenseDateDesc(1, startDate, endDate))
                .thenReturn(expenses);

        // Act
        List<ExpenseDTO> result = expenseService.getExpensesByClubAndDateRange(1, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository, times(1))
                .findByClubIdAndExpenseDateBetweenOrderByExpenseDateDesc(1, startDate, endDate);
    }

    @Test
    void getExpenseById_WhenExists_ShouldReturnExpense() {
        // Arrange
        when(expenseRepository.findById(1)).thenReturn(Optional.of(testExpense));

        // Act
        ExpenseDTO result = expenseService.getExpenseById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Office Supplies", result.getDesignation());
        assertEquals(150.0, result.getAmount());
        verify(expenseRepository, times(1)).findById(1);
    }

    @Test
    void getExpenseById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> expenseService.getExpenseById(999));
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense() {
        // Arrange
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);
        doNothing().when(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), any(), anyString(), anyLong());

        // Act
        ExpenseDTO result = expenseService.createExpense(testExpenseDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Office Supplies", result.getDesignation());
        verify(expenseRepository, times(1)).save(any(Expense.class));
        verify(clubHistoryService, times(1)).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), any(), anyString(), anyLong());
    }

    @Test
    void createIncomeEntry_ShouldCreateIncome() {
        // Arrange
        testExpenseDTO.setSource("MEMBERSHIP_FEE");
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseDTO result = expenseService.createIncomeEntry(testExpenseDTO);

        // Assert
        assertNotNull(result);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenExists_ShouldUpdateExpense() {
        // Arrange
        ExpenseDTO updateDTO = ExpenseDTO.builder()
                .designation("Updated Supplies")
                .amount(200.0)
                .expenseDate(LocalDateTime.now())
                .notes("Updated notes")
                .build();

        when(expenseRepository.findById(1)).thenReturn(Optional.of(testExpense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        // Act
        ExpenseDTO result = expenseService.updateExpense(1, updateDTO);

        // Assert
        assertNotNull(result);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void updateExpense_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> expenseService.updateExpense(999, testExpenseDTO));
    }

    @Test
    void deleteExpense_ShouldDeleteSuccessfully() {
        // Arrange
        doNothing().when(expenseRepository).deleteById(1);

        // Act
        expenseService.deleteExpense(1);

        // Assert
        verify(expenseRepository, times(1)).deleteById(1);
    }

    @Test
    void getTotalExpensesByClub_ShouldReturnTotal() {
        // Arrange
        Expense expense2 = Expense.builder()
                .id(2)
                .clubId(1)
                .amount(250.0)
                .build();
        List<Expense> expenses = Arrays.asList(testExpense, expense2);
        when(expenseRepository.findByClubIdOrderByExpenseDateDesc(1)).thenReturn(expenses);

        // Act
        Double total = expenseService.getTotalExpensesByClub(1);

        // Assert
        assertEquals(400.0, total);
        verify(expenseRepository, times(1)).findByClubIdOrderByExpenseDateDesc(1);
    }

    @Test
    void existsIncomeEntryForToken_WhenExists_ShouldReturnTrue() {
        // Arrange
        testExpense.setNotes("Payment token: ABC123");
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByClubIdOrderByExpenseDateDesc(1)).thenReturn(expenses);

        // Act
        boolean result = expenseService.existsIncomeEntryForToken(1, "ABC123");

        // Assert
        assertTrue(result);
    }

    @Test
    void existsIncomeEntryForToken_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        List<Expense> expenses = Arrays.asList(testExpense);
        when(expenseRepository.findByClubIdOrderByExpenseDateDesc(1)).thenReturn(expenses);

        // Act
        boolean result = expenseService.existsIncomeEntryForToken(1, "XYZ999");

        // Assert
        assertFalse(result);
    }
}
