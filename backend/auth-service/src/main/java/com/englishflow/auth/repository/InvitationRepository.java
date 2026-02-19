package com.englishflow.auth.repository;

import com.englishflow.auth.entity.Invitation;
import com.englishflow.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    
    Optional<Invitation> findByToken(String token);
    
    Optional<Invitation> findByEmailAndUsedFalse(String email);
    
    List<Invitation> findByInvitedBy(Long invitedBy);
    
    List<Invitation> findByRole(User.Role role);
    
    List<Invitation> findByUsedFalse();
    
    List<Invitation> findByExpiryDateBeforeAndUsedFalse(LocalDateTime date);
    
    boolean existsByEmailAndUsedFalse(String email);
}
