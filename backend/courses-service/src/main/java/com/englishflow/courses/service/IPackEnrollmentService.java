package com.englishflow.courses.service;

import com.englishflow.courses.dto.PackEnrollmentDTO;

import java.util.List;

public interface IPackEnrollmentService {
    
    PackEnrollmentDTO enrollStudent(Long studentId, Long packId);
    
    PackEnrollmentDTO getById(Long id);
    
    List<PackEnrollmentDTO> getByStudentId(Long studentId);
    
    List<PackEnrollmentDTO> getByPackId(Long packId);
    
    List<PackEnrollmentDTO> getByTutorId(Long tutorId);
    
    List<PackEnrollmentDTO> getActiveEnrollmentsByStudent(Long studentId);
    
    PackEnrollmentDTO updateProgress(Long enrollmentId, Integer progressPercentage);
    
    void completeEnrollment(Long enrollmentId);
    
    void cancelEnrollment(Long enrollmentId);
    
    boolean isStudentEnrolled(Long studentId, Long packId);
}
