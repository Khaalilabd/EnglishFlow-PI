package com.englishflow.sponsors.mapper;

import com.englishflow.sponsors.dto.SponsorDTO;
import com.englishflow.sponsors.entity.Sponsor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SponsorMapper {
    
    SponsorDTO toDTO(Sponsor sponsor);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "status", ignore = true) // Status defaults to PENDING
    Sponsor toEntity(SponsorDTO sponsorDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "level", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntityFromDTO(SponsorDTO sponsorDTO, @MappingTarget Sponsor sponsor);
}
