package com.englishflow.complaints.repository;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    List<Complaint> findByUserId(Long userId);
    
    List<Complaint> findByStatus(ComplaintStatus status);
    
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Complaint> findAllByOrderByCreatedAtDesc();
    
    List<Complaint> findByUserIdAndCategoryAndCreatedAtAfter(
            Long userId, 
            ComplaintCategory category, 
            LocalDateTime createdAt
    );
    
    List<Complaint> findByUserIdAndCreatedAtAfter(
            Long userId, 
            LocalDateTime createdAt
    );
    
    List<Complaint> findByCategory(ComplaintCategory category);
}

