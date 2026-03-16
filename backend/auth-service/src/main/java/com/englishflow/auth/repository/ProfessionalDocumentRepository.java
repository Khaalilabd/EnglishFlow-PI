package com.englishflow.auth.repository;

import com.englishflow.auth.entity.ProfessionalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionalDocumentRepository extends JpaRepository<ProfessionalDocument, Long> {
    List<ProfessionalDocument> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
