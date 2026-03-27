package com.englishflow.courses.service;

import com.englishflow.courses.dto.TutorAvailabilityDTO;
import com.englishflow.courses.enums.TutorStatus;

import java.util.List;

public interface ITutorAvailabilityService {
    
    TutorAvailabilityDTO createOrUpdateAvailability(TutorAvailabilityDTO availabilityDTO);
    
    TutorAvailabilityDTO getByTutorId(Long tutorId);
    
    TutorAvailabilityDTO getById(Long id);
    
    List<TutorAvailabilityDTO> getAllAvailabilities();
    
    List<TutorAvailabilityDTO> getByStatus(TutorStatus status);
    
    List<TutorAvailabilityDTO> getAvailableTutorsByCategoryAndLevel(String category, String level);
    
    List<TutorAvailabilityDTO> getTutorsWithCapacity();
    
    void incrementStudentCount(Long tutorId);
    
    void decrementStudentCount(Long tutorId);
    
    void deleteAvailability(Long id);
}
