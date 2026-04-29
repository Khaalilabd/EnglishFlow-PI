package com.englishflow.complaints.service;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.AcademicRiskLevel;
import com.englishflow.complaints.enums.ComplaintCategory;
import com.englishflow.complaints.enums.ComplaintPriority;
import com.englishflow.complaints.enums.TargetRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ComplaintPriorityServiceTest {

    @InjectMocks
    private ComplaintPriorityService complaintPriorityService;

    private Complaint testComplaint;

    @BeforeEach
    void setUp() {
        testComplaint = new Complaint();
        testComplaint.setId(1L);
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void calculatePriority_WithHighScore_ShouldReturnCritical() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.BEHAVIORAL);
        testComplaint.setRiskScore(90);
        testComplaint.setRiskLevel(AcademicRiskLevel.CRITICAL);
        testComplaint.setRequiresIntervention(true);
        testComplaint.setCreatedAt(LocalDateTime.now().minusDays(10));

        // Act
        ComplaintPriority result = complaintPriorityService.calculatePriority(testComplaint);

        // Assert
        assertEquals(ComplaintPriority.CRITICAL, result);
    }

    @Test
    void calculatePriority_WithMediumScore_ShouldReturnCritical() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.PEDAGOGICAL);
        testComplaint.setRiskScore(60);
        testComplaint.setRiskLevel(AcademicRiskLevel.HIGH);

        // Act
        ComplaintPriority result = complaintPriorityService.calculatePriority(testComplaint);

        // Assert
        assertEquals(ComplaintPriority.CRITICAL, result);
    }

    @Test
    void calculatePriority_WithLowScore_ShouldReturnHigh() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setRiskScore(30);
        testComplaint.setRiskLevel(AcademicRiskLevel.MEDIUM);

        // Act
        ComplaintPriority result = complaintPriorityService.calculatePriority(testComplaint);

        // Assert
        assertEquals(ComplaintPriority.HIGH, result);
    }

    @Test
    void calculatePriority_WithVeryLowScore_ShouldReturnMedium() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.OTHER);
        testComplaint.setRiskScore(10);
        testComplaint.setRiskLevel(AcademicRiskLevel.LOW);

        // Act
        ComplaintPriority result = complaintPriorityService.calculatePriority(testComplaint);

        // Assert
        assertEquals(ComplaintPriority.MEDIUM, result);
    }

    @Test
    void calculatePriorityAndTarget_ShouldSetAllFields() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.PEDAGOGICAL);

        // Act
        complaintPriorityService.calculatePriorityAndTarget(testComplaint);

        // Assert
        assertNotNull(testComplaint.getTargetRole());
        assertNotNull(testComplaint.getRiskScore());
        assertNotNull(testComplaint.getRiskLevel());
        assertNotNull(testComplaint.getRequiresIntervention());
        assertNotNull(testComplaint.getPriority());
        assertEquals(TargetRole.TUTOR, testComplaint.getTargetRole());
    }

    @Test
    void calculateRiskScore_WithBehavioralCategory_ShouldReturnHighScore() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.BEHAVIORAL);
        testComplaint.setCreatedAt(LocalDateTime.now().minusDays(5));

        // Act
        int result = complaintPriorityService.calculateRiskScore(testComplaint);

        // Assert
        assertTrue(result >= 40);
    }

    @Test
    void calculateRiskScore_WithTechnicalCategory_ShouldReturnLowerScore() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setCreatedAt(LocalDateTime.now());

        // Act
        int result = complaintPriorityService.calculateRiskScore(testComplaint);

        // Assert
        assertTrue(result >= 20);
        assertTrue(result <= 100);
    }

    @Test
    void calculateRiskScore_WithHighSessionCount_ShouldIncreaseScore() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.PEDAGOGICAL);
        testComplaint.setSessionCount(5);
        testComplaint.setCreatedAt(LocalDateTime.now());

        // Act
        int result = complaintPriorityService.calculateRiskScore(testComplaint);

        // Assert
        assertTrue(result >= 50);
    }

    @Test
    void calculateRiskScore_ShouldNotExceed100() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.BEHAVIORAL);
        testComplaint.setSessionCount(10);
        testComplaint.setCreatedAt(LocalDateTime.now().minusDays(30));

        // Act
        int result = complaintPriorityService.calculateRiskScore(testComplaint);

        // Assert
        assertTrue(result <= 100);
        assertTrue(result >= 80);
    }

    @Test
    void requiresIntervention_WithBehavioralCategory_ShouldReturnTrue() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.BEHAVIORAL);

        // Act
        boolean result = complaintPriorityService.requiresIntervention(testComplaint);

        // Assert
        assertTrue(result);
    }

    @Test
    void requiresIntervention_WithTutorBehaviorCategory_ShouldReturnTrue() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TUTOR_BEHAVIOR);

        // Act
        boolean result = complaintPriorityService.requiresIntervention(testComplaint);

        // Assert
        assertTrue(result);
    }

    @Test
    void requiresIntervention_WithHighRiskScore_ShouldReturnTrue() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setRiskScore(80);

        // Act
        boolean result = complaintPriorityService.requiresIntervention(testComplaint);

        // Assert
        assertTrue(result);
    }

    @Test
    void requiresIntervention_WithOldComplaint_ShouldReturnTrue() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setRiskScore(30);
        testComplaint.setCreatedAt(LocalDateTime.now().minusDays(10));

        // Act
        boolean result = complaintPriorityService.requiresIntervention(testComplaint);

        // Assert
        assertTrue(result);
    }

    @Test
    void requiresIntervention_WithLowRiskAndRecent_ShouldReturnFalse() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.OTHER);
        testComplaint.setRiskScore(20);
        testComplaint.setCreatedAt(LocalDateTime.now());

        // Act
        boolean result = complaintPriorityService.requiresIntervention(testComplaint);

        // Assert
        assertFalse(result);
    }

    @Test
    void calculateAcademicRiskLevel_WithCriticalScore_ShouldReturnCritical() {
        // Arrange
        testComplaint.setRiskScore(85);

        // Act
        AcademicRiskLevel result = complaintPriorityService.calculateAcademicRiskLevel(testComplaint);

        // Assert
        assertEquals(AcademicRiskLevel.CRITICAL, result);
    }

    @Test
    void calculateAcademicRiskLevel_WithHighScore_ShouldReturnHigh() {
        // Arrange
        testComplaint.setRiskScore(65);

        // Act
        AcademicRiskLevel result = complaintPriorityService.calculateAcademicRiskLevel(testComplaint);

        // Assert
        assertEquals(AcademicRiskLevel.HIGH, result);
    }

    @Test
    void calculateAcademicRiskLevel_WithMediumScore_ShouldReturnMedium() {
        // Arrange
        testComplaint.setRiskScore(45);

        // Act
        AcademicRiskLevel result = complaintPriorityService.calculateAcademicRiskLevel(testComplaint);

        // Assert
        assertEquals(AcademicRiskLevel.MEDIUM, result);
    }

    @Test
    void calculateAcademicRiskLevel_WithLowScore_ShouldReturnLow() {
        // Arrange
        testComplaint.setRiskScore(25);

        // Act
        AcademicRiskLevel result = complaintPriorityService.calculateAcademicRiskLevel(testComplaint);

        // Assert
        assertEquals(AcademicRiskLevel.LOW, result);
    }

    @Test
    void calculateAcademicRiskLevel_WithVeryLowScore_ShouldReturnNormal() {
        // Arrange
        testComplaint.setRiskScore(10);

        // Act
        AcademicRiskLevel result = complaintPriorityService.calculateAcademicRiskLevel(testComplaint);

        // Assert
        assertEquals(AcademicRiskLevel.NORMAL, result);
    }

    @Test
    void calculatePriorityAndTarget_WithPedagogicalCategory_ShouldSetTutorTarget() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.PEDAGOGICAL);

        // Act
        complaintPriorityService.calculatePriorityAndTarget(testComplaint);

        // Assert
        assertEquals(TargetRole.TUTOR, testComplaint.getTargetRole());
    }

    @Test
    void calculatePriorityAndTarget_WithBehavioralCategory_ShouldSetAcademicOfficeTarget() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.BEHAVIORAL);

        // Act
        complaintPriorityService.calculatePriorityAndTarget(testComplaint);

        // Assert
        assertEquals(TargetRole.ACADEMIC_OFFICE_AFFAIR, testComplaint.getTargetRole());
    }

    @Test
    void calculatePriorityAndTarget_WithTechnicalCategory_ShouldSetSupportTarget() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);

        // Act
        complaintPriorityService.calculatePriorityAndTarget(testComplaint);

        // Assert
        assertEquals(TargetRole.SUPPORT, testComplaint.getTargetRole());
    }

    @Test
    void calculatePriorityAndTarget_WithExistingTargetRole_ShouldNotOverride() {
        // Arrange
        testComplaint.setCategory(ComplaintCategory.TECHNICAL);
        testComplaint.setTargetRole(TargetRole.TUTOR);

        // Act
        complaintPriorityService.calculatePriorityAndTarget(testComplaint);

        // Assert
        assertEquals(TargetRole.TUTOR, testComplaint.getTargetRole());
    }
}
