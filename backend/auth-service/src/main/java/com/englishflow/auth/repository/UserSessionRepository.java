package com.englishflow.auth.repository;

import com.englishflow.auth.entity.UserSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Find session by token
     */
    Optional<UserSession> findBySessionToken(String sessionToken);

    /**
     * Find all active sessions for a user
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.status = 'ACTIVE' ORDER BY s.lastActivity DESC")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") Long userId);

    /**
     * Find all sessions for a user with pagination
     */
    Page<UserSession> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find sessions by status
     */
    Page<UserSession> findByStatusOrderByLastActivityDesc(UserSession.SessionStatus status, Pageable pageable);

    /**
     * Find suspicious sessions
     */
    @Query("SELECT s FROM UserSession s WHERE s.suspicious = true ORDER BY s.createdAt DESC")
    List<UserSession> findSuspiciousSessions();

    /**
     * Find sessions by IP address
     */
    List<UserSession> findByIpAddressOrderByCreatedAtDesc(String ipAddress);

    /**
     * Find concurrent sessions for a user (active sessions)
     */
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.userId = :userId AND s.status = 'ACTIVE'")
    Long countActiveSessionsByUserId(@Param("userId") Long userId);

    /**
     * Find sessions that should be marked as inactive (no activity for X minutes)
     */
    @Query("SELECT s FROM UserSession s WHERE s.status = 'ACTIVE' AND s.lastActivity < :cutoffTime")
    List<UserSession> findInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Find expired sessions
     */
    @Query("SELECT s FROM UserSession s WHERE s.status IN ('ACTIVE', 'INACTIVE') AND s.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);

    /**
     * Find sessions from same IP in last X hours (for suspicious activity detection)
     */
    @Query("SELECT s FROM UserSession s WHERE s.ipAddress = :ipAddress AND s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<UserSession> findRecentSessionsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Find sessions by device type
     */
    List<UserSession> findByDeviceTypeOrderByCreatedAtDesc(String deviceType);

    /**
     * Get session statistics for dashboard
     */
    @Query("SELECT s.status, COUNT(s) FROM UserSession s WHERE s.createdAt >= :since GROUP BY s.status")
    List<Object[]> getSessionStatistics(@Param("since") LocalDateTime since);

    /**
     * Get device statistics
     */
    @Query("SELECT s.deviceType, COUNT(s) FROM UserSession s WHERE s.createdAt >= :since GROUP BY s.deviceType")
    List<Object[]> getDeviceStatistics(@Param("since") LocalDateTime since);

    /**
     * Get geographic statistics
     */
    @Query("SELECT s.country, COUNT(s) FROM UserSession s WHERE s.createdAt >= :since AND s.country IS NOT NULL GROUP BY s.country ORDER BY COUNT(s) DESC")
    List<Object[]> getGeographicStatistics(@Param("since") LocalDateTime since);

    /**
     * Terminate all active sessions for a user
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = 'TERMINATED', s.terminationReason = :reason, s.terminatedAt = :now WHERE s.userId = :userId AND s.status = 'ACTIVE'")
    int terminateAllUserSessions(@Param("userId") Long userId, 
                                @Param("reason") UserSession.TerminationReason reason, 
                                @Param("now") LocalDateTime now);

    /**
     * Terminate specific session
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = 'TERMINATED', s.terminationReason = :reason, s.terminatedAt = :now WHERE s.id = :sessionId")
    int terminateSession(@Param("sessionId") Long sessionId, 
                        @Param("reason") UserSession.TerminationReason reason, 
                        @Param("now") LocalDateTime now);

    /**
     * Mark sessions as inactive
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = 'INACTIVE' WHERE s.id IN :sessionIds")
    int markSessionsAsInactive(@Param("sessionIds") List<Long> sessionIds);

    /**
     * Mark sessions as expired
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.status = 'EXPIRED' WHERE s.id IN :sessionIds")
    int markSessionsAsExpired(@Param("sessionIds") List<Long> sessionIds);

    /**
     * Delete old terminated sessions (cleanup)
     */
    @Modifying
    @Query("DELETE FROM UserSession s WHERE s.status = 'TERMINATED' AND s.terminatedAt < :cutoffDate")
    int deleteOldTerminatedSessions(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Search sessions with multiple criteria
     */
    @Query("SELECT s FROM UserSession s WHERE " +
           "(:userId IS NULL OR s.userId = :userId) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:deviceType IS NULL OR s.deviceType = :deviceType) AND " +
           "(:ipAddress IS NULL OR s.ipAddress = :ipAddress) AND " +
           "(:country IS NULL OR s.country = :country) AND " +
           "(:suspicious IS NULL OR s.suspicious = :suspicious) AND " +
           "(:startDate IS NULL OR s.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR s.createdAt <= :endDate) " +
           "ORDER BY s.lastActivity DESC")
    Page<UserSession> searchSessions(@Param("userId") Long userId,
                                   @Param("status") UserSession.SessionStatus status,
                                   @Param("deviceType") String deviceType,
                                   @Param("ipAddress") String ipAddress,
                                   @Param("country") String country,
                                   @Param("suspicious") Boolean suspicious,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);

    /**
     * Find sessions that exceed concurrent login limit
     */
    @Query("SELECT s FROM UserSession s WHERE s.userId IN " +
           "(SELECT s2.userId FROM UserSession s2 WHERE s2.status = 'ACTIVE' GROUP BY s2.userId HAVING COUNT(s2) > :limit) " +
           "AND s.status = 'ACTIVE' ORDER BY s.lastActivity ASC")
    List<UserSession> findSessionsExceedingLimit(@Param("limit") int limit);
}