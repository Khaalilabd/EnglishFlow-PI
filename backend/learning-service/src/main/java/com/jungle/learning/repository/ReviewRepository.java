package com.jungle.learning.repository;

import com.jungle.learning.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    List<Review> findByEbookId(Long ebookId);
    
    List<Review> findByUserId(Long userId);
    
    Optional<Review> findByEbookIdAndUserId(Long ebookId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.ebook.id = :ebookId")
    Double getAverageRatingByEbookId(Long ebookId);
    
    Long countByEbookId(Long ebookId);
    
    List<Review> findByEbookIdOrderByCreatedAtDesc(Long ebookId);
    
    List<Review> findByEbookIdAndRating(Long ebookId, Integer rating);
}
