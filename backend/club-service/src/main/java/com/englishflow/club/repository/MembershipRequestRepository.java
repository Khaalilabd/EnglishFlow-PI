package com.englishflow.club.repository;

import com.englishflow.club.entity.MembershipRequest;
import com.englishflow.club.enums.MembershipRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Integer> {
    
    List<MembershipRequest> findByClubIdAndStatus(Integer clubId, MembershipRequestStatus status);

    List<MembershipRequest> findByClubId(Integer clubId);
    
    List<MembershipRequest> findByUserId(Long userId);
    
    Optional<MembershipRequest> findByClubIdAndUserId(Integer clubId, Long userId);
    
    boolean existsByClubIdAndUserIdAndStatus(Integer clubId, Long userId, MembershipRequestStatus status);
}
