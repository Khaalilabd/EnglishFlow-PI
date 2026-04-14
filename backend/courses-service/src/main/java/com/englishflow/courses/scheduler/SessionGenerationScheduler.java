package com.englishflow.courses.scheduler;

import com.englishflow.courses.service.OnlineLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionGenerationScheduler {
    
    private final OnlineLessonService onlineLessonService;
    
    /**
     * Generate sessions daily at 2 AM for the next 4 weeks
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void generateSessionsDaily() {
        try {
            log.info("Starting session generation for next 4 weeks");
            onlineLessonService.generateSessionsForNextWeeks(4);
            log.info("Session generation completed successfully");
        } catch (Exception e) {
            log.error("Error generating sessions", e);
        }
    }
}
