package com.englishflow.auth.repository;

import com.englishflow.auth.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by user ID with pagination
     */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find audit logs by user email with pagination
     */
    Page<AuditLog> findByUserEmailOrderByCreatedAtDesc(String userEmail, Pageable pageable);

    /**
     * Find audit logs by action with pagination
     */
    Page<AuditLog> findByActionOrderByCreatedAtDesc(AuditLog.AuditAction action, Pageable pageable);

    /**
     * Find audit logs by status with pagination
     */
    Page<AuditLog> findByStatusOrderByCreatedAtDesc(AuditLog.AuditStatus status, Pageable pageable);

    /**
     * Find audit logs by date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate ORDER BY a.createdAt DESC")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate, 
                                   Pageable pageable);

    /**
     * Find audit logs by IP address (for security analysis)
     */
    Page<AuditLog> findByIpAddressOrderByCreatedAtDesc(String ipAddress, Pageable pageable);

    /**
     * Find failed login attempts for a user in the last X hours
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userEmail = :email AND a.action = 'LOGIN_FAILED' AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AuditLog> findFailedLoginAttempts(@Param("email") String email, @Param("since") LocalDateTime since);

    /**
     * Find suspicious activities (multiple failed logins from same IP)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.ipAddress = :ipAddress AND a.status = 'FAILED' AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AuditLog> findSuspiciousActivities(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Count actions by user in the last X hours
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.createdAt >= :since")
    Long countUserActionsInPeriod(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find recent security events
     */
    @Query("SELECT a FROM AuditLog a WHERE a.action IN ('SUSPICIOUS_LOGIN_ATTEMPT', 'RATE_LIMIT_EXCEEDED', 'INVALID_TOKEN_USED', 'MULTIPLE_FAILED_LOGINS') AND a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentSecurityEvents(@Param("since") LocalDateTime since);

    /**
     * Get audit statistics for dashboard
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.createdAt >= :since GROUP BY a.action")
    List<Object[]> getAuditStatistics(@Param("since") LocalDateTime since);

    /**
     * Delete old audit logs (for cleanup)
     */
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :cutoffDate")
    void deleteOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Search audit logs by multiple criteria
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:userId IS NULL OR a.userId = :userId) AND " +
           "(:email IS NULL OR a.userEmail LIKE %:email%) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:ipAddress IS NULL OR a.ipAddress = :ipAddress) AND " +
           "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR a.createdAt <= :endDate) " +
           "ORDER BY a.createdAt DESC")
    Page<AuditLog> searchAuditLogs(@Param("userId") Long userId,
                                   @Param("email") String email,
                                   @Param("action") AuditLog.AuditAction action,
                                   @Param("status") AuditLog.AuditStatus status,
                                   @Param("ipAddress") String ipAddress,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   Pageable pageable);
}