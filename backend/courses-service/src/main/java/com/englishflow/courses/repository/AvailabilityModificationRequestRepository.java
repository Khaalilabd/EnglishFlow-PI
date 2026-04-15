package com.englishflow.courses.repository;

import com.englishflow.courses.entity.AvailabilityModificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvailabilityModificationRequestRepository extends JpaRepository<AvailabilityModificationRequest, Long> {
    
    List<AvailabilityModificationRequest> findByTutorIdOrderByRequestedAtDesc(Long tutorId);
    
    List<AvailabilityModificationRequest> findByStatusOrderByRequestedAtDesc(AvailabilityModificationRequest.RequestStatus status);
    
    List<AvailabilityModificationRequest> findAllByOrderByRequestedAtDesc();
}
