package com.englishflow.event.mapper;

import com.englishflow.event.dto.EventDTO;
import com.englishflow.event.entity.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    
    EventDTO toDTO(Event event);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    Event toEntity(EventDTO eventDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    void updateEntityFromDTO(EventDTO eventDTO, @MappingTarget Event event);
}
