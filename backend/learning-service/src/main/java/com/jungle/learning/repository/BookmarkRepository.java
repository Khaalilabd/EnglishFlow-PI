package com.jungle.learning.repository;

import com.jungle.learning.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    List<Bookmark> findByProgressId(Long progressId);
    
    List<Bookmark> findByProgressIdOrderByPageNumberAsc(Long progressId);
}
