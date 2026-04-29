package com.jungle.learning.service;

import com.jungle.learning.dto.CreateReviewRequest;
import com.jungle.learning.dto.ReviewDTO;
import com.jungle.learning.model.Ebook;
import com.jungle.learning.model.Review;
import com.jungle.learning.repository.EbookRepository;
import com.jungle.learning.repository.ReviewRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private EbookRepository ebookRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ReviewService reviewService;

    private Ebook ebook;
    private Review review;
    private CreateReviewRequest createRequest;

    @BeforeEach
    void setUp() {
        ebook = new Ebook();
        ebook.setId(1L);
        ebook.setTitle("Test Ebook");
        ebook.setAverageRating(4.5);
        ebook.setReviewCount(10);

        review = new Review();
        review.setId(1L);
        review.setEbook(ebook);
        review.setUserId(100L);
        review.setRating(5);
        review.setComment("Great book!");
        review.setIsVerified(false);
        review.setHelpfulCount(0);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateReviewRequest();
        createRequest.setEbookId(1L);
        createRequest.setRating(5);
        createRequest.setComment("Great book!");
    }

    @Test
    void createReview_NewReview_ShouldCreateAndReturn() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(reviewRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.empty());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.getAverageRatingByEbookId(1L)).thenReturn(4.5);
        when(reviewRepository.countByEbookId(1L)).thenReturn(11L);
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);
        when(userServiceClient.getUserName(100L)).thenReturn("Test User");

        ReviewDTO result = reviewService.createReview(createRequest, 100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getRating());
        assertEquals("Great book!", result.getComment());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_DuplicateReview_ShouldThrowException() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(reviewRepository.findByEbookIdAndUserId(1L, 100L)).thenReturn(Optional.of(review));

        assertThrows(IllegalStateException.class, () -> 
            reviewService.createReview(createRequest, 100L)
        );
    }

    @Test
    void createReview_EbookNotFound_ShouldThrowException() {
        when(ebookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            reviewService.createReview(createRequest, 100L)
        );
    }

    @Test
    void updateReview_ValidUpdate_ShouldUpdateAndReturn() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.getAverageRatingByEbookId(1L)).thenReturn(4.5);
        when(reviewRepository.countByEbookId(1L)).thenReturn(10L);
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);
        when(userServiceClient.getUserName(100L)).thenReturn("Test User");

        CreateReviewRequest updateRequest = new CreateReviewRequest();
        updateRequest.setRating(4);
        updateRequest.setComment("Updated comment");

        ReviewDTO result = reviewService.updateReview(1L, updateRequest, 100L);

        assertNotNull(result);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void updateReview_Unauthorized_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(RuntimeException.class, () -> 
            reviewService.updateReview(1L, createRequest, 999L)
        );
    }

    @Test
    void updateReview_ReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            reviewService.updateReview(1L, createRequest, 100L)
        );
    }

    @Test
    void deleteReview_ValidDelete_ShouldDelete() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.getAverageRatingByEbookId(1L)).thenReturn(4.5);
        when(reviewRepository.countByEbookId(1L)).thenReturn(9L);
        when(ebookRepository.findById(1L)).thenReturn(Optional.of(ebook));
        when(ebookRepository.save(any(Ebook.class))).thenReturn(ebook);

        reviewService.deleteReview(1L, 100L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void deleteReview_Unauthorized_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(RuntimeException.class, () -> 
            reviewService.deleteReview(1L, 999L)
        );
    }

    @Test
    void deleteReview_ReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            reviewService.deleteReview(1L, 100L)
        );
    }

    @Test
    void getEbookReviews_ShouldReturnListOfReviews() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByEbookIdOrderByCreatedAtDesc(1L)).thenReturn(reviews);
        when(userServiceClient.getUserName(100L)).thenReturn("Test User");

        List<ReviewDTO> result = reviewService.getEbookReviews(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getUserReviews_ShouldReturnUserReviews() {
        List<Review> reviews = Arrays.asList(review);
        when(reviewRepository.findByUserId(100L)).thenReturn(reviews);
        when(userServiceClient.getUserName(100L)).thenReturn("Test User");

        List<ReviewDTO> result = reviewService.getUserReviews(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void markHelpful_ShouldIncrementHelpfulCount() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        reviewService.markHelpful(1L);

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void markHelpful_ReviewNotFound_ShouldThrowException() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            reviewService.markHelpful(1L)
        );
    }
}
