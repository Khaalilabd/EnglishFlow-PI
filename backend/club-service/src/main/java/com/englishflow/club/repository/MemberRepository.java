package com.englishflow.club.repository;

import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    
    List<Member> findByClubId(Integer clubId);
    
    List<Member> findByUserId(Long userId);
    
    Optional<Member> findByClubIdAndUserId(Integer clubId, Long userId);
    
    List<Member> findByClubIdAndRank(Integer clubId, RankType rank);
    
    boolean existsByClubIdAndUserId(Integer clubId, Long userId);
    
    long countByClubId(Integer clubId);
}
