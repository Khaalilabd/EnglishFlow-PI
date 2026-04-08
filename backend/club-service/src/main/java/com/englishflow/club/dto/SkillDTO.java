package com.englishflow.club.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO {
    
    private Integer id;
    
    @NotBlank(message = "Skill name is required")
    private String name;
    
    private String description;
    
    private Integer clubId;
    
    private LocalDateTime createdAt;
}
