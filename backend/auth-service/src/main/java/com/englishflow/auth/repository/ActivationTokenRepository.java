package com.englishflow.auth.repository;

import com.englishflow.auth.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    Optional<ActivationToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM ActivationToken a WHERE a.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
