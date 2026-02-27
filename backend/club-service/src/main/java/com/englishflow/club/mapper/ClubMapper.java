package com.englishflow.club.mapper;

import com.englishflow.club.dto.ClubDTO;
import com.englishflow.club.dto.ClubWithRoleDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClubMapper {
    
    ClubDTO toDTO(Club club);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Club toEntity(ClubDTO clubDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ClubDTO clubDTO, @MappingTarget Club club);
    
    @Mapping(source = "club.id", target = "id")
    @Mapping(source = "club.name", target = "name")
    @Mapping(source = "club.description", target = "description")
    @Mapping(source = "club.objective", target = "objective")
    @Mapping(source = "club.category", target = "category")
    @Mapping(source = "club.maxMembers", target = "maxMembers")
    @Mapping(source = "club.image", target = "image")
    @Mapping(source = "club.status", target = "status")
    @Mapping(source = "club.createdBy", target = "createdBy")
    @Mapping(source = "club.reviewedBy", target = "reviewedBy")
    @Mapping(source = "club.reviewComment", target = "reviewComment")
    @Mapping(source = "club.createdAt", target = "createdAt")
    @Mapping(source = "club.updatedAt", target = "updatedAt")
    @Mapping(source = "member.rank", target = "userRole")
    @Mapping(source = "member.joinedAt", target = "joinedAt")
    ClubWithRoleDTO toClubWithRoleDTO(Club club, Member member);
}
