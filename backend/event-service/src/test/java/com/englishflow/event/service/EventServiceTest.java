package com.englishflow.event.service;

import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.enums.EventStatus;
import com.englishflow.event.enums.EventType;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.EventMapper;
import com.englishflow.event.repository.EventRepository;
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
    private EventMapper eventMapper;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDTO eventDTO;
    private Long eventId = 1L;
    private Long userId = 100L;

    @BeforeEach
    void setUp() {
        event = new Event();
        event.setId(eventId);
        event.setTitle("English Workshop");
        event.setDescription("Learn English grammar");
        event.setEventType(EventType.WORKSHOP);
        event.setStatus(EventStatus.UPCOMING);
        event.setStartDate(LocalDateTime.now().plusDays(7));
        event.setEndDate(LocalDateTime.now().plusDays(7).plusHours(2));
        event.setMaxParticipants(50);
        event.setCreatedBy(userId);

        eventDTO = new EventDTO();
        eventDTO.setId(eventId);
        eventDTO.setTitle("English Workshop");
        eventDTO.setDescription("Learn English grammar");
        eventDTO.setEventType("WORKSHOP");
        eventDTO.setStatus("UPCOMING");
        eventDTO.setMaxParticipants(50);
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() {
        // Given
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        List<EventDTO> result = eventService.getAllEvents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("English Workshop", result.get(0).getTitle());
        verify(eventRepository).findAll();
    }

    @Test
    void getEventById_WhenExists_ShouldReturnEvent() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        EventDTO result = eventService.getEventById(eventId);

        // Then
        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("English Workshop", result.getTitle());
    }

    @Test
    void getEventById_WhenNotExists_ShouldThrowException() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> eventService.getEventById(eventId));
    }

    @Test
    void getEventsByType_ShouldReturnFilteredEvents() {
        // Given
        when(eventRepository.findByEventType(EventType.WORKSHOP))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        List<EventDTO> result = eventService.getEventsByType(EventType.WORKSHOP);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("WORKSHOP", result.get(0).getEventType());
    }

    @Test
    void getUpcomingEvents_ShouldReturnUpcomingEvents() {
        // Given
        when(eventRepository.findUpcomingEvents(any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        List<EventDTO> result = eventService.getUpcomingEvents();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UPCOMING", result.get(0).getStatus());
    }

    @Test
    void createEvent_WithValidPermission_ShouldCreateEvent() {
        // Given
        doNothing().when(permissionService).checkEventCreationPermission(userId);
        when(eventMapper.toEntity(eventDTO)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        EventDTO result = eventService.createEvent(eventDTO, userId);

        // Then
        assertNotNull(result);
        assertEquals("English Workshop", result.getTitle());
        verify(permissionService).checkEventCreationPermission(userId);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void createEvent_WithoutPermission_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("No permission"))
            .when(permissionService).checkEventCreationPermission(userId);

        // When & Then
        assertThrows(RuntimeException.class, 
            () -> eventService.createEvent(eventDTO, userId));
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void updateEvent_WhenExists_ShouldUpdateEvent() {
        // Given
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        EventDTO updateDTO = new EventDTO();
        updateDTO.setTitle("Updated Workshop");
        updateDTO.setDescription("Updated description");
        updateDTO.setMaxParticipants(60);

        // When
        EventDTO result = eventService.updateEvent(eventId, updateDTO);

        // Then
        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void deleteEvent_WhenExists_ShouldDelete() {
        // Given
        when(eventRepository.existsById(eventId)).thenReturn(true);

        // When
        eventService.deleteEvent(eventId);

        // Then
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void deleteEvent_WhenNotExists_ShouldThrowException() {
        // Given
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, 
            () -> eventService.deleteEvent(eventId));
        verify(eventRepository, never()).deleteById(eventId);
    }

    @Test
    void getEventsByClub_ShouldReturnClubEvents() {
        // Given
        Long clubId = 10L;
        when(eventRepository.findByClubId(clubId)).thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        List<EventDTO> result = eventService.getEventsByClub(clubId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getEventsByStatus_ShouldReturnFilteredEvents() {
        // Given
        when(eventRepository.findByStatus(EventStatus.UPCOMING))
            .thenReturn(Arrays.asList(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        List<EventDTO> result = eventService.getEventsByStatus(EventStatus.UPCOMING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("UPCOMING", result.get(0).getStatus());
    }
}
