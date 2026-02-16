package com.englishflow.community.repository;

import com.englishflow.community.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Page<Topic> findBySubCategoryId(Long subCategoryId, Pageable pageable);
    Page<Topic> findByUserId(Long userId, Pageable pageable);
}
