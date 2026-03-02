package com.englishflow.messaging.mapper;

import com.englishflow.messaging.dto.ConversationDTO;
import com.englishflow.messaging.model.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConversationMapper {
    
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "lastMessage", ignore = true)
    @Mapping(target = "unreadCount", ignore = true)
    ConversationDTO toDTO(Conversation conversation);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Conversation toEntity(ConversationDTO conversationDTO);
}
