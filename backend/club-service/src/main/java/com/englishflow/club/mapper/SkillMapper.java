package com.englishflow.club.mapper;

import com.englishflow.club.dto.SkillDTO;
import com.englishflow.club.entity.Skill;
import org.springframework.stereotype.Component;

@Component
public class SkillMapper {
    
    public SkillDTO toDTO(Skill skill) {
        if (skill == null) {
            return null;
        }
        
        return SkillDTO.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .clubId(skill.getClub() != null ? skill.getClub().getId() : null)
                .createdAt(skill.getCreatedAt())
                .build();
    }
    
    public Skill toEntity(SkillDTO skillDTO) {
        if (skillDTO == null) {
            return null;
        }
        
        return Skill.builder()
                .id(skillDTO.getId())
                .name(skillDTO.getName())
                .description(skillDTO.getDescription())
                .build();
    }
}
