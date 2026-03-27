package com.jungle.learning.repository;

import com.jungle.learning.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByStudentId(Long studentId);
    List<QuizAttempt> findByQuiz_Id(Long quizId);
    List<QuizAttempt> findByQuiz_IdAndStudentId(Long quizId, Long studentId);
}
