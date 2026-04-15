package com.englishflow.courses.service;

import com.englishflow.courses.dto.OnlineMeetingSessionDTO;
import com.englishflow.courses.entity.OnlineMeetingSession;
import com.englishflow.courses.repository.OnlineMeetingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineMeetingSessionService {
    
    private final OnlineMeetingSessionRepository sessionRepository;
    
    @Transactional
    public OnlineMeetingSessionDTO createSession(Long lessonId, String roomId, String inviteLink, Long tutorId) {
        log.info("Creating meeting session for lesson {} with roomId {}", lessonId, roomId);
        
        // Check if there's already an active session for this lesson
        Optional<OnlineMeetingSession> existingSession = sessionRepository.findByLessonIdAndIsActiveTrue(lessonId);
        if (existingSession.isPresent()) {
            log.warn("Active session already exists for lesson {}", lessonId);
            return mapToDTO(existingSession.get());
        }
        
        OnlineMeetingSession session = new OnlineMeetingSession();
        session.setLessonId(lessonId);
        session.setRoomId(roomId);
        session.setInviteLink(inviteLink);
        session.setTutorId(tutorId);
        session.setIsActive(true);
        
        OnlineMeetingSession saved = sessionRepository.save(session);
        log.info("Meeting session created with ID: {}", saved.getId());
        
        return mapToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public Optional<OnlineMeetingSessionDTO> getActiveSessionByLessonId(Long lessonId) {
        return sessionRepository.findByLessonIdAndIsActiveTrue(lessonId)
                .map(this::mapToDTO);
    }
    
    @Transactional(readOnly = true)
    public Optional<OnlineMeetingSessionDTO> getActiveSessionByRoomId(String roomId) {
        return sessionRepository.findByRoomIdAndIsActiveTrue(roomId)
                .map(this::mapToDTO);
    }
    
    @Transactional
    public void endSession(Long lessonId) {
        log.info("Ending meeting session for lesson {}", lessonId);
        
        Optional<OnlineMeetingSession> session = sessionRepository.findByLessonIdAndIsActiveTrue(lessonId);
        if (session.isPresent()) {
            OnlineMeetingSession meetingSession = session.get();
            meetingSession.setIsActive(false);
            meetingSession.setEndedAt(LocalDateTime.now());
            sessionRepository.save(meetingSession);
            log.info("Meeting session ended for lesson {}", lessonId);
        } else {
            log.warn("No active session found for lesson {}", lessonId);
        }
    }
    
    @Transactional
    public void endSessionByRoomId(String roomId) {
        log.info("Ending meeting session for room {}", roomId);
        
        Optional<OnlineMeetingSession> session = sessionRepository.findByRoomIdAndIsActiveTrue(roomId);
        if (session.isPresent()) {
            OnlineMeetingSession meetingSession = session.get();
            meetingSession.setIsActive(false);
            meetingSession.setEndedAt(LocalDateTime.now());
            sessionRepository.save(meetingSession);
            log.info("Meeting session ended for room {}", roomId);
        } else {
            log.warn("No active session found for room {}", roomId);
        }
    }
    
    private OnlineMeetingSessionDTO mapToDTO(OnlineMeetingSession session) {
        OnlineMeetingSessionDTO dto = new OnlineMeetingSessionDTO();
        dto.setId(session.getId());
        dto.setLessonId(session.getLessonId());
        dto.setRoomId(session.getRoomId());
        dto.setInviteLink(session.getInviteLink());
        dto.setTutorId(session.getTutorId());
        dto.setStartedAt(session.getStartedAt());
        dto.setEndedAt(session.getEndedAt());
        dto.setIsActive(session.getIsActive());
        return dto;
    }
}
