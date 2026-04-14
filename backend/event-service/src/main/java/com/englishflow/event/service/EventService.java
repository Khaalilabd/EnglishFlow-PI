package com.englishflow.event.service;

import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.dto.MemberDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.enums.EventType;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.EventMapper;
import com.englishflow.event.repository.EventRepository;
import com.englishflow.event.repository.ParticipantRepository;
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
    private final com.englishflow.event.repository.ParticipantRepository participantRepository;
    private final PermissionService permissionService;
    private final EventMapper eventMapper;
    private final com.englishflow.event.client.ClubServiceClient clubServiceClient;
    private final com.englishflow.event.client.SponsorServiceClient sponsorServiceClient;
    private final WebSocketNotificationService wsNotificationService; // ← Ajout WebSocket
    
    @Cacheable(value = "events", key = "'all'")
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        log.info("Fetching all events from database");
        try {
            List<Event> events = eventRepository.findAll();
            log.info("Found {} events", events.size());
            return events.stream()
                    .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
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
        return enrichEventWithClubName(eventMapper.toDTO(event));
    }
    
    @Cacheable(value = "eventsByType", key = "#type")
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByType(EventType type) {
        log.debug("Fetching events by type: {}", type);
        return eventRepository.findByType(type).stream()
                .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
                .collect(Collectors.toList());
    }
    
    @Cacheable(value = "upcomingEvents")
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        log.info("Fetching upcoming events (not started yet)");
        try {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Current time: {}", now);
            // Événements qui n'ont pas encore commencé
            List<Event> events = eventRepository.findByStartDateAfter(now);
            log.info("Found {} upcoming events", events.size());
            return events.stream()
                    .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
                    .sorted((e1, e2) -> e1.getStartDate().compareTo(e2.getStartDate())) // Tri par date de début la plus proche
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching upcoming events", e);
            throw e;
        }
    }
    
    @Cacheable(value = "ongoingEvents")
    @Transactional(readOnly = true)
    public List<EventDTO> getOngoingEvents() {
        log.info("Fetching ongoing events (happening today)");
        try {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Current time: {}", now);
            // Événements en cours : startDate <= now AND endDate >= now
            List<Event> events = eventRepository.findByStartDateBeforeAndEndDateAfter(now, now);
            log.info("Found {} ongoing events", events.size());
            return events.stream()
                    .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching ongoing events", e);
            throw e;
        }
    }
    
    @Cacheable(value = "pastEvents")
    @Transactional(readOnly = true)
    public List<EventDTO> getPastEvents() {
        log.info("Fetching past events (ended)");
        try {
            LocalDateTime now = LocalDateTime.now();
            log.debug("Current time: {}", now);
            // Événements terminés : endDate < now
            List<Event> events = eventRepository.findByEndDateBefore(now);
            log.info("Found {} past events", events.size());
            return events.stream()
                    .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
                    .sorted((e1, e2) -> e2.getEndDate().compareTo(e1.getEndDate())) // Tri par date de fin la plus récente
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching past events", e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByCreator(Long creatorId) {
        log.info("Fetching events created by user: {}", creatorId);
        return eventRepository.findByCreatorId(creatorId).stream()
                .map(event -> enrichEventWithClubName(eventMapper.toDTO(event)))
                .collect(Collectors.toList());
    }
    
    /**
     * Enriches an EventDTO with club name if clubId is present but clubName is missing
     */
    private EventDTO enrichEventWithClubName(EventDTO eventDTO) {
        if (eventDTO.getClubId() != null && (eventDTO.getClubName() == null || eventDTO.getClubName().isEmpty())) {
            try {
                var club = clubServiceClient.getClubById(eventDTO.getClubId());
                eventDTO.setClubName(club.getName());
                log.debug("Enriched event {} with club name: {}", eventDTO.getId(), club.getName());
            } catch (Exception e) {
                log.warn("Could not fetch club name for event {} with clubId {}", eventDTO.getId(), eventDTO.getClubId(), e);
                eventDTO.setClubName("Unknown Club");
            }
        }
        
        // Enrich with sponsor data
        enrichEventWithSponsors(eventDTO);
        
        return eventDTO;
    }
    
    /**
     * Enriches an EventDTO with sponsor details from sponsor-service
     */
    private void enrichEventWithSponsors(EventDTO eventDTO) {
        if (eventDTO.getSponsorIds() != null && !eventDTO.getSponsorIds().isEmpty()) {
            try {
                java.util.List<com.englishflow.event.dto.EventSponsorDTO> sponsors = new java.util.ArrayList<>();
                for (Long sponsorId : eventDTO.getSponsorIds()) {
                    try {
                        var sponsor = sponsorServiceClient.getSponsorById(sponsorId);
                        sponsors.add(sponsor);
                    } catch (Exception e) {
                        log.warn("Could not fetch sponsor {} for event {}", sponsorId, eventDTO.getId(), e);
                    }
                }
                eventDTO.setSponsors(sponsors);
                log.debug("Enriched event {} with {} sponsors", eventDTO.getId(), sponsors.size());
            } catch (Exception e) {
                log.warn("Error enriching event {} with sponsors", eventDTO.getId(), e);
            }
        }
    }
    
    @Caching(evict = {
        @CacheEvict(value = "events", key = "'all'"),
        @CacheEvict(value = "eventsByType", allEntries = true),
        @CacheEvict(value = "upcomingEvents", allEntries = true),
        @CacheEvict(value = "ongoingEvents", allEntries = true),
        @CacheEvict(value = "pastEvents", allEntries = true)
    })
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO) {
        log.info("Creating new event: {}", eventDTO.getTitle());
        permissionService.checkEventCreationPermission(eventDTO.getCreatorId());
        
        // Récupérer le club de l'utilisateur (utiliser clubId du frontend si fourni)
        if (eventDTO.getClubId() != null) {
            try {
                var club = clubServiceClient.getClubById(eventDTO.getClubId());
                eventDTO.setClubName(club.getName());
                log.info("Event will be created for club: {} (ID: {})", club.getName(), eventDTO.getClubId());
            } catch (Exception e) {
                log.warn("Could not fetch club name for clubId: {}", eventDTO.getClubId(), e);
            }
        } else {
            try {
                var memberships = clubServiceClient.getMembersByUserId(eventDTO.getCreatorId());
                if (!memberships.isEmpty()) {
                    var membership = memberships.stream()
                        .filter(m -> m.getRank() != null &&
                            (m.getRank().name().equals("PRESIDENT") ||
                             m.getRank().name().equals("VICE_PRESIDENT") ||
                             m.getRank().name().equals("EVENT_MANAGER")))
                        .findFirst();

                    if (membership.isPresent()) {
                        Integer clubId = membership.get().getClubId();
                        eventDTO.setClubId(clubId);
                        try {
                            var club = clubServiceClient.getClubById(clubId);
                            eventDTO.setClubName(club.getName());
                            log.info("Event will be created for club: {} (ID: {})", club.getName(), clubId);
                        } catch (Exception e) {
                            log.warn("Could not fetch club name for clubId: {}", clubId, e);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Could not fetch club information for user: {}", eventDTO.getCreatorId(), e);
            }
        }
        
        Event event = eventMapper.toEntity(eventDTO);
        event.setCurrentParticipants(0);
        Event savedEvent = eventRepository.save(event);

        // Auto-register VICE_PRESIDENT, TREASURER, EVENT_MANAGER as default participants
        if (savedEvent.getClubId() != null) {
            try {
                List<MemberDTO> clubMembers = clubServiceClient.getMembersByClubId(savedEvent.getClubId());
                List<String> autoRegisterRanks = java.util.Arrays.asList("VICE_PRESIDENT", "TREASURER", "EVENT_MANAGER");
                for (MemberDTO member : clubMembers) {
                    if (member.getRank() != null && autoRegisterRanks.contains(member.getRank().name())) {
                        Long memberId = member.getUserId();
                        if (!participantRepository.existsByEventIdAndUserId(savedEvent.getId(), memberId)) {
                            Participant p = new Participant();
                            p.setEvent(savedEvent);
                            p.setUserId(memberId);
                            p.setClubRole(member.getRank().name());
                            participantRepository.save(p);
                            savedEvent.setCurrentParticipants(savedEvent.getCurrentParticipants() + 1);
                        }
                    }
                }
                eventRepository.save(savedEvent);
                log.info("Auto-registered {} default participants for event {}", savedEvent.getCurrentParticipants(), savedEvent.getId());
            } catch (Exception e) {
                log.warn("Could not auto-register club members for event {}: {}", savedEvent.getId(), e.getMessage());
            }
        }
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.notifyEventCreated(
            savedEvent.getId().longValue(),
            savedEvent.getTitle()
        );
        
        log.info("Event created successfully by user: {}", eventDTO.getCreatorId());
        return enrichEventWithClubName(eventMapper.toDTO(savedEvent));
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

        // Block modification if event is ongoing or ended
        LocalDateTime now = LocalDateTime.now();
        if (!event.getStartDate().isAfter(now)) {
            throw new com.englishflow.event.exception.UnauthorizedException(
                "Cannot modify an event that has already started or ended"
            );
        }

        eventMapper.updateEntityFromDTO(eventDTO, event);

        // Reset status to PENDING so Academic Affairs can review the modification
        event.setStatus(com.englishflow.event.enums.EventStatus.PENDING);
        log.info("Event {} status reset to PENDING after modification", id);

        Event updatedEvent = eventRepository.save(event);
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.notifyEventUpdated(
            updatedEvent.getId().longValue(),
            updatedEvent.getTitle()
        );
        
        log.info("Event updated successfully: {}", id);
        return enrichEventWithClubName(eventMapper.toDTO(updatedEvent));
    }
    
    @Caching(evict = {
        @CacheEvict(value = "events", key = "'all'"),
        @CacheEvict(value = "eventById", key = "#id"),
        @CacheEvict(value = "eventsByType", allEntries = true),
        @CacheEvict(value = "upcomingEvents", allEntries = true),
        @CacheEvict(value = "ongoingEvents", allEntries = true),
        @CacheEvict(value = "pastEvents", allEntries = true)
    })
    @Transactional
    public void deleteEvent(Integer id) {
        log.info("Deleting event id: {}", id);
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        
        String eventTitle = event.getTitle();
        eventRepository.deleteById(id);
        
        // 🔔 Envoyer notification WebSocket
        wsNotificationService.notifyEventCancelled(id.longValue(), eventTitle);
        
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
        return enrichEventWithClubName(eventMapper.toDTO(updatedEvent));
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
        return enrichEventWithClubName(eventMapper.toDTO(updatedEvent));
    }
    
    @CacheEvict(value = {"events", "eventById", "eventsByType", "upcomingEvents", "ongoingEvents", "pastEvents"}, allEntries = true)
    @Transactional
    public int syncClubNamesForAllEvents() {
        log.info("Syncing club names for all events");
        List<Event> events = eventRepository.findAll();
        int updated = 0;
        
        for (Event event : events) {
            if (event.getCreatorId() != null) {
                try {
                    var memberships = clubServiceClient.getMembersByUserId(event.getCreatorId());
                    if (!memberships.isEmpty()) {
                        var membership = memberships.stream()
                            .filter(m -> m.getRank() != null && 
                                (m.getRank().name().equals("PRESIDENT") || 
                                 m.getRank().name().equals("VICE_PRESIDENT") || 
                                 m.getRank().name().equals("EVENT_MANAGER")))
                            .findFirst();
                        
                        if (membership.isPresent()) {
                            Integer clubId = membership.get().getClubId();
                            try {
                                var club = clubServiceClient.getClubById(clubId);
                                event.setClubId(clubId);
                                event.setClubName(club.getName());
                                eventRepository.save(event);
                                updated++;
                                log.info("Updated event {} with club: {} (ID: {})", event.getId(), club.getName(), clubId);
                            } catch (Exception e) {
                                log.warn("Could not fetch club for event {}", event.getId(), e);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not sync club for event {}", event.getId(), e);
                }
            }
        }
        
        log.info("Synced {} events with club names", updated);
        return updated;
    }
}