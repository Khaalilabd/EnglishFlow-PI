package com.jungle.learning.repository;

import com.jungle.learning.model.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EbookRepository extends JpaRepository<Ebook, Long> {
    List<Ebook> findByLevel(Ebook.Level level);
    List<Ebook> findByCategory(Ebook.Category category);
    List<Ebook> findByIsFree(Boolean isFree);
}
