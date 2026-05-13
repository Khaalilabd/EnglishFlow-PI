package com.englishflow.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "courses-service", path = "/pack-enrollments")
public interface CoursesServiceClient {
    
    /**
     * Get all student IDs enrolled with a specific tutor
     */
    @GetMapping("/tutor/{tutorId}/students")
    List<Long> getStudentIdsByTutorId(@PathVariable("tutorId") Long tutorId);
    
    /**
     * Get pack completion rates for a tutor
     */
    @GetMapping("/tutor/{tutorId}/completion-rates")
    Map<String, Integer> getPackCompletionRates(@PathVariable("tutorId") Long tutorId);
}
