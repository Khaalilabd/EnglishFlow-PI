package com.englishflow.event.service;

import com.englishflow.event.dto.EventFeedbackDTO;
import com.englishflow.event.dto.EventFeedbackStatsDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.EventFeedback;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.exception.UnauthorizedException;
import com.englishflow.event.mapper.EventFeedbackMapper;
import com.englishflow.event.repository.EventFeedbackRepository;
import com.englishflow.event.repository.EventRepository;
import com.englishflow.event.repository.ParticipantRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventFeedbackServiceTest {

    @Mock
    private EventFeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventFeedbackMapper feedbackMapper;

    @InjectMocks
    private EventFeedbackService feedbackService;

    private Event event;
    private EventFeedback feedback;
    private EventFeedbackDTO feedbackDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setCreatorId(1L);
        event.setEndDate(LocalDateTime.now().minusDays(1)); // Event ended

        feedback = new EventFeedback();
        feedback.setId(1L);
        feedback.setEventId(1);
        feedback.setUserId(1L);
        feedback.setRating(5.0);
        feedback.setComment("Great event!");
        feedback.setAnonymous(false);

        feedbackDTO = new EventFeedbackDTO();
        feedbackDTO.setId(1L);
        feedbackDTO.setEventId(1);
        feedbackDTO.setUserId(1L);
        feedbackDTO.setRating(5.0);
        feedbackDTO.setComment("Great event!");
        feedbackDTO.setAnonymous(false);
    }

    @Test
    void createFeedback_WhenValid_ShouldCreateFeedback() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);
        when(feedbackRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);
        when(feedbackMapper.toEntity(any(EventFeedbackDTO.class))).thenReturn(feedback);
        when(feedbackRepository.save(any(EventFeedback.class))).thenReturn(feedback);
        when(feedbackMapper.toDTO(any(EventFeedback.class))).thenReturn(feedbackDTO);

        // Act
        EventFeedbackDTO result = feedbackService.createFeedback(feedbackDTO);

        // Assert
        assertNotNull(result);
        assertEquals(5.0, result.getRating());
        verify(feedbackRepository).save(any(EventFeedback.class));
    }

    @Test
    void createFeedback_WhenEventNotEnded_ShouldThrowException() {
        // Arrange
        event.setEndDate(LocalDateTime.now().plusDays(1)); // Event not ended
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            feedbackService.createFeedback(feedbackDTO)
        );
    }

    @Test
    void createFeedback_WhenNotParticipant_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);
        event.setCreatorId(2L); // Different creator

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            feedbackService.createFeedback(feedbackDTO)
        );
    }

    @Test
    void createFeedback_WhenAlreadyGivenFeedback_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);
        when(feedbackRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            feedbackService.createFeedback(feedbackDTO)
        );
    }

    @Test
    void getEventFeedbacks_ShouldReturnFeedbackList() {
        // Arrange
        when(feedbackRepository.findByEventId(1)).thenReturn(Arrays.asList(feedback));
        when(feedbackMapper.toDTO(any(EventFeedback.class))).thenReturn(feedbackDTO);

        // Act
        List<EventFeedbackDTO> result = feedbackService.getEventFeedbacks(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(feedbackRepository).findByEventId(1);
    }

    @Test
    void getEventFeedbacks_WhenAnonymous_ShouldHideUserInfo() {
        // Arrange
        feedbackDTO.setAnonymous(true);
        feedbackDTO.setUserFirstName("John");
        feedbackDTO.setUserLastName("Doe");
        when(feedbackRepository.findByEventId(1)).thenReturn(Arrays.asList(feedback));
        when(feedbackMapper.toDTO(any(EventFeedback.class))).thenReturn(feedbackDTO);

        // Act
        List<EventFeedbackDTO> result = feedbackService.getEventFeedbacks(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getUserFirstName());
        assertNull(result.get(0).getUserLastName());
    }

    @Test
    void getEventFeedbackStats_ShouldReturnStats() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(feedbackRepository.countByEventId(1)).thenReturn(10L);
        when(feedbackRepository.getAverageRatingByEventId(1)).thenReturn(4.5);
        when(feedbackRepository.countSatisfiedByEventId(1)).thenReturn(8L);
        when(feedbackRepository.getRatingDistributionByEventId(1))
            .thenReturn(Arrays.asList(
                new Object[]{5, 5L},
                new Object[]{4, 3L},
                new Object[]{3, 2L}
            ));

        // Act
        EventFeedbackStatsDTO result = feedbackService.getEventFeedbackStats(1);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalFeedbacks());
        assertEquals(4.5, result.getAverageRating());
        assertEquals(80.0, result.getSatisfactionRate());
        assertNotNull(result.getRatingDistribution());
    }

    @Test
    void getEventFeedbackStats_WhenNoFeedbacks_ShouldReturnEmptyStats() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(feedbackRepository.countByEventId(1)).thenReturn(0L);

        // Act
        EventFeedbackStatsDTO result = feedbackService.getEventFeedbackStats(1);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalFeedbacks());
    }

    @Test
    void hasUserGivenFeedback_WhenFeedbackExists_ShouldReturnTrue() {
        // Arrange
        when(feedbackRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);

        // Act
        boolean result = feedbackService.hasUserGivenFeedback(1, 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasUserGivenFeedback_WhenNoFeedback_ShouldReturnFalse() {
        // Arrange
        when(feedbackRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);

        // Act
        boolean result = feedbackService.hasUserGivenFeedback(1, 1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getUserFeedback_WhenExists_ShouldReturnFeedback() {
        // Arrange
        when(feedbackRepository.findByEventIdAndUserId(1, 1L)).thenReturn(Optional.of(feedback));
        when(feedbackMapper.toDTO(any(EventFeedback.class))).thenReturn(feedbackDTO);

        // Act
        EventFeedbackDTO result = feedbackService.getUserFeedback(1, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getUserFeedback_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(feedbackRepository.findByEventIdAndUserId(1, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            feedbackService.getUserFeedback(1, 1L)
        );
    }

    @Test
    void updateFeedback_WhenOwner_ShouldUpdateFeedback() {
        // Arrange
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(feedbackRepository.save(any(EventFeedback.class))).thenReturn(feedback);
        when(feedbackMapper.toDTO(any(EventFeedback.class))).thenReturn(feedbackDTO);

        feedbackDTO.setRating(4.0);
        feedbackDTO.setComment("Updated comment");

        // Act
        EventFeedbackDTO result = feedbackService.updateFeedback(1L, feedbackDTO, 1L);

        // Assert
        assertNotNull(result);
        verify(feedbackRepository).save(feedback);
    }

    @Test
    void updateFeedback_WhenNotOwner_ShouldThrowException() {
        // Arrange
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            feedbackService.updateFeedback(1L, feedbackDTO, 2L)
        );
    }

    @Test
    void deleteFeedback_WhenOwner_ShouldDeleteFeedback() {
        // Arrange
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        doNothing().when(feedbackRepository).delete(any(EventFeedback.class));

        // Act
        feedbackService.deleteFeedback(1L, 1L);

        // Assert
        verify(feedbackRepository).delete(feedback);
    }

    @Test
    void deleteFeedback_WhenNotOwner_ShouldThrowException() {
        // Arrange
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> 
            feedbackService.deleteFeedback(1L, 2L)
        );
    }
}
