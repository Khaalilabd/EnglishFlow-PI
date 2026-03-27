package com.jungle.learning.service;

import com.jungle.learning.dto.CreateReviewRequest;
import com.jungle.learning.dto.ReviewDTO;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.Review;
import com.jungle.learning.repository.EbookRepository;
import com.jungle.learning.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final EbookRepository ebookRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public ReviewDTO createReview(CreateReviewRequest request, Long userId) {
        Ebook ebook = ebookRepository.findById(request.getEbookId())
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        // Check if user already reviewed this ebook
        reviewRepository.findByEbookIdAndUserId(request.getEbookId(), userId)
                .ifPresent(r -> {
                    throw new IllegalStateException("You have already reviewed this ebook. You can only submit one review per ebook.");
                });

        Review review = new Review();
        review.setEbook(ebook);
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setIsVerified(false);

        try {
            Review saved = reviewRepository.save(review);
            
            // Update ebook stats
            updateEbookRatingStats(ebook.getId());
            
            return mapToDTO(saved);
        } catch (Exception e) {
            // Handle database constraint violation
            if (e.getMessage() != null && e.getMessage().contains("unique_user_ebook")) {
                throw new IllegalStateException("You have already reviewed this ebook. You can only submit one review per ebook.");
            }
            throw e;
        }
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, CreateReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);

        // Update ebook stats
        updateEbookRatingStats(review.getEbook().getId());

        return mapToDTO(updated);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Long ebookId = review.getEbook().getId();
        reviewRepository.delete(review);

        // Update ebook stats
        updateEbookRatingStats(ebookId);
    }

    public List<ReviewDTO> getEbookReviews(Long ebookId) {
        return reviewRepository.findByEbookIdOrderByCreatedAtDesc(ebookId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getUserReviews(Long userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markHelpful(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    private void updateEbookRatingStats(Long ebookId) {
        Double avgRating = reviewRepository.getAverageRatingByEbookId(ebookId);
        Long reviewCount = reviewRepository.countByEbookId(ebookId);

        Ebook ebook = ebookRepository.findById(ebookId)
                .orElseThrow(() -> new RuntimeException("Ebook not found"));

        ebook.setAverageRating(avgRating != null ? avgRating : 0.0);
        ebook.setReviewCount(reviewCount.intValue());
        ebookRepository.save(ebook);
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setEbookId(review.getEbook().getId());
        dto.setUserId(review.getUserId());
        
        // Fetch actual user name from auth service
        String userName = userServiceClient.getUserName(review.getUserId());
        dto.setUserName(userName);
        
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setIsVerified(review.getIsVerified());
        dto.setHelpfulCount(review.getHelpfulCount());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
