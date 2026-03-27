package com.jungle.learning.service;

import com.jungle.learning.model.Quiz;
import com.jungle.learning.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSchedulerService {
    
    private final QuizRepository quizRepository;
    
    /**
     * Check every minute for quizzes that should be auto-published
     */
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    @Transactional
    public void autoPublishScheduledQuizzes() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Quiz> scheduledQuizzes = quizRepository.findByPublishedFalseAndPublishAtBefore(now);
            
            if (!scheduledQuizzes.isEmpty()) {
                log.info("Found {} quizzes to auto-publish", scheduledQuizzes.size());
                
                for (Quiz quiz : scheduledQuizzes) {
                    quiz.setPublished(true);
                    quizRepository.save(quiz);
                    log.info("Auto-published quiz: {} (ID: {})", quiz.getTitle(), quiz.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error in auto-publish scheduler", e);
        }
    }
}
