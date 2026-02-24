package com.englishflow.courses.service;

import com.englishflow.courses.dto.PackEnrollmentDTO;
import com.englishflow.courses.entity.Pack;
import com.englishflow.courses.entity.PackEnrollment;
import com.englishflow.courses.repository.PackEnrollmentRepository;
import com.englishflow.courses.repository.PackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackEnrollmentService implements IPackEnrollmentService {
    
    private final PackEnrollmentRepository enrollmentRepository;
    private final PackRepository packRepository;
    private final IPackService packService;
    private final ITutorAvailabilityService tutorAvailabilityService;
    
    @Override
    @Transactional
    public PackEnrollmentDTO enrollStudent(Long studentId, Long packId) {
        // Check if already enrolled
        if (isStudentEnrolled(studentId, packId)) {
            throw new RuntimeException("Student is already enrolled in this pack");
        }
        
        // Get pack details
        Pack pack = packRepository.findById(packId)
            .orElseThrow(() -> new RuntimeException("Pack not found with id: " + packId));
        
        // Check if pack is full
        if (pack.isFull()) {
            throw new RuntimeException("Pack is full");
        }
        
        // Check if enrollment is open
        if (!pack.isEnrollmentOpen()) {
            throw new RuntimeException("Enrollment is not open for this pack");
        }
        
        // Create enrollment
        PackEnrollment enrollment = new PackEnrollment();
        enrollment.setStudentId(studentId);
        enrollment.setStudentName("Student " + studentId); // Should be fetched from user service
        enrollment.setPackId(packId);
        enrollment.setPackName(pack.getName());
        enrollment.setPackCategory(pack.getCategory().toString());
        enrollment.setPackLevel(pack.getLevel().toString());
        enrollment.setTutorId(pack.getTutorId());
        enrollment.setTutorName(pack.getTutorName());
        enrollment.setTotalCourses(pack.getCourseIds() != null ? pack.getCourseIds().size() : 0);
        enrollment.setCompletedCourses(0);
        enrollment.setStatus("ACTIVE");
        enrollment.setIsActive(true);
        enrollment.setProgressPercentage(0);
        
        PackEnrollment saved = enrollmentRepository.save(enrollment);
        
        // Update pack enrollment count
        packService.incrementEnrollment(packId);
        
        // Update tutor student count
        tutorAvailabilityService.incrementStudentCount(pack.getTutorId());
        
        return toDTO(saved);
    }
    
    @Override
    public PackEnrollmentDTO getById(Long id) {
        return enrollmentRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + id));
    }
    
    @Override
    public List<PackEnrollmentDTO> getByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PackEnrollmentDTO> getByPackId(Long packId) {
        return enrollmentRepository.findByPackId(packId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PackEnrollmentDTO> getByTutorId(Long tutorId) {
        return enrollmentRepository.findByTutorId(tutorId).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PackEnrollmentDTO> getActiveEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentIdAndIsActive(studentId, true).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PackEnrollmentDTO updateProgress(Long enrollmentId, Integer progressPercentage) {
        PackEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
        
        enrollment.setProgressPercentage(progressPercentage);
        
        // Auto-complete if 100%
        if (progressPercentage >= 100) {
            enrollment.setCompletedAt(LocalDateTime.now());
            enrollment.setIsActive(false);
        }
        
        PackEnrollment updated = enrollmentRepository.save(enrollment);
        return toDTO(updated);
    }
    
    @Override
    @Transactional
    public void completeEnrollment(Long enrollmentId) {
        PackEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
        
        enrollment.setCompletedAt(LocalDateTime.now());
        enrollment.setIsActive(false);
        enrollment.setProgressPercentage(100);
        
        enrollmentRepository.save(enrollment);
    }
    
    @Override
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        PackEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new RuntimeException("Enrollment not found with id: " + enrollmentId));
        
        enrollment.setIsActive(false);
        enrollmentRepository.save(enrollment);
        
        // Update pack enrollment count
        packService.decrementEnrollment(enrollment.getPackId());
        
        // Update tutor student count
        tutorAvailabilityService.decrementStudentCount(enrollment.getTutorId());
    }
    
    @Override
    public boolean isStudentEnrolled(Long studentId, Long packId) {
        return enrollmentRepository.findByStudentIdAndPackId(studentId, packId).isPresent();
    }
    
    private PackEnrollmentDTO toDTO(PackEnrollment enrollment) {
        PackEnrollmentDTO dto = new PackEnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setStudentId(enrollment.getStudentId());
        dto.setStudentName(enrollment.getStudentName());
        dto.setPackId(enrollment.getPackId());
        dto.setPackName(enrollment.getPackName());
        dto.setPackCategory(enrollment.getPackCategory());
        dto.setPackLevel(enrollment.getPackLevel());
        dto.setTutorId(enrollment.getTutorId());
        dto.setTutorName(enrollment.getTutorName());
        dto.setTotalCourses(enrollment.getTotalCourses());
        dto.setCompletedCourses(enrollment.getCompletedCourses());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setCompletedAt(enrollment.getCompletedAt());
        dto.setStatus(enrollment.getStatus());
        dto.setProgressPercentage(enrollment.getProgressPercentage());
        dto.setIsActive(enrollment.getIsActive());
        return dto;
    }
}
