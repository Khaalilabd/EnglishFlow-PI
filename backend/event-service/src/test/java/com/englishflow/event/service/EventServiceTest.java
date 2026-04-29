package com.englishflow.event.service;

import com.englishflow.event.client.ClubServiceClient;
import com.englishflow.event.client.SponsorServiceClient;
import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.enums.EventStatus;
import com.englishflow.event.enums.EventType;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.EventMapper;
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
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private PermissionService permissionService;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private ClubServiceClient clubServiceClient;

    @Mock
    private SponsorServiceClient sponsorServiceClient;

    @Mock
    private WebSocketNotificationService wsNotificationService;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDTO eventDTO;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(1);
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setType(EventType.WORKSHOP);
        event.setStatus(EventStatus.PENDING);
        event.setStartDate(LocalDateTime.now().plusDays(1));
        event.setEndDate(LocalDateTime.now().plusDays(2));
        event.setMaxParticipants(50);
        event.setCurrentParticipants(0);
        event.setCreatorId(1L);

        eventDTO = new EventDTO();
        eventDTO.setId(1);
        eventDTO.setTitle("Test Event");
        eventDTO.setDescription("Test Description");
        eventDTO.setType(EventType.WORKSHOP);
        eventDTO.setStatus(EventStatus.PENDING);
        eventDTO.setStartDate(LocalDateTime.now().plusDays(1));
        eventDTO.setEndDate(LocalDateTime.now().plusDays(2));
        eventDTO.setMaxParticipants(50);
        eventDTO.setCreatorId(1L);
    }

    @Test
    void getAllEvents_ShouldReturnEventList() {
        // Arrange
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findAll();
        verify(eventMapper).toDTO(event);
    }

    @Test
    void getEventById_WhenEventExists_ShouldReturnEvent() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        EventDTO result = eventService.getEventById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository).findById(1);
    }

    @Test
    void getEventById_WhenEventNotFound_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            eventService.getEventById(999)
        );
    }

    @Test
    void getEventsByType_ShouldReturnFilteredEvents() {
        // Arrange
        when(eventRepository.findByType(EventType.WORKSHOP)).thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getEventsByType(EventType.WORKSHOP);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByType(EventType.WORKSHOP);
    }

    @Test
    void getUpcomingEvents_ShouldReturnFutureEvents() {
        // Arrange
        when(eventRepository.findByStartDateAfter(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getUpcomingEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByStartDateAfter(any(LocalDateTime.class));
    }

    @Test
    void getOngoingEvents_ShouldReturnCurrentEvents() {
        // Arrange
        event.setStartDate(LocalDateTime.now().minusHours(1));
        event.setEndDate(LocalDateTime.now().plusHours(1));
        when(eventRepository.findByStartDateBeforeAndEndDateAfter(any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getOngoingEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByStartDateBeforeAndEndDateAfter(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getPastEvents_ShouldReturnEndedEvents() {
        // Arrange
        event.setEndDate(LocalDateTime.now().minusDays(1));
        when(eventRepository.findByEndDateBefore(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getPastEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    void getEventsByCreator_ShouldReturnCreatorEvents() {
        // Arrange
        Long creatorId = 1L;
        when(eventRepository.findByCreatorId(creatorId)).thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        List<EventDTO> result = eventService.getEventsByCreator(creatorId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByCreatorId(creatorId);
    }

    @Test
    void createEvent_ShouldCreateAndReturnEvent() {
        // Arrange
        doNothing().when(permissionService).checkEventCreationPermission(anyLong());
        when(eventMapper.toEntity(any(EventDTO.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);
        doNothing().when(wsNotificationService).notifyEventCreated(anyLong(), anyString());

        // Act
        EventDTO result = eventService.createEvent(eventDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository).save(any(Event.class));
        verify(wsNotificationService).notifyEventCreated(anyLong(), anyString());
    }

    @Test
    void updateEvent_WhenEventNotStarted_ShouldUpdateEvent() {
        // Arrange
        event.setStartDate(LocalDateTime.now().plusDays(1));
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);
        doNothing().when(eventMapper).updateEntityFromDTO(any(EventDTO.class), any(Event.class));
        doNothing().when(wsNotificationService).notifyEventUpdated(anyLong(), anyString());

        // Act
        EventDTO result = eventService.updateEvent(1, eventDTO);

        // Assert
        assertNotNull(result);
        verify(eventRepository).save(any(Event.class));
        verify(wsNotificationService).notifyEventUpdated(anyLong(), anyString());
    }

    @Test
    void deleteEvent_ShouldDeleteEvent() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        doNothing().when(eventRepository).deleteById(1);
        doNothing().when(wsNotificationService).notifyEventCancelled(anyLong(), anyString());

        // Act
        eventService.deleteEvent(1);

        // Assert
        verify(eventRepository).deleteById(1);
        verify(wsNotificationService).notifyEventCancelled(anyLong(), anyString());
    }

    @Test
    void approveEvent_ShouldSetStatusToApproved() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        EventDTO result = eventService.approveEvent(1);

        // Assert
        assertNotNull(result);
        assertEquals(EventStatus.APPROVED, event.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void rejectEvent_ShouldSetStatusToRejected() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(eventDTO);

        // Act
        EventDTO result = eventService.rejectEvent(1);

        // Assert
        assertNotNull(result);
        assertEquals(EventStatus.REJECTED, event.getStatus());
        verify(eventRepository).save(event);
    }
}
