package com.englishflow.auth.repository;

import com.englishflow.auth.entity.TutorApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TutorApplicationRepository extends JpaRepository<TutorApplication, Long> {

    Optional<TutorApplication> findByEmail(String email);

    boolean existsByEmail(String email);

    List<TutorApplication> findByStatus(TutorApplication.ApplicationStatus status);

    List<TutorApplication> findByStatusIn(List<TutorApplication.ApplicationStatus> statuses);

    @Query("SELECT a FROM TutorApplication a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<TutorApplication> findByStatusOrderByCreatedAtDesc(@Param("status") TutorApplication.ApplicationStatus status);

    @Query("SELECT a FROM TutorApplication a WHERE a.reviewedBy = :reviewerId")
    List<TutorApplication> findByReviewedBy(@Param("reviewerId") Long reviewerId);

    @Query("SELECT COUNT(a) FROM TutorApplication a WHERE a.status = :status")
    Long countByStatus(@Param("status") TutorApplication.ApplicationStatus status);

    @Query("SELECT a FROM TutorApplication a WHERE a.submittedAt BETWEEN :startDate AND :endDate")
    List<TutorApplication> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM TutorApplication a WHERE a.interviewScheduledAt BETWEEN :startDate AND :endDate")
    List<TutorApplication> findUpcomingInterviews(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
