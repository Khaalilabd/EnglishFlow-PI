package com.englishflow.courses.service;

import com.englishflow.courses.dto.TimeSlotDTO;
import com.englishflow.courses.dto.TutorAvailabilityDTO;
import com.englishflow.courses.entity.TimeSlot;
import com.englishflow.courses.entity.TutorAvailability;
import com.englishflow.courses.enums.TutorStatus;
import com.englishflow.courses.repository.TutorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorAvailabilityService implements ITutorAvailabilityService {
    
    private final TutorAvailabilityRepository availabilityRepository;
    
    @Override
    @Transactional
    public TutorAvailabilityDTO createOrUpdateAvailability(TutorAvailabilityDTO availabilityDTO) {
        TutorAvailability availability = availabilityRepository.findByTutorId(availabilityDTO.getTutorId())
            .orElse(new TutorAvailability());
        
        availability.setTutorId(availabilityDTO.getTutorId());
        availability.setTutorName(availabilityDTO.getTutorName());
        availability.setAvailableDays(availabilityDTO.getAvailableDays());
        availability.setMaxStudentsCapacity(availabilityDTO.getMaxStudentsCapacity());
        availability.setCategories(availabilityDTO.getCategories());
        availability.setLevels(availabilityDTO.getLevels());
        availability.setStatus(availabilityDTO.getStatus());
        
        // Clear existing time slots and add new ones
        availability.getTimeSlots().clear();
        if (availabilityDTO.getTimeSlots() != null) {
            for (TimeSlotDTO slotDTO : availabilityDTO.getTimeSlots()) {
                TimeSlot slot = new TimeSlot();
                slot.setStartTime(slotDTO.getStartTime());
                slot.setEndTime(slotDTO.getEndTime());
                slot.setTutorAvailability(availability);
                availability.getTimeSlots().add(slot);
            }
        }
        
        TutorAvailability saved = availabilityRepository.save(availability);
        return toDTO(saved);
    }
    
    @Override
    public TutorAvailabilityDTO getByTutorId(Long tutorId) {
        return availabilityRepository.findByTutorId(tutorId)
            .map(this::toDTO)
            .orElse(null);
    }
    
    @Override
    public TutorAvailabilityDTO getById(Long id) {
        return availabilityRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Tutor availability not found with id: " + id));
    }
    
    @Override
    public List<TutorAvailabilityDTO> getAllAvailabilities() {
        return availabilityRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TutorAvailabilityDTO> getByStatus(TutorStatus status) {
        return availabilityRepository.findByStatus(status).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TutorAvailabilityDTO> getAvailableTutorsByCategoryAndLevel(String category, String level) {
        return availabilityRepository.findAvailableTutorsByCategoryAndLevel(category, level).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<TutorAvailabilityDTO> getTutorsWithCapacity() {
        return availabilityRepository.findTutorsWithCapacity().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void incrementStudentCount(Long tutorId) {
        TutorAvailability availability = availabilityRepository.findByTutorId(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor availability not found for tutor: " + tutorId));
        
        availability.setCurrentStudentsCount(availability.getCurrentStudentsCount() + 1);
        
        // Update status based on capacity
        if (availability.getCurrentStudentsCount() >= availability.getMaxStudentsCapacity()) {
            availability.setStatus(TutorStatus.BUSY);
        }
        
        availabilityRepository.save(availability);
    }
    
    @Override
    @Transactional
    public void decrementStudentCount(Long tutorId) {
        TutorAvailability availability = availabilityRepository.findByTutorId(tutorId)
            .orElseThrow(() -> new RuntimeException("Tutor availability not found for tutor: " + tutorId));
        
        if (availability.getCurrentStudentsCount() > 0) {
            availability.setCurrentStudentsCount(availability.getCurrentStudentsCount() - 1);
            
            // Update status if capacity is available
            if (availability.getCurrentStudentsCount() < availability.getMaxStudentsCapacity() 
                && availability.getStatus() == TutorStatus.BUSY) {
                availability.setStatus(TutorStatus.AVAILABLE);
            }
            
            availabilityRepository.save(availability);
        }
    }
    
    @Override
    @Transactional
    public void deleteAvailability(Long id) {
        availabilityRepository.deleteById(id);
    }
    
    private TutorAvailabilityDTO toDTO(TutorAvailability availability) {
        TutorAvailabilityDTO dto = new TutorAvailabilityDTO();
        dto.setId(availability.getId());
        dto.setTutorId(availability.getTutorId());
        dto.setTutorName(availability.getTutorName());
        dto.setAvailableDays(availability.getAvailableDays());
        dto.setMaxStudentsCapacity(availability.getMaxStudentsCapacity());
        dto.setCurrentStudentsCount(availability.getCurrentStudentsCount());
        dto.setAvailableCapacity(availability.getAvailableCapacity());
        dto.setCapacityPercentage(availability.getCapacityPercentage());
        dto.setCategories(availability.getCategories());
        dto.setLevels(availability.getLevels());
        dto.setStatus(availability.getStatus());
        dto.setLastUpdated(availability.getLastUpdated());
        dto.setCreatedAt(availability.getCreatedAt());
        
        // Convert time slots
        List<TimeSlotDTO> timeSlotDTOs = availability.getTimeSlots().stream()
            .map(slot -> new TimeSlotDTO(slot.getId(), slot.getStartTime(), slot.getEndTime()))
            .collect(Collectors.toList());
        dto.setTimeSlots(timeSlotDTOs);
        
        return dto;
    }
}
