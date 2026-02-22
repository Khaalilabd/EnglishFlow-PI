package com.englishflow.complaints.repository;

import com.englishflow.complaints.entity.ComplaintNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintNotificationRepository extends JpaRepository<ComplaintNotification, Long> {
    List<ComplaintNotification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);
    List<ComplaintNotification> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
    long countByRecipientIdAndIsReadFalse(Long recipientId);
    void deleteByComplaintId(Long complaintId);
}
