package com.englishflow.complaints.repository;

import com.englishflow.complaints.entity.ComplaintMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintMessageRepository extends JpaRepository<ComplaintMessage, Long> {
    List<ComplaintMessage> findByComplaintIdOrderByTimestampAsc(Long complaintId);
    void deleteByComplaintId(Long complaintId);
}
