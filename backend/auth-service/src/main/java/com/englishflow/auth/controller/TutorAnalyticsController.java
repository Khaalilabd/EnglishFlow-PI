package com.englishflow.auth.controller;

import com.englishflow.auth.service.TutorAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/tutors")
@RequiredArgsConstructor
@Slf4j
public class TutorAnalyticsController {

    private final TutorAnalyticsService tutorAnalyticsService;

    /**
     * Get analytics dashboard data for a tutor
     */
    @GetMapping("/{tutorId}/analytics")
    public ResponseEntity<Map<String, Object>> getTutorAnalytics(
            @PathVariable Long tutorId,
            @RequestParam(defaultValue = "month") String range) {
        
        log.info("Fetching analytics for tutor {} with range {}", tutorId, range);
        
        Map<String, Object> response = tutorAnalyticsService.getTutorAnalytics(tutorId, range);
        
        return ResponseEntity.ok(response);
    }
}
