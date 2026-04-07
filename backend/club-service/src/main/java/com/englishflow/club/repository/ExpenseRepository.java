package com.englishflow.club.repository;

import com.englishflow.club.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    
    List<Expense> findByClubIdOrderByExpenseDateDesc(Integer clubId);
    
    List<Expense> findByClubIdAndExpenseDateBetweenOrderByExpenseDateDesc(
        Integer clubId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    List<Expense> findByCreatedByOrderByExpenseDateDesc(Long createdBy);
}
