package com.englishflow.event.service;

import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.enums.EventType;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final EventRepository eventRepository;
    private final PermissionService permissionService;
    
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        log.info("Fetching all events");
        try {
            List<Event> events = eventRepository.findAll();
            log.info("Found {} events", events.size());
            return events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all events", e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public EventDTO getEventById(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToDTO(event);
    }
    
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByType(EventType type) {
        return eventRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        log.info("Fetching upcoming events");
        try {
            LocalDateTime now = LocalDateTime.now();
            log.info("Current time: {}", now);
            List<Event> events = eventRepository.findByEventDateAfter(now);
            log.info("Found {} upcoming events", events.size());
            return events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching upcoming events", e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByCreator(Long creatorId) {
        log.info("Fetching events created by user: {}", creatorId);
        return eventRepository.findByCreatorId(creatorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        // Vérifier les permissions avant de créer l'événement
        permissionService.checkEventCreationPermission(eventDTO.getCreatorId());
        
        Event event = convertToEntity(eventDTO);
        event.setCurrentParticipants(0);
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully by user: {}", eventDTO.getCreatorId());
        return convertToDTO(savedEvent);
    }
    
    @Transactional
    public EventDTO updateEvent(Integer id, EventDTO eventDTO) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setTitle(eventDTO.getTitle());
        event.setType(eventDTO.getType());
        event.setEventDate(eventDTO.getEventDate());
        event.setLocation(eventDTO.getLocation());
        event.setMaxParticipants(eventDTO.getMaxParticipants());
        event.setDescription(eventDTO.getDescription());
        
        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }
    
    @Transactional
    public void deleteEvent(Integer id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }
    
    @Transactional
    public EventDTO approveEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setStatus(com.englishflow.event.enums.EventStatus.APPROVED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} approved successfully", id);
        return convertToDTO(updatedEvent);
    }
    
    @Transactional
    public EventDTO rejectEvent(Integer id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setStatus(com.englishflow.event.enums.EventStatus.REJECTED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} rejected successfully", id);
        return convertToDTO(updatedEvent);
    }
    
    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setType(event.getType());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setMaxParticipants(event.getMaxParticipants());
        dto.setCurrentParticipants(event.getCurrentParticipants());
        dto.setDescription(event.getDescription());
        dto.setCreatorId(event.getCreatorId());
        dto.setImage(event.getImage());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());
        return dto;
    }
    
    private Event convertToEntity(EventDTO dto) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setType(dto.getType());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setMaxParticipants(dto.getMaxParticipants());
        event.setDescription(dto.getDescription());
        event.setCreatorId(dto.getCreatorId());
        event.setImage(dto.getImage());
        if (dto.getStatus() != null) {
            event.setStatus(dto.getStatus());
        }
        return event;
    }
}