package com.jungle.learning.repository;

import com.jungle.learning.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);
    List<Quiz> findByPublished(Boolean published);
    List<Quiz> findByPublishedFalseAndPublishAtBefore(LocalDateTime dateTime);
    List<Quiz> findByCategoryAndPublishedTrue(String category);
    List<Quiz> findByDifficultyAndPublishedTrue(String difficulty);
}
