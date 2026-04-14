package com.englishflow.event.service;

import com.englishflow.event.client.AuthServiceClient;
import com.englishflow.event.client.ClubServiceClient;
import com.englishflow.event.dto.ExpenseDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipantService {
    
    private final ParticipantRepository participantRepository;
    private final EventRepository eventRepository;
    private final ParticipantMapper participantMapper;
    private final WebSocketNotificationService wsNotificationService;
    private final AuthServiceClient authServiceClient;
    private final ClubServiceClient clubServiceClient;
    
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

        // Handle participation fee
        Double fee = event.getParticipationFee();
        boolean hasFee = fee != null && fee > 0;
        if (hasFee) {
            participant.setPaymentStatus("PAYMENT_PENDING");
            participant.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        } else {
            participant.setPaymentStatus("PAID");
        }
        
        Participant savedParticipant = participantRepository.save(participant);
        
        int newCount = (int) (currentCount + 1);
        event.setCurrentParticipants(newCount);
        eventRepository.save(event);
        
        wsNotificationService.notifyParticipantJoined(
            eventId.longValue(),
            event.getTitle(),
            userId,
            "User " + userId,
            newCount,
            event.getMaxParticipants()
        );
        
        log.info("User {} joined event {} — hasFee: {}", userId, eventId, hasFee);
        return participantMapper.toDTO(savedParticipant);
    }

    @Transactional
    public ParticipantDTO confirmPayment(Integer participantId, String paymentMethod, String paymentToken) {
        log.info("Confirming payment for event participant {} via {}", participantId, paymentMethod);

        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        if (!"PAYMENT_PENDING".equals(participant.getPaymentStatus())) {
            throw new RuntimeException("Participant is not awaiting payment");
        }

        participant.setPaymentStatus("PAID");
        participant.setPaymentMethod(paymentMethod);
        participant.setPaymentToken(paymentToken);
        participant.setPaymentConfirmedAt(LocalDateTime.now());

        Participant updated = participantRepository.save(participant);

        // Send confirmation email
        try {
            AuthServiceClient.UserInfo userInfo = authServiceClient.getUserById(participant.getUserId());
            if (userInfo != null) {
                Event event = participant.getEvent();
                String eventLink = "http://localhost:4200/user-panel/events/" + event.getId();
                authServiceClient.sendEventPaymentConfirmedEmail(
                    userInfo.getEmail(),
                    userInfo.getFirstName(),
                    event.getTitle(),
                    event.getParticipationFee(),
                    eventLink
                );
            }
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email for participant {}: {}", participantId, e.getMessage());
        }

        log.info("Payment confirmed for event participant {}", participantId);

        // Auto-create income entry in club treasury
        try {
            Event event = participant.getEvent();
            Double fee = event.getParticipationFee();
            if (fee != null && fee > 0 && event.getClubId() != null) {
                ExpenseDTO incomeEntry = ExpenseDTO.builder()
                        .clubId(event.getClubId())
                        .designation("Event fee from participant #" + participant.getUserId() + " — " + event.getTitle())
                        .amount(fee)
                        .expenseDate(LocalDateTime.now())
                        .createdBy(participant.getUserId())
                        .notes("EVENT_FEE_INCOME | payment: " + paymentToken)
                        .source("EVENT_FEE")
                        .build();
                clubServiceClient.createIncomeEntry(incomeEntry);
            }
        } catch (Exception e) {
            log.warn("Failed to create income entry for event payment: {}", e.getMessage());
        }

        return participantMapper.toDTO(updated);
    }

    @Transactional(readOnly = true)
    public ParticipantDTO getParticipantById(Integer participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));
        return participantMapper.toDTO(participant);
    }

    @Transactional(readOnly = true)
    public Double getTotalConfirmedPayments(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        Double fee = event.getParticipationFee();
        if (fee == null || fee <= 0) return 0.0;
        return participantRepository.findByEventId(eventId).stream()
                .filter(p -> "PAID".equals(p.getPaymentStatus()) && p.getPaymentConfirmedAt() != null)
                .mapToDouble(p -> fee)
                .sum();
    }

    @Transactional(readOnly = true)
    public Double getTotalConfirmedPaymentsByClub(Integer clubId) {
        return eventRepository.findAll().stream()
                .filter(e -> clubId.equals(e.getClubId()))
                .mapToDouble(event -> {
                    Double fee = event.getParticipationFee();
                    if (fee == null || fee <= 0) return 0.0;
                    return participantRepository.findByEventId(event.getId()).stream()
                            .filter(p -> "PAID".equals(p.getPaymentStatus()) && p.getPaymentConfirmedAt() != null)
                            .mapToDouble(p -> fee)
                            .sum();
                })
                .sum();
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
        
        int newCount = (int) participantRepository.countByEventId(eventId);
        event.setCurrentParticipants(newCount);
        eventRepository.save(event);
        
        wsNotificationService.notifyParticipantLeft(
            eventId.longValue(),
            event.getTitle(),
            userId,
            "User " + userId,
            newCount,
            event.getMaxParticipants()
        );
        
        log.info("User {} successfully left event {}", userId, eventId);
    }
    
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getEventParticipants(Integer eventId) {
        log.debug("Fetching participants for event: {}", eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with id: " + eventId);
        }
        
        List<ParticipantDTO> participants = participantRepository.findByEventId(eventId).stream()
                .map(p -> {
                    ParticipantDTO dto = participantMapper.toDTO(p);
                    dto.setClubRole(p.getClubRole());
                    return dto;
                })
                .collect(Collectors.toList());
        
        List<Long> userIds = participants.stream()
                .map(ParticipantDTO::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        if (!userIds.isEmpty()) {
            Map<Long, AuthServiceClient.UserInfo> userInfoMap = authServiceClient.getUsersByIds(userIds);
            participants.forEach(participant -> {
                AuthServiceClient.UserInfo userInfo = userInfoMap.get(participant.getUserId());
                if (userInfo != null) {
                    participant.setUserEmail(userInfo.getEmail());
                    participant.setUserFirstName(userInfo.getFirstName());
                    participant.setUserLastName(userInfo.getLastName());
                    participant.setUserProfilePhoto(userInfo.getProfilePhoto());
                }
            });
        }
        
        return participants;
    }
    
    @Transactional(readOnly = true)
    public List<ParticipantDTO> getUserEvents(Long userId) {
        log.info("Fetching events for user: {}", userId);
        List<Participant> participants = participantRepository.findByUserId(userId);
        return participants.stream()
                .map(participantMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public boolean isUserParticipant(Integer eventId, Long userId) {
        return participantRepository.existsByEventIdAndUserId(eventId, userId);
    }
}
