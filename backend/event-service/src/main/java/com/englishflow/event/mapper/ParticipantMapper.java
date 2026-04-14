package com.englishflow.event.mapper;

import com.englishflow.event.dto.ParticipantDTO;
import com.englishflow.event.entity.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ParticipantMapper {
    
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.title", target = "eventTitle")
    @Mapping(source = "event.participationFee", target = "participationFee")
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "userFirstName", ignore = true)
    @Mapping(target = "userLastName", ignore = true)
    @Mapping(target = "userProfilePhoto", ignore = true)
    ParticipantDTO toDTO(Participant participant);
}
