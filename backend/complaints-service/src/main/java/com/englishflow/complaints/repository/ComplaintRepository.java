package com.englishflow.complaints.repository;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ========== PAGINATED METHODS ==========
    
    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    Page<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Page<Complaint> findByStatusOrderByCreatedAtDesc(ComplaintStatus status, Pageable pageable);
    
    Page<Complaint> findByTargetRoleOrderByCreatedAtDesc(TargetRole targetRole, Pageable pageable);
    
    Page<Complaint> findByCategoryOrderByCreatedAtDesc(ComplaintCategory category, Pageable pageable);
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "(:userId IS NULL OR c.userId = :userId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:category IS NULL OR c.category = :category) AND " +
           "(:targetRole IS NULL OR c.targetRole = :targetRole) " +
           "ORDER BY c.createdAt DESC")
    Page<Complaint> findByFilters(
        @Param("userId") Long userId,
        @Param("status") ComplaintStatus status,
        @Param("category") ComplaintCategory category,
        @Param("targetRole") TargetRole targetRole,
        Pageable pageable
    );
    
    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.createdAt DESC")
    Page<Complaint> searchComplaints(@Param("search") String search, Pageable pageable);
}

