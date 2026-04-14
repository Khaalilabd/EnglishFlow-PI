package com.englishflow.courses.service;

import com.englishflow.courses.dto.AvailableTimeSlotDTO;
import com.englishflow.courses.dto.TutorAvailableSlotsDTO;
import com.englishflow.courses.entity.LessonTimeAssignment;
import com.englishflow.courses.entity.TimeSlot;
import com.englishflow.courses.entity.TutorAvailability;
import com.englishflow.courses.enums.DayOfWeek;
import com.englishflow.courses.repository.LessonTimeAssignmentRepository;
import com.englishflow.courses.repository.TutorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorAvailabilitySlotService {
    
    private final TutorAvailabilityRepository availabilityRepository;
    private final LessonTimeAssignmentRepository assignmentRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @Transactional(readOnly = true)
    public TutorAvailableSlotsDTO getAvailableSlots(Long tutorId) {
        log.info("Fetching available slots for tutor: {}", tutorId);
        
        TutorAvailableSlotsDTO result = new TutorAvailableSlotsDTO();
        result.setTutorId(tutorId);
        
        try {
            // Check all availabilities first for debugging
            List<TutorAvailability> allAvailabilities = availabilityRepository.findAll();
            log.info("Total availabilities in database: {}", allAvailabilities.size());
            allAvailabilities.forEach(a -> log.info("Found availability: tutorId={}, tutorName={}", a.getTutorId(), a.getTutorName()));
            
            Optional<TutorAvailability> availabilityOpt = availabilityRepository.findByTutorId(tutorId);
            
            if (availabilityOpt.isEmpty()) {
                log.warn("No availability configured for tutor: {} after checking {} total records", tutorId, allAvailabilities.size());
                result.setHasAvailability(false);
                result.setTutorName("Unknown");
                return result;
            }
            
            TutorAvailability availability = availabilityOpt.get();
            result.setTutorName(availability.getTutorName());
            result.setHasAvailability(true);
            
            // Get all booked assignments for this tutor
            List<LessonTimeAssignment> bookedAssignments = assignmentRepository.findByTutorId(tutorId);
            
            // Build available slots list
            List<AvailableTimeSlotDTO> slots = new ArrayList<>();
            
            for (DayOfWeek day : availability.getAvailableDays()) {
                for (TimeSlot timeSlot : availability.getTimeSlots()) {
                    AvailableTimeSlotDTO slotDTO = new AvailableTimeSlotDTO();
                    slotDTO.setDayOfWeek(day);
                    slotDTO.setStartTime(timeSlot.getStartTime().format(TIME_FORMATTER));
                    slotDTO.setEndTime(timeSlot.getEndTime().format(TIME_FORMATTER));
                    
                    // Check if this slot is booked
                    Optional<LessonTimeAssignment> booking = bookedAssignments.stream()
                        .filter(a -> a.getDayOfWeek() == day && 
                                     a.getStartTime().equals(timeSlot.getStartTime()))
                        .findFirst();
                    
                    if (booking.isPresent()) {
                        slotDTO.setBooked(true);
                        // Safely get lesson ID
                        try {
                            slotDTO.setBookedByLessonId(booking.get().getLesson().getId());
                        } catch (Exception e) {
                            log.warn("Could not get lesson ID for booking, setting to null", e);
                            slotDTO.setBookedByLessonId(null);
                        }
                    } else {
                        slotDTO.setBooked(false);
                    }
                    
                    slots.add(slotDTO);
                }
            }
            
            result.setAvailableSlots(slots);
            log.info("Found {} total slots for tutor {}, {} are available", 
                     slots.size(), tutorId, slots.stream().filter(s -> !s.isBooked()).count());
            
            return result;
        } catch (Exception e) {
            log.error("Error fetching available slots for tutor {}: {}", tutorId, e.getMessage(), e);
            // Return empty result instead of throwing
            result.setHasAvailability(false);
            result.setTutorName("Unknown");
            return result;
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isSlotAvailable(Long tutorId, DayOfWeek dayOfWeek, LocalTime startTime) {
        return !assignmentRepository.existsByTutorIdAndDayOfWeekAndStartTime(tutorId, dayOfWeek, startTime);
    }
    
    @Transactional(readOnly = true)
    public boolean tutorHasAvailability(Long tutorId) {
        return availabilityRepository.findByTutorId(tutorId).isPresent();
    }
}
