package com.englishflow.event.service;

import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.exception.AlreadyParticipantException;
import com.englishflow.event.exception.EventFullException;
import com.englishflow.event.exception.ResourceNotFoundException;
import com.englishflow.event.repository.EventRepository;
import com.englishflow.event.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    
    @Transactional
    public ParticipantDTO joinEvent(Integer eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        // Check if user is already a participant
        if (participantRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new AlreadyParticipantException("User is already registered for this event");
        }
        
        // Check if event is full
        long currentCount = participantRepository.countByEventId(eventId);
        if (currentCount >= event.getMaxParticipants()) {
            throw new EventFullException("Event is full. Maximum participants reached.");
        }
        
        // Create participant
        Participant participant = new Participant();
        participant.setEvent(event);
        participant.setUserId(userId);
        
        Participant savedParticipant = participantRepository.save(participant);
        
        // Update current participants count
        event.setCurrentParticipants((int) (currentCount + 1));
        eventRepository.save(event);
        
        return convertToDTO(savedParticipant);
    }
    
    @Transactional
    public void leaveEvent(Integer eventId, Long userId) {
        Participant participant = participantRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        
        participantRepository.delete(participant);
        
        // Update current participants count
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        
        long currentCount = participantRepository.countByEventId(eventId);
        event.setCurrentParticipants((int) currentCount);
        eventRepository.save(event);
    }
    
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getEventParticipants(Integer eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        
        return participantRepository.findByEventId(eventId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getUserEvents(Long userId) {
        log.info("Fetching events for user: {}", userId);
        try {
            List<Participant> participants = participantRepository.findByUserId(userId);
            log.info("Found {} participants for user {}", participants.size(), userId);
            return participants.stream()
                    .map(this::convertToDTO)
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
    
    private ParticipantDTO convertToDTO(Participant participant) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(participant.getId());
        dto.setEventId(participant.getEvent().getId());
        dto.setUserId(participant.getUserId());
        dto.setJoinDate(participant.getJoinDate());
        return dto;
    }
}
