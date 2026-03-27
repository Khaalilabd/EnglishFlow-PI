package com.englishflow.club.repository;

import com.englishflow.club.entity.ClubUpdateRequest;
import com.englishflow.club.enums.UpdateRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubUpdateRequestRepository extends JpaRepository<ClubUpdateRequest, Integer> {
    
    List<ClubUpdateRequest> findByClubIdAndStatus(Integer clubId, UpdateRequestStatus status);
    
    List<ClubUpdateRequest> findByClubId(Integer clubId);
    
    Optional<ClubUpdateRequest> findFirstByClubIdAndStatusOrderByCreatedAtDesc(Integer clubId, UpdateRequestStatus status);
}
