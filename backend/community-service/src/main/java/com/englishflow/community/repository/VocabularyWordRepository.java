package com.englishflow.community.repository;

import com.englishflow.community.entity.VocabularyWord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyWordRepository extends JpaRepository<VocabularyWord, Long> {
    
    Page<VocabularyWord> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    Optional<VocabularyWord> findByUserIdAndWord(Long userId, String word);
    
    boolean existsByUserIdAndWord(Long userId, String word);
    
    List<VocabularyWord> findByUserIdAndMasteryLevel(Long userId, VocabularyWord.MasteryLevel masteryLevel);
    
    @Query("SELECT COUNT(v) FROM VocabularyWord v WHERE v.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(v) FROM VocabularyWord v WHERE v.userId = :userId AND v.masteryLevel = :level")
    Long countByUserIdAndMasteryLevel(@Param("userId") Long userId, @Param("level") VocabularyWord.MasteryLevel level);
    
    @Query("SELECT v FROM VocabularyWord v WHERE v.userId = :userId AND LOWER(v.word) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<VocabularyWord> searchByUserIdAndWord(@Param("userId") Long userId, @Param("search") String search, Pageable pageable);
    
    Page<VocabularyWord> findByUserIdAndMasteryLevelOrderByCreatedAtDesc(Long userId, VocabularyWord.MasteryLevel masteryLevel, Pageable pageable);
}
