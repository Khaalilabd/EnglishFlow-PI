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
import static org.mockito.ArgumentMatchers.any;
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

    private Event testEvent;
    private EventDTO testEventDTO;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1);
        testEvent.setTitle("English Workshop");
        testEvent.setDescription("Learn English");
        testEvent.setType(EventType.WORKSHOP);
        testEvent.setStartDate(LocalDateTime.now().plusDays(7));
        testEvent.setEndDate(LocalDateTime.now().plusDays(7).plusHours(2));
        testEvent.setLocation("Room 101");
        testEvent.setMaxParticipants(30);
        testEvent.setCurrentParticipants(0);
        testEvent.setStatus(EventStatus.PENDING);
        testEvent.setCreatorId(1L);

        testEventDTO = new EventDTO();
        testEventDTO.setId(1);
        testEventDTO.setTitle("English Workshop");
        testEventDTO.setDescription("Learn English");
        testEventDTO.setType(EventType.WORKSHOP);
        testEventDTO.setStartDate(LocalDateTime.now().plusDays(7));
        testEventDTO.setEndDate(LocalDateTime.now().plusDays(7).plusHours(2));
        testEventDTO.setLocation("Room 101");
        testEventDTO.setMaxParticipants(30);
        testEventDTO.setCurrentParticipants(0);
        testEventDTO.setStatus(EventStatus.PENDING);
        testEventDTO.setCreatorId(1L);
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(testEventDTO);

        // Act
        List<EventDTO> result = eventService.getAllEvents();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void getEventById_WhenEventExists_ShouldReturnEvent() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toDTO(testEvent)).thenReturn(testEventDTO);

        // Act
        EventDTO result = eventService.getEventById(1);

        // Assert
        assertNotNull(result);
        assertEquals("English Workshop", result.getTitle());
        verify(eventRepository, times(1)).findById(1);
    }

    @Test
    void getEventById_WhenEventNotExists_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(999));
        verify(eventRepository, times(1)).findById(999);
    }

    @Test
    void createEvent_ShouldCreateAndReturnEvent() {
        // Arrange
        when(eventMapper.toEntity(testEventDTO)).thenReturn(testEvent);
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventMapper.toDTO(testEvent)).thenReturn(testEventDTO);
        doNothing().when(permissionService).checkEventCreationPermission(anyLong());
        doNothing().when(wsNotificationService).notifyEventCreated(anyLong(), anyString());

        // Act
        EventDTO result = eventService.createEvent(testEventDTO);

        // Assert
        assertNotNull(result);
        assertEquals("English Workshop", result.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
        verify(wsNotificationService, times(1)).notifyEventCreated(anyLong(), anyString());
    }

    @Test
    void getEventsByType_ShouldReturnFilteredEvents() {
        // Arrange
        List<Event> events = Arrays.asList(testEvent);
        when(eventRepository.findByType(EventType.WORKSHOP)).thenReturn(events);
        when(eventMapper.toDTO(any(Event.class))).thenReturn(testEventDTO);

        // Act
        List<EventDTO> result = eventService.getEventsByType(EventType.WORKSHOP);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByType(EventType.WORKSHOP);
    }

    @Test
    void deleteEvent_WhenEventExists_ShouldDeleteSuccessfully() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventRepository).deleteById(1);
        doNothing().when(wsNotificationService).notifyEventCancelled(anyLong(), anyString());

        // Act
        eventService.deleteEvent(1);

        // Assert
        verify(eventRepository, times(1)).deleteById(1);
        verify(wsNotificationService, times(1)).notifyEventCancelled(anyLong(), anyString());
    }

    @Test
    void deleteEvent_WhenEventNotExists_ShouldThrowException() {
        // Arrange
        when(eventRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> eventService.deleteEvent(999));
        verify(eventRepository, never()).deleteById(999);
    }

    @Test
    void approveEvent_ShouldUpdateStatusToApproved() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventMapper.toDTO(testEvent)).thenReturn(testEventDTO);

        // Act
        EventDTO result = eventService.approveEvent(1);

        // Assert
        assertNotNull(result);
        assertEquals(EventStatus.APPROVED, testEvent.getStatus());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void rejectEvent_ShouldUpdateStatusToRejected() {
        // Arrange
        when(eventRepository.findById(1)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);
        when(eventMapper.toDTO(testEvent)).thenReturn(testEventDTO);

        // Act
        EventDTO result = eventService.rejectEvent(1);

        // Assert
        assertNotNull(result);
        assertEquals(EventStatus.REJECTED, testEvent.getStatus());
        verify(eventRepository, times(1)).save(testEvent);
    }
}
