package com.englishflow.courses.service;

import com.englishflow.courses.dto.AvailabilityModificationRequestDTO;
import com.englishflow.courses.entity.AvailabilityModificationRequest;
import com.englishflow.courses.entity.TutorAvailability;
import com.englishflow.courses.repository.AvailabilityModificationRequestRepository;
import com.englishflow.courses.repository.TutorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilityModificationRequestService {
    
    private final AvailabilityModificationRequestRepository requestRepository;
    private final TutorAvailabilityRepository availabilityRepository;
    
    @Transactional
    public AvailabilityModificationRequestDTO createRequest(AvailabilityModificationRequestDTO dto) {
        AvailabilityModificationRequest request = new AvailabilityModificationRequest();
        request.setTutorId(dto.getTutorId());
        request.setTutorName(dto.getTutorName());
        request.setTutorEmail(dto.getTutorEmail());
        request.setReason(dto.getReason());
        request.setProposedAvailability(dto.getProposedAvailability());
        request.setStatus(AvailabilityModificationRequest.RequestStatus.PENDING);
        
        AvailabilityModificationRequest saved = requestRepository.save(request);
        return convertToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<AvailabilityModificationRequestDTO> getAllRequests() {
        return requestRepository.findAllByOrderByRequestedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AvailabilityModificationRequestDTO> getRequestsByTutor(Long tutorId) {
        return requestRepository.findByTutorIdOrderByRequestedAtDesc(tutorId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AvailabilityModificationRequestDTO> getPendingRequests() {
        return requestRepository.findByStatusOrderByRequestedAtDesc(
                AvailabilityModificationRequest.RequestStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AvailabilityModificationRequestDTO approveRequest(Long requestId, Long reviewerId, String reviewerName, String comment) {
        AvailabilityModificationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));
        
        if (request.getStatus() != AvailabilityModificationRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been reviewed");
        }
        
        // Unlock the tutor's availability
        TutorAvailability availability = availabilityRepository.findByTutorId(request.getTutorId())
                .orElseThrow(() -> new RuntimeException("Tutor availability not found"));
        
        availability.setLocked(false);
        availabilityRepository.save(availability);
        
        // Update request status
        request.setStatus(AvailabilityModificationRequest.RequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewerId(reviewerId);
        request.setReviewerName(reviewerName);
        request.setReviewComment(comment);
        
        AvailabilityModificationRequest updated = requestRepository.save(request);
        return convertToDTO(updated);
    }
    
    @Transactional
    public AvailabilityModificationRequestDTO rejectRequest(Long requestId, Long reviewerId, String reviewerName, String comment) {
        AvailabilityModificationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));
        
        if (request.getStatus() != AvailabilityModificationRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been reviewed");
        }
        
        request.setStatus(AvailabilityModificationRequest.RequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewerId(reviewerId);
        request.setReviewerName(reviewerName);
        request.setReviewComment(comment);
        
        AvailabilityModificationRequest updated = requestRepository.save(request);
        return convertToDTO(updated);
    }
    
    private AvailabilityModificationRequestDTO convertToDTO(AvailabilityModificationRequest request) {
        AvailabilityModificationRequestDTO dto = new AvailabilityModificationRequestDTO();
        dto.setId(request.getId());
        dto.setTutorId(request.getTutorId());
        dto.setTutorName(request.getTutorName());
        dto.setTutorEmail(request.getTutorEmail());
        dto.setReason(request.getReason());
        dto.setProposedAvailability(request.getProposedAvailability());
        dto.setStatus(request.getStatus().name());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setReviewedAt(request.getReviewedAt());
        dto.setReviewerId(request.getReviewerId());
        dto.setReviewerName(request.getReviewerName());
        dto.setReviewComment(request.getReviewComment());
        return dto;
    }
}
