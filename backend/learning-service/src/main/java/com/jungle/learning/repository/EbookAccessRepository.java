package com.jungle.learning.repository;

import com.jungle.learning.model.EbookAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EbookAccessRepository extends JpaRepository<EbookAccess, Long> {
    List<EbookAccess> findByStudentId(Long studentId);
    List<EbookAccess> findByEbookId(Long ebookId);
    Optional<EbookAccess> findByEbook_IdAndStudentId(Long ebookId, Long studentId);
}
