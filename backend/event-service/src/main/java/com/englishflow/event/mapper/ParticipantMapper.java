package com.englishflow.event.mapper;

import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParticipantMapper {
    
    @Mapping(source = "event.id", target = "eventId")
    ParticipantDTO toDTO(Participant participant);
}
