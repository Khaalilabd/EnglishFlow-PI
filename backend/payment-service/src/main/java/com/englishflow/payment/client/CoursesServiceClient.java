package com.englishflow.payment.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Client for interacting with the courses-service for enrollment operations.
 * Uses RestTemplate to call courses-service endpoints for enrollment and unenrollment.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CoursesServiceClient {

    private final RestTemplate restTemplate;

    @Value("${courses-service.url:http://localhost:8086}")
    private String coursesServiceUrl;

    /**
     * Unenroll a student from a specific course.
     * Removes the enrollment record and related progress data.
     *
     * @param studentId the ID of the student to unenroll
     * @param courseId the ID of the course to unenroll from
     * @throws RuntimeException if the unenrollment fails
     */
    public void unenrollFromCourse(Long studentId, Long courseId) {
        try {
            String url = coursesServiceUrl + "/enrollments/unenroll?studentId=" + studentId + "&courseId=" + courseId;
            restTemplate.delete(url);
            log.info("Successfully unenrolled student {} from course {}", studentId, courseId);
        } catch (Exception e) {
            log.error("Failed to unenroll student {} from course {}: {}", studentId, courseId, e.getMessage());
            throw new RuntimeException("Failed to unenroll from course: " + e.getMessage(), e);
        }
    }

    /**
     * Unenroll a student from a pack.
     * Removes the pack enrollment record and all related course enrollments.
     *
     * @param studentId the ID of the student to unenroll
     * @param packId the ID of the pack to unenroll from
     * @throws RuntimeException if the unenrollment fails
     */
    public void unenrollFromPack(Long studentId, Long packId) {
        try {
            String url = coursesServiceUrl + "/pack-enrollments/unenroll?studentId=" + studentId + "&packId=" + packId;
            restTemplate.delete(url);
            log.info("Successfully unenrolled student {} from pack {}", studentId, packId);
        } catch (Exception e) {
            log.error("Failed to unenroll student {} from pack {}: {}", studentId, packId, e.getMessage());
            throw new RuntimeException("Failed to unenroll from pack: " + e.getMessage(), e);
        }
    }
}
