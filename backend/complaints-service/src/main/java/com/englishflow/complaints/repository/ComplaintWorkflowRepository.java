package com.englishflow.complaints.repository;

import com.englishflow.complaints.entity.ComplaintWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintWorkflowRepository extends JpaRepository<ComplaintWorkflow, Long> {
    List<ComplaintWorkflow> findByComplaintIdOrderByTimestampDesc(Long complaintId);
    List<ComplaintWorkflow> findByIsEscalationTrue();
    void deleteByComplaintId(Long complaintId);
}
