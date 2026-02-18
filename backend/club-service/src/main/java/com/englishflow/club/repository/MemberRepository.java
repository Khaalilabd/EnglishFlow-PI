package com.englishflow.club.repository;

import com.englishflow.club.entity.Member;
import com.englishflow.club.enums.RankType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.club WHERE m.club.id = :clubId")
    List<Member> findByClubId(@Param("clubId") Integer clubId);
    
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.club WHERE m.userId = :userId")
    List<Member> findByUserId(@Param("userId") Long userId);
    
    Optional<Member> findByClubIdAndUserId(Integer clubId, Long userId);
    
    List<Member> findByClubIdAndRank(Integer clubId, RankType rank);
    
    boolean existsByClubIdAndUserId(Integer clubId, Long userId);
    
    long countByClubId(Integer clubId);
}
