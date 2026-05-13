package com.englishflow.auth.service;

import com.englishflow.auth.client.CoursesServiceClient;
import com.englishflow.auth.client.MLServiceClient;
import com.englishflow.auth.entity.StudentAnalytics;
import com.englishflow.auth.entity.User;
import com.englishflow.auth.repository.StudentAnalyticsRepository;
import com.englishflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorAnalyticsService {

    private final CoursesServiceClient coursesServiceClient;
    private final MLServiceClient mlServiceClient;
    private final StudentAnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;

    /**
     * Get aggregated analytics for a tutor
     */
    public Map<String, Object> getTutorAnalytics(Long tutorId, String range) {
        log.info("Fetching analytics for tutor {} with range {}", tutorId, range);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all student IDs enrolled with this tutor
            List<Long> studentIds = coursesServiceClient.getStudentIdsByTutorId(tutorId);
            
            if (studentIds.isEmpty()) {
                log.warn("No students found for tutor {}", tutorId);
                return getEmptyAnalytics();
            }
            
            // Get analytics for all students
            List<StudentAnalytics> allAnalytics = analyticsRepository.findByUserIdIn(studentIds);
            
            // Calculate stats
            response.put("stats", calculateStats(allAnalytics, range));
            
            // Calculate performance data
            response.put("performanceData", calculatePerformanceData(allAnalytics, range));
            
            // Calculate risk distribution
            response.put("riskData", calculateRiskDistribution(allAnalytics));
            
            // Calculate engagement data
            response.put("engagementData", calculateEngagementData(allAnalytics, range));
            
            // Calculate completion data
            response.put("completionData", calculateCompletionData(studentIds, tutorId));
            
            // Get at-risk students
            response.put("atRiskStudents", getAtRiskStudents(allAnalytics, studentIds));
            
        } catch (Exception e) {
            log.error("Error fetching tutor analytics: {}", e.getMessage(), e);
            return getEmptyAnalytics();
        }
        
        return response;
    }
    
    private Map<String, Object> calculateStats(List<StudentAnalytics> analytics, String range) {
        Map<String, Object> stats = new HashMap<>();
        
        int totalStudents = analytics.size();
        stats.put("totalStudents", totalStudents);
        
        // Count new students this month
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        long newStudents = analytics.stream()
                .filter(a -> a.getFirstRegistrationDate() != null && 
                           a.getFirstRegistrationDate().isAfter(oneMonthAgo))
                .count();
        stats.put("newStudentsThisMonth", newStudents);
        
        // Calculate average success rate
        double avgSuccessRate = analytics.stream()
                .mapToDouble(StudentAnalytics::getAvgScore)
                .average()
                .orElse(0.0);
        stats.put("avgSuccessRate", Math.round(avgSuccessRate));
        
        // Success rate change (mock for now - would need historical data)
        stats.put("successRateChange", 8);
        
        // Count at-risk students (success rate < 50%)
        long atRiskCount = analytics.stream()
                .filter(a -> a.getAvgScore() < 50.0)
                .count();
        stats.put("atRiskStudents", atRiskCount);
        stats.put("atRiskPercentage", totalStudents > 0 ? 
                Math.round((atRiskCount * 100.0) / totalStudents * 10.0) / 10.0 : 0.0);
        
        // Calculate average study time
        double avgStudyTime = analytics.stream()
                .mapToInt(StudentAnalytics::getTotalTimeSpentMinutes)
                .average()
                .orElse(0.0) / 60.0; // Convert to hours
        stats.put("avgStudyTime", Math.round(avgStudyTime * 10.0) / 10.0);
        
        return stats;
    }
    
    private Map<String, Object> calculatePerformanceData(List<StudentAnalytics> analytics, String range) {
        Map<String, Object> data = new HashMap<>();
        
        // For now, calculate weekly averages for the last 4 weeks
        List<String> labels = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");
        
        // Calculate average score (simplified - would need time-series data)
        double avgScore = analytics.stream()
                .mapToDouble(StudentAnalytics::getAvgScore)
                .average()
                .orElse(0.0);
        
        // Simulate weekly progression
        List<Integer> weeklyScores = Arrays.asList(
                Math.max(0, (int)(avgScore - 7)),
                Math.max(0, (int)(avgScore - 4)),
                Math.max(0, (int)(avgScore - 2)),
                Math.max(0, (int)avgScore)
        );
        
        data.put("labels", labels);
        data.put("data", weeklyScores);
        
        return data;
    }
    
    private Map<String, Object> calculateRiskDistribution(List<StudentAnalytics> analytics) {
        Map<String, Object> data = new HashMap<>();
        
        long lowRisk = analytics.stream().filter(a -> a.getAvgScore() >= 70.0).count();
        long mediumRisk = analytics.stream().filter(a -> a.getAvgScore() >= 50.0 && a.getAvgScore() < 70.0).count();
        long highRisk = analytics.stream().filter(a -> a.getAvgScore() < 50.0).count();
        
        data.put("labels", Arrays.asList("Low Risk", "Medium Risk", "High Risk"));
        data.put("data", Arrays.asList(lowRisk, mediumRisk, highRisk));
        
        return data;
    }
    
    private Map<String, Object> calculateEngagementData(List<StudentAnalytics> analytics, String range) {
        Map<String, Object> data = new HashMap<>();
        
        // Calculate total sessions and clicks
        int totalSessions = analytics.stream().mapToInt(StudentAnalytics::getTotalSessions).sum();
        int totalClicks = analytics.stream().mapToInt(StudentAnalytics::getTotalClicks).sum();
        
        // Distribute across 4 weeks (simplified)
        List<String> labels = Arrays.asList("Week 1", "Week 2", "Week 3", "Week 4");
        List<Integer> sessions = Arrays.asList(
                totalSessions / 5, totalSessions / 4, totalSessions / 4, totalSessions / 3
        );
        List<Integer> clicks = Arrays.asList(
                totalClicks / 5, totalClicks / 4, totalClicks / 4, totalClicks / 3
        );
        
        data.put("labels", labels);
        data.put("sessions", sessions);
        data.put("clicks", clicks);
        
        return data;
    }
    
    private Map<String, Object> calculateCompletionData(List<Long> studentIds, Long tutorId) {
        Map<String, Object> data = new HashMap<>();
        
        try {
            // Get pack completion rates from courses service
            Map<String, Integer> completionRates = coursesServiceClient.getPackCompletionRates(tutorId);
            
            data.put("labels", new ArrayList<>(completionRates.keySet()));
            data.put("data", new ArrayList<>(completionRates.values()));
        } catch (Exception e) {
            log.error("Error fetching completion data: {}", e.getMessage());
            // Return empty data
            data.put("labels", Collections.emptyList());
            data.put("data", Collections.emptyList());
        }
        
        return data;
    }
    
    private List<Map<String, Object>> getAtRiskStudents(List<StudentAnalytics> analytics, List<Long> studentIds) {
        // Get ALL students, sorted by success rate (lowest first = highest risk first)
        List<StudentAnalytics> sortedAnalytics = analytics.stream()
                .sorted(Comparator.comparingDouble(StudentAnalytics::getAvgScore))
                .collect(Collectors.toList());
        
        List<Map<String, Object>> allStudents = new ArrayList<>();
        
        for (StudentAnalytics analytics1 : sortedAnalytics) {
            Optional<User> userOpt = userRepository.findById(analytics1.getUserId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> student = new HashMap<>();
                student.put("id", user.getId());
                student.put("name", user.getFirstName() + " " + user.getLastName());
                student.put("packName", "Course Pack"); // Would need to fetch from courses service
                student.put("successRate", Math.round(analytics1.getAvgScore()));
                student.put("lastActivity", analytics1.getLastActivityAt() != null ? 
                        analytics1.getLastActivityAt().toLocalDate().toString() : 
                        LocalDate.now().toString());
                
                // Get cluster from ML service (real clustering model)
                String cluster = getClusterFromML(analytics1);
                student.put("cluster", cluster);
                
                allStudents.add(student);
            }
        }
        
        return allStudents;
    }
    
    private String getClusterFromML(StudentAnalytics analytics) {
        try {
            // Prepare data for ML service
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("num_of_prev_attempts", analytics.getPreviousAttempts());
            studentData.put("studied_credits", analytics.getStudiedCredits());
            studentData.put("total_clicks", analytics.getTotalClicks());
            studentData.put("nb_sessions", analytics.getTotalSessions());
            studentData.put("avg_clicks", analytics.getAvgClicksPerSession());
            studentData.put("max_clicks", analytics.getMaxClicksInSession());
            studentData.put("avg_score", analytics.getAvgScore());
            studentData.put("min_score", analytics.getMinScore());
            studentData.put("max_score", analytics.getMaxScore());
            studentData.put("nb_assessments", analytics.getTotalAssessments());
            studentData.put("nb_tma", analytics.getCompletedTMA());
            studentData.put("nb_cma", analytics.getCompletedCMA());
            studentData.put("nb_exams", analytics.getCompletedExams());
            studentData.put("date_registration", analytics.getFirstRegistrationDate() != null ? 
                    analytics.getFirstRegistrationDate().toLocalDate().toEpochDay() : 0);
            studentData.put("is_unregistered", analytics.getIsUnregistered() ? 1 : 0);
            studentData.put("module_presentation_length", 30); // Default value
            
            // Call ML service
            Map<String, Object> response = mlServiceClient.getClusteringPrediction(studentData);
            
            // Extract cluster label
            String clusterLabel = (String) response.get("cluster_label");
            
            // Translate to English if needed
            if (clusterLabel != null) {
                if (clusterLabel.contains("Performants")) {
                    return "High Performers";
                } else if (clusterLabel.contains("Moyens")) {
                    return "Average Students";
                } else if (clusterLabel.contains("Risque")) {
                    return "At Risk";
                }
                return clusterLabel;
            }
            
        } catch (Exception e) {
            log.warn("Failed to get cluster from ML service for user {}: {}", 
                    analytics.getUserId(), e.getMessage());
        }
        
        // Fallback to rule-based clustering if ML service fails
        return getClusterFallback(analytics);
    }
    
    private String getClusterFallback(StudentAnalytics analytics) {
        double score = analytics.getAvgScore();
        int sessions = analytics.getTotalSessions();
        int clicks = analytics.getTotalClicks();
        
        // Cluster 0: Étudiants Performants (High performers)
        if (score >= 75 && sessions >= 30 && clicks >= 500) {
            return "High Performers";
        }
        // Cluster 1: Étudiants Moyens (Average students)
        else if (score >= 50 && sessions >= 15 && clicks >= 200) {
            return "Average Students";
        }
        // Cluster 2: Étudiants À Risque (At risk students)
        else {
            return "At Risk";
        }
    }
    
    private Map<String, Object> getEmptyAnalytics() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", 0);
        stats.put("newStudentsThisMonth", 0);
        stats.put("avgSuccessRate", 0);
        stats.put("successRateChange", 0);
        stats.put("atRiskStudents", 0);
        stats.put("atRiskPercentage", 0.0);
        stats.put("avgStudyTime", 0.0);
        response.put("stats", stats);
        
        response.put("performanceData", Map.of("labels", Collections.emptyList(), "data", Collections.emptyList()));
        response.put("riskData", Map.of("labels", Collections.emptyList(), "data", Collections.emptyList()));
        response.put("engagementData", Map.of("labels", Collections.emptyList(), "sessions", Collections.emptyList(), "clicks", Collections.emptyList()));
        response.put("completionData", Map.of("labels", Collections.emptyList(), "data", Collections.emptyList()));
        response.put("atRiskStudents", Collections.emptyList());
        
        return response;
    }
}
