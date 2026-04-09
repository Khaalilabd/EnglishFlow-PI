package com.englishflow.auth.repository;

import com.englishflow.auth.entity.InterviewSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewSchedule, Long> {

    /**
     * Récupère tous les rendez-vous d'un interviewer dans une période donnée
     */
    @Query("SELECT s FROM InterviewSchedule s WHERE s.interviewerId = :interviewerId " +
           "AND s.status = 'SCHEDULED' " +
           "AND s.scheduledStart >= :startDate " +
           "AND s.scheduledStart <= :endDate " +
           "ORDER BY s.scheduledStart ASC")
    List<InterviewSchedule> findScheduledInterviewsByInterviewerAndDateRange(
            @Param("interviewerId") Long interviewerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Vérifie s'il y a un conflit d'horaire pour un interviewer
     */
    @Query("SELECT COUNT(s) > 0 FROM InterviewSchedule s WHERE s.interviewerId = :interviewerId " +
           "AND s.status = 'SCHEDULED' " +
           "AND ((s.scheduledStart < :endTime AND s.scheduledEnd > :startTime))")
    boolean hasScheduleConflict(
            @Param("interviewerId") Long interviewerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * Récupère le rendez-vous par application ID et status
     */
    @Query("SELECT s FROM InterviewSchedule s WHERE s.application.id = :applicationId AND s.status = :status")
    Optional<InterviewSchedule> findByApplicationIdAndStatus(
            @Param("applicationId") Long applicationId, 
            @Param("status") InterviewSchedule.ScheduleStatus status
    );

    /**
     * Récupère tous les rendez-vous d'une application
     */
    @Query("SELECT s FROM InterviewSchedule s WHERE s.application.id = :applicationId ORDER BY s.createdAt DESC")
    List<InterviewSchedule> findByApplicationIdOrderByCreatedAtDesc(@Param("applicationId") Long applicationId);

    /**
     * Récupère le rendez-vous par Google Event ID
     */
    Optional<InterviewSchedule> findByGoogleEventId(String googleEventId);

    /**
     * Récupère les rendez-vous à venir pour un interviewer
     */
    @Query("SELECT s FROM InterviewSchedule s WHERE s.interviewerId = :interviewerId " +
           "AND s.status = 'SCHEDULED' " +
           "AND s.scheduledStart >= :now " +
           "ORDER BY s.scheduledStart ASC")
    List<InterviewSchedule> findUpcomingInterviewsByInterviewer(
            @Param("interviewerId") Long interviewerId,
            @Param("now") LocalDateTime now
    );
}
