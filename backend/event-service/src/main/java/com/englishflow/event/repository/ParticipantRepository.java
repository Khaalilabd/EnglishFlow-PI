package com.englishflow.event.repository;

import com.englishflow.event.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    
    List<Participant> findByEventId(Integer eventId);
    
    List<Participant> findByUserId(Long userId);
    
    Optional<Participant> findByEventIdAndUserId(Integer eventId, Long userId);
    
    boolean existsByEventIdAndUserId(Integer eventId, Long userId);
    
    @Query("SELECT COUNT(p) FROM Participant p WHERE p.event.id = :eventId")
    long countByEventId(Integer eventId);
    
    void deleteByEventIdAndUserId(Integer eventId, Long userId);
}
