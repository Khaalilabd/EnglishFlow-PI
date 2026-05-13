package com.englishflow.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "ml-service", url = "${ml.service.url:http://localhost:5000}")
public interface MLServiceClient {
    
    /**
     * Get clustering prediction for a student
     */
    @PostMapping("/clustering")
    Map<String, Object> getClusteringPrediction(@RequestBody Map<String, Object> studentData);
    
    /**
     * Get performance prediction for a student
     */
    @PostMapping("/predict")
    Map<String, Object> getPerformancePrediction(@RequestBody Map<String, Object> studentData);
    
    /**
     * Get course recommendations for a student
     */
    @PostMapping("/recommend")
    Map<String, Object> getCourseRecommendations(@RequestBody Map<String, Object> studentData);
}
