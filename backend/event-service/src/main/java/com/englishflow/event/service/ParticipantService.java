package com.englishflow.event.service;

import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.exception.AlreadyParticipantException;
import com.englishflow.event.exception.EventFullException;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.mapper.ParticipantMapper;
import com.englishflow.event.repository.EventRepository;
import com.englishflow.event.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {
    
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ParticipantMapper participantMapper;
    
    @CacheEvict(value = {"participants", "eventById"}, allEntries = true)
    @Transactional
    public ParticipantDTO joinEvent(Integer eventId, Long userId) {
        log.info("User {} joining event {}", userId, eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        if (participantRepository.existsByEventIdAndUserId(eventId, userId)) {
            log.warn("User {} already registered for event {}", userId, eventId);
            throw new AlreadyParticipantException("User is already registered for this event");
        }
        
        long currentCount = participantRepository.countByEventId(eventId);
        if (currentCount >= event.getMaxParticipants()) {
            log.warn("Event {} is full", eventId);
            throw new EventFullException("Event is full. Maximum participants reached.");
        }
        
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUserId(userId);
        
        Participant savedParticipant = participantRepository.save(participant);
        
        event.setCurrentParticipants((int) (currentCount + 1));
        eventRepository.save(event);
        
        log.info("User {} successfully joined event {}", userId, eventId);
        return participantMapper.toDTO(savedParticipant);
    }
    
    @CacheEvict(value = {"participants", "eventById"}, allEntries = true)
    @Transactional
    public void leaveEvent(Integer eventId, Long userId) {
        log.info("User {} leaving event {}", userId, eventId);
        Participant participant = participantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        
        participantRepository.delete(participant);
        
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        long currentCount = participantRepository.countByEventId(eventId);
        event.setCurrentParticipants((int) currentCount);
        eventRepository.save(event);
        
        log.info("User {} successfully left event {}", userId, eventId);
    }
    
    @Cacheable(value = "participants", key = "'event-' + #eventId")
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getEventParticipants(Integer eventId) {
        log.debug("Fetching participants for event: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        
        return participantRepository.findByEventId(eventId).stream()
                .map(participantMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getUserEvents(Long userId) {
        log.info("Fetching events for user: {}", userId);
        try {
            List<Participant> participants = participantRepository.findByUserId(userId);
            log.info("Found {} participants for user {}", participants.size(), userId);
            return participants.stream()
                    .map(participantMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching events for user: {}", userId, e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isUserParticipant(Integer eventId, Long userId) {
        return participantRepository.existsByEventIdAndUserId(eventId, userId);
    }
}
