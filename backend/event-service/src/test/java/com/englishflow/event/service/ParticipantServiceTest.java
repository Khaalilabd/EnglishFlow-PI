package com.englishflow.event.service;

import com.englishflow.event.client.AuthServiceClient;
import com.englishflow.event.client.ClubServiceClient;
import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.enums.EventType;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantMapper participantMapper;

    @Mock
    private WebSocketNotificationService wsNotificationService;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private ClubServiceClient clubServiceClient;

    @InjectMocks
    private ParticipantService participantService;

    private Event event;
    private Participant participant;
    private ParticipantDTO participantDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setType(EventType.WORKSHOP);
        event.setMaxParticipants(50);
        event.setCurrentParticipants(0);
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));

        participant = new Participant();
        participant.setId(1);
        participant.setEvent(event);
        participant.setUserId(1L);
        participant.setPaymentStatus("PAID");

        participantDTO = new ParticipantDTO();
        participantDTO.setId(1);
        participantDTO.setEventId(1);
        participantDTO.setUserId(1L);
        participantDTO.setPaymentStatus("PAID");
    }

    @Test
    void joinEvent_WhenValidRequest_ShouldAddParticipant() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);
        when(participantRepository.countByEventId(1)).thenReturn(0L);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(participantMapper.toDTO(any(Participant.class))).thenReturn(participantDTO);
        doNothing().when(wsNotificationService).notifyParticipantJoined(anyLong(), anyString(), anyLong(), anyString(), anyInt(), anyInt());

        // Act
        ParticipantDTO result = participantService.joinEvent(1, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(participantRepository).save(any(Participant.class));
        verify(wsNotificationService).notifyParticipantJoined(anyLong(), anyString(), anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void joinEvent_WhenAlreadyParticipant_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(AlreadyParticipantException.class, () -> 
            participantService.joinEvent(1, 1L)
        );
    }

    @Test
    void joinEvent_WhenEventFull_ShouldThrowException() {
        // Arrange
        event.setMaxParticipants(1);
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);
        when(participantRepository.countByEventId(1)).thenReturn(1L);

        // Act & Assert
        assertThrows(EventFullException.class, () -> 
            participantService.joinEvent(1, 1L)
        );
    }

    @Test
    void joinEvent_WhenEventNotFound_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            participantService.joinEvent(999, 1L)
        );
    }

    @Test
    void leaveEvent_WhenParticipantExists_ShouldRemoveParticipant() {
        // Arrange
        when(participantRepository.findByEventIdAndUserId(1, 1L)).thenReturn(Optional.of(participant));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.countByEventId(1)).thenReturn(0L);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        doNothing().when(participantRepository).delete(any(Participant.class));
        doNothing().when(wsNotificationService).notifyParticipantLeft(anyLong(), anyString(), anyLong(), anyString(), anyInt(), anyInt());

        // Act
        participantService.leaveEvent(1, 1L);

        // Assert
        verify(participantRepository).delete(participant);
        verify(wsNotificationService).notifyParticipantLeft(anyLong(), anyString(), anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void leaveEvent_WhenParticipantNotFound_ShouldThrowException() {
        // Arrange
        when(participantRepository.findByEventIdAndUserId(1, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            participantService.leaveEvent(1, 1L)
        );
    }

    @Test
    void getEventParticipants_ShouldReturnParticipantList() {
        // Arrange
        when(eventRepository.existsById(1)).thenReturn(true);
        when(participantRepository.findByEventId(1)).thenReturn(Arrays.asList(participant));
        when(participantMapper.toDTO(any(Participant.class))).thenReturn(participantDTO);
        when(authServiceClient.getUsersByIds(anyList())).thenReturn(java.util.Collections.emptyMap());

        // Act
        List<ParticipantDTO> result = participantService.getEventParticipants(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantRepository).findByEventId(1);
    }

    @Test
    void getEventParticipants_WhenEventNotFound_ShouldThrowException() {
        // Arrange
        when(eventRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            participantService.getEventParticipants(999)
        );
    }

    @Test
    void getUserEvents_ShouldReturnUserParticipations() {
        // Arrange
        when(participantRepository.findByUserId(1L)).thenReturn(Arrays.asList(participant));
        when(participantMapper.toDTO(any(Participant.class))).thenReturn(participantDTO);

        // Act
        List<ParticipantDTO> result = participantService.getUserEvents(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(participantRepository).findByUserId(1L);
    }

    @Test
    void isUserParticipant_WhenUserIsParticipant_ShouldReturnTrue() {
        // Arrange
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(true);

        // Act
        boolean result = participantService.isUserParticipant(1, 1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void isUserParticipant_WhenUserIsNotParticipant_ShouldReturnFalse() {
        // Arrange
        when(participantRepository.existsByEventIdAndUserId(1, 1L)).thenReturn(false);

        // Act
        boolean result = participantService.isUserParticipant(1, 1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void confirmPayment_ShouldUpdatePaymentStatus() {
        // Arrange
        participant.setPaymentStatus("PAYMENT_PENDING");
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        when(participantMapper.toDTO(any(Participant.class))).thenReturn(participantDTO);

        AuthServiceClient.UserInfo userInfo = new AuthServiceClient.UserInfo();
        userInfo.setEmail("test@example.com");
        userInfo.setFirstName("Test");
        when(authServiceClient.getUserById(anyLong())).thenReturn(userInfo);

        // Act
        ParticipantDTO result = participantService.confirmPayment(1, "CARD", "token123");

        // Assert
        assertNotNull(result);
        assertEquals("PAID", participant.getPaymentStatus());
        verify(participantRepository).save(participant);
    }

    @Test
    void getParticipantById_WhenExists_ShouldReturnParticipant() {
        // Arrange
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(participantMapper.toDTO(any(Participant.class))).thenReturn(participantDTO);

        // Act
        ParticipantDTO result = participantService.getParticipantById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getTotalConfirmedPayments_ShouldCalculateTotal() {
        // Arrange
        event.setParticipationFee(10.0);
        participant.setPaymentStatus("PAID");
        participant.setPaymentConfirmedAt(LocalDateTime.now());
        
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(participantRepository.findByEventId(1)).thenReturn(Arrays.asList(participant));

        // Act
        Double result = participantService.getTotalConfirmedPayments(1);

        // Assert
        assertNotNull(result);
        assertEquals(10.0, result);
    }
}
