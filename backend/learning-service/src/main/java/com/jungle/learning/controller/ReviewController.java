package com.jungle.learning.controller;

import com.jungle.learning.dto.CreateReviewRequest;
import com.jungle.learning.dto.ReviewDTO;
import com.jungle.learning.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning/reviews-deprecated")
@RequiredArgsConstructor
// DEPRECATED: Reviews are now handled in EbookController under /api/learning/ebooks/{ebookId}/reviews
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @RequestBody CreateReviewRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        ReviewDTO review = reviewService.createReview(request, userId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestBody CreateReviewRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        ReviewDTO review = reviewService.updateReview(reviewId, request, userId);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("X-User-Id") Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ebook/{ebookId}")
    public ResponseEntity<List<ReviewDTO>> getEbookReviews(@PathVariable Long ebookId) {
        List<ReviewDTO> reviews = reviewService.getEbookReviews(ebookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markHelpful(@PathVariable Long reviewId) {
        reviewService.markHelpful(reviewId);
        return ResponseEntity.ok().build();
    }
}
