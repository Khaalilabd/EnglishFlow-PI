package com.englishflow.auth.repository;

import com.englishflow.auth.entity.ApplicationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote> findByApplicationIdOrderByCreatedAtDesc(Long applicationId);

    List<ApplicationNote> findByCreatedBy(Long createdBy);
}
