package com.englishflow.club.service;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Skill;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.mapper.SkillMapper;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillService {
    
    private final SkillRepository skillRepository;
    private final ClubRepository clubRepository;
    private final SkillMapper skillMapper;
    
    @Transactional(readOnly = true)
    public List<SkillDTO> getSkillsByClubId(Integer clubId) {
        log.debug("Fetching skills for club: {}", clubId);
        return skillRepository.findByClubId(clubId).stream()
                .map(skillMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SkillDTO addSkillToClub(Integer clubId, SkillDTO skillDTO) {
        log.info("Adding skill to club: {}", clubId);
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException(clubId));
        
        Skill skill = skillMapper.toEntity(skillDTO);
        skill.setClub(club);
        
        Skill savedSkill = skillRepository.save(skill);
        log.info("Skill added successfully to club: {}", clubId);
        return skillMapper.toDTO(savedSkill);
    }
    
    @Transactional
    public void deleteSkill(Integer skillId) {
        log.info("Deleting skill: {}", skillId);
        skillRepository.deleteById(skillId);
        log.info("Skill deleted successfully: {}", skillId);
    }
    
    @Transactional
    public void updateClubSkills(Integer clubId, List<SkillDTO> skillDTOs) {
        log.info("Updating skills for club: {}", clubId);
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException(clubId));
        
        // Supprimer les anciennes skills
        skillRepository.deleteByClubId(clubId);
        
        // Ajouter les nouvelles skills
        if (skillDTOs != null && !skillDTOs.isEmpty()) {
            List<Skill> skills = skillDTOs.stream()
                    .map(dto -> {
                        Skill skill = skillMapper.toEntity(dto);
                        skill.setClub(club);
                        return skill;
                    })
                    .collect(Collectors.toList());
            
            skillRepository.saveAll(skills);
        }
        
        log.info("Skills updated successfully for club: {}", clubId);
    }
}
