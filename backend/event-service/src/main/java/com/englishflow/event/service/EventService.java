package com.englishflow.event.service;

import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.enums.EventType;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.EventMapper;
import com.englishflow.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final EventMapper eventMapper;
    
    @Cacheable(value = "events", key = "'all'")
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        log.info("Fetching all events from database");
        try {
            List<Event> events = eventRepository.findAll();
            log.info("Found {} events", events.size());
            return events.stream()
                    .map(eventMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all events", e);
            throw e;
        }
    }
    
    @Cacheable(value = "eventById", key = "#id")
    @Transactional(readOnly = true)
    public EventDTO getEventById(Integer id) {
        log.debug("Fetching event by id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return eventMapper.toDTO(event);
    }
    
    @Cacheable(value = "eventsByType", key = "#type")
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByType(EventType type) {
        log.debug("Fetching events by type: {}", type);
        return eventRepository.findByType(type).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "upcomingEvents")
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        log.info("Fetching upcoming events");
        try {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Current time: {}", now);
            List<Event> events = eventRepository.findByEventDateAfter(now);
            log.info("Found {} upcoming events", events.size());
            return events.stream()
                    .map(eventMapper::toDTO)
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
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Caching(evict = {
        @CacheEvict(value = "events", key = "'all'"),
        @CacheEvict(value = "eventsByType", allEntries = true),
        @CacheEvict(value = "upcomingEvents", allEntries = true)
    })
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        log.info("Creating new event: {}", eventDTO.getTitle());
        permissionService.checkEventCreationPermission(eventDTO.getCreatorId());
        
        Event event = eventMapper.toEntity(eventDTO);
        event.setCurrentParticipants(0);
        Event savedEvent = eventRepository.save(event);
        log.info("Event created successfully by user: {}", eventDTO.getCreatorId());
        return eventMapper.toDTO(savedEvent);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "events", key = "'all'"),
        @CacheEvict(value = "eventById", key = "#id"),
        @CacheEvict(value = "eventsByType", allEntries = true),
        @CacheEvict(value = "upcomingEvents", allEntries = true)
    })
    @Transactional
    public EventDTO updateEvent(Integer id, EventDTO eventDTO) {
        log.info("Updating event id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        eventMapper.updateEntityFromDTO(eventDTO, event);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event updated successfully: {}", id);
        return eventMapper.toDTO(updatedEvent);
    }
    
    @Caching(evict = {
        @CacheEvict(value = "events", key = "'all'"),
        @CacheEvict(value = "eventById", key = "#id"),
        @CacheEvict(value = "eventsByType", allEntries = true),
        @CacheEvict(value = "upcomingEvents", allEntries = true)
    })
    @Transactional
    public void deleteEvent(Integer id) {
        log.info("Deleting event id: {}", id);
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
        log.info("Event deleted successfully: {}", id);
    }
    
    @CacheEvict(value = {"eventById", "events"}, allEntries = true)
    @Transactional
    public EventDTO approveEvent(Integer id) {
        log.info("Approving event id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setStatus(com.englishflow.event.enums.EventStatus.APPROVED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} approved successfully", id);
        return eventMapper.toDTO(updatedEvent);
    }
    
    @CacheEvict(value = {"eventById", "events"}, allEntries = true)
    @Transactional
    public EventDTO rejectEvent(Integer id) {
        log.info("Rejecting event id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        event.setStatus(com.englishflow.event.enums.EventStatus.REJECTED);
        Event updatedEvent = eventRepository.save(event);
        log.info("Event {} rejected successfully", id);
        return eventMapper.toDTO(updatedEvent);
    }
}