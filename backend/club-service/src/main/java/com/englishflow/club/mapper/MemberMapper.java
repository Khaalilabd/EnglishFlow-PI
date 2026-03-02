package com.englishflow.club.mapper;

import com.englishflow.club.dto.MemberDTO;
import com.englishflow.club.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberMapper {
    
    @Mapping(source = "club.id", target = "clubId")
    MemberDTO toDTO(Member member);
}
