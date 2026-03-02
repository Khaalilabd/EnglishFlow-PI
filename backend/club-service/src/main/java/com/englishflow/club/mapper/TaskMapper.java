package com.englishflow.club.mapper;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    
    @Mapping(source = "club.id", target = "clubId")
    TaskDTO toDTO(Task task);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "club", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskDTO taskDTO);
}
