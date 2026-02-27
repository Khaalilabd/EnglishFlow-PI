package com.englishflow.messaging.mapper;

import com.englishflow.messaging.dto.MessageDTO;
import com.englishflow.messaging.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MessageMapper {
    
    @Mapping(source = "conversation.id", target = "conversationId")
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "readBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    MessageDTO toDTO(Message message);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "readStatuses", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Message toEntity(MessageDTO messageDTO);
}
