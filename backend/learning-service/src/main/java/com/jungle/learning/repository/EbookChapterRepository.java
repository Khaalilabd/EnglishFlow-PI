package com.jungle.learning.repository;

import com.jungle.learning.model.EbookChapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EbookChapterRepository extends JpaRepository<EbookChapter, Long> {
    
    List<EbookChapter> findByEbookIdOrderByOrderIndexAsc(Long ebookId);
    
    List<EbookChapter> findByEbookIdAndIsFreeTrue(Long ebookId);
}
