package com.englishflow.club.repository;

import com.englishflow.club.entity.Club;
import com.englishflow.club.enums.ClubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Integer> {
    
    List<Club> findByCategory(ClubCategory category);
    
    List<Club> findByNameContainingIgnoreCase(String name);
}
