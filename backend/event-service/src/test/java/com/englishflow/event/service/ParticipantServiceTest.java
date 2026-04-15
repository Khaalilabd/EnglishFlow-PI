package com.englishflow.event.service;

import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.enums.EventStatus;
import com.englishflow.event.exception.AlreadyParticipantException;
import com.englishflow.event.exception.EventFullException;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.ParticipantMapper;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantMapper participantMapper;

    @InjectMocks
    private ParticipantService participantService;

    private Event event;
    private Participant participant;
    private ParticipantDTO participantDTO;
    private Long eventId = 1L;
    private Long userId = 100L;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(eventId);
        event.setTitle("English Workshop");
        event.setMaxParticipants(50);
        event.setStatus(EventStatus.UPCOMING);
        event.setStartDate(LocalDateTime.now().plusDays(7));

        participant = new Participant();
        participant.setId(1L);
        participant.setEvent(event);
        participant.setUserId(userId);
        participant.setUserName("John Doe");
        participant.setJoinedAt(LocalDateTime.now());

        participantDTO = new ParticipantDTO();
        participantDTO.setId(1L);
        participantDTO.setEventId(eventId);
        participantDTO.setUserId(userId);
        participantDTO.setUserName("John Doe");
    }

    @Test
    void joinEvent_WhenValid_ShouldAddParticipant() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(participantRepository.countByEventId(eventId)).thenReturn(25L);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        when(participantMapper.toDTO(participant)).thenReturn(participantDTO);

        // When
        ParticipantDTO result = participantService.joinEvent(eventId, userId, "John Doe");

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(participantRepository).save(any(Participant.class));
    }

    @Test
    void joinEvent_WhenAlreadyParticipant_ShouldThrowException() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(true);

        // When & Then
        assertThrows(AlreadyParticipantException.class, 
            () -> participantService.joinEvent(eventId, userId, "John Doe"));
        verify(participantRepository, never()).save(any(Participant.class));
    }

    @Test
    void joinEvent_WhenEventFull_ShouldThrowException() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(participantRepository.countByEventId(eventId)).thenReturn(50L);

        // When & Then
        assertThrows(EventFullException.class, 
            () -> participantService.joinEvent(eventId, userId, "John Doe"));
    }

    @Test
    void joinEvent_WhenEventNotFound_ShouldThrowException() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> participantService.joinEvent(eventId, userId, "John Doe"));
    }

    @Test
    void leaveEvent_WhenParticipant_ShouldRemove() {
        // Given
        when(participantRepository.findByEventIdAndUserId(eventId, userId))
            .thenReturn(Optional.of(participant));

        // When
        participantService.leaveEvent(eventId, userId);

        // Then
        verify(participantRepository).delete(participant);
    }

    @Test
    void leaveEvent_WhenNotParticipant_ShouldThrowException() {
        // Given
        when(participantRepository.findByEventIdAndUserId(eventId, userId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> participantService.leaveEvent(eventId, userId));
    }

    @Test
    void getEventParticipants_ShouldReturnParticipants() {
        // Given
        when(participantRepository.findByEventId(eventId))
            .thenReturn(Arrays.asList(participant));
        when(participantMapper.toDTO(participant)).thenReturn(participantDTO);

        // When
        List<ParticipantDTO> result = participantService.getEventParticipants(eventId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void getUserEvents_ShouldReturnUserEvents() {
        // Given
        when(participantRepository.findByUserId(userId))
            .thenReturn(Arrays.asList(participant));
        when(participantMapper.toDTO(participant)).thenReturn(participantDTO);

        // When
        List<ParticipantDTO> result = participantService.getUserEvents(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());
    }

    @Test
    void isUserParticipant_WhenParticipant_ShouldReturnTrue() {
        // Given
        when(participantRepository.existsByEventIdAndUserId(eventId, userId))
            .thenReturn(true);

        // When
        boolean result = participantService.isUserParticipant(eventId, userId);

        // Then
        assertTrue(result);
    }

    @Test
    void isUserParticipant_WhenNotParticipant_ShouldReturnFalse() {
        // Given
        when(participantRepository.existsByEventIdAndUserId(eventId, userId))
            .thenReturn(false);

        // When
        boolean result = participantService.isUserParticipant(eventId, userId);

        // Then
        assertFalse(result);
    }

    @Test
    void getParticipantCount_ShouldReturnCount() {
        // Given
        when(participantRepository.countByEventId(eventId)).thenReturn(35L);

        // When
        Long result = participantService.getParticipantCount(eventId);

        // Then
        assertEquals(35L, result);
    }
}
