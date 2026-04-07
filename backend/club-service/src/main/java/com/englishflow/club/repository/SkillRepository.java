package com.englishflow.club.repository;

import com.englishflow.club.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    
    List<Skill> findByClubId(Integer clubId);
    
    void deleteByClubId(Integer clubId);
}
