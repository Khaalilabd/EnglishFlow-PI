package com.englishflow.sponsors.repository;

import com.englishflow.sponsors.entity.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Long> {
    List<Sponsor> findByLevel(Sponsor.SponsorLevel level);
    List<Sponsor> findByStatus(Sponsor.SponsorStatus status);
    List<Sponsor> findByUserId(Long userId);
}
