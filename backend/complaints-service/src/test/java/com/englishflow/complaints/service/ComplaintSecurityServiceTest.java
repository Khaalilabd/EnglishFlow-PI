package com.englishflow.complaints.service;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.enums.TargetRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ComplaintSecurityServiceTest {

    @InjectMocks
    private ComplaintSecurityService securityService;

    private Complaint complaint;

    @BeforeEach
    void setUp() {
        complaint = new Complaint();
        complaint.setId(1L);
        complaint.setUserId(100L);
        complaint.setStatus(ComplaintStatus.OPEN);
        complaint.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);
    }

    // ========== canViewComplaint Tests ==========

    @Test
    void canViewComplaint_WhenUserIsOwner_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canViewComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canViewComplaint_WhenUserIsAdmin_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canViewComplaint(complaint, 999L, "ADMIN");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canViewComplaint_WhenUserIsAcademicOfficeAffair_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canViewComplaint(complaint, 999L, "ACADEMIC_OFFICE_AFFAIR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canViewComplaint_WhenTutorViewsTutorComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.TUTOR);

        // Act
        boolean result = securityService.canViewComplaint(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canViewComplaint_WhenSupportViewsSupportComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act
        boolean result = securityService.canViewComplaint(complaint, 300L, "SUPPORT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canViewComplaint_WhenUnauthorizedUser_ShouldReturnFalse() {
        // Act
        boolean result = securityService.canViewComplaint(complaint, 999L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canViewComplaint_WhenTutorViewsNonTutorComplaint_ShouldReturnFalse() {
        // Arrange
        complaint.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);

        // Act
        boolean result = securityService.canViewComplaint(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isFalse();
    }

    // ========== canUpdateComplaint Tests ==========

    @Test
    void canUpdateComplaint_WhenOwnerAndStatusOpen_ShouldReturnTrue() {
        // Arrange
        complaint.setStatus(ComplaintStatus.OPEN);

        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenOwnerAndStatusSubmitted_ShouldReturnTrue() {
        // Arrange
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenOwnerButStatusResolved_ShouldReturnFalse() {
        // Arrange
        complaint.setStatus(ComplaintStatus.RESOLVED);

        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canUpdateComplaint_WhenAdmin_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 999L, "ADMIN");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenAcademicOfficeAffair_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 999L, "ACADEMIC_OFFICE_AFFAIR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenTutorAndTutorComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.TUTOR);

        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenSupportAndSupportComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 300L, "SUPPORT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canUpdateComplaint_WhenUnauthorizedUser_ShouldReturnFalse() {
        // Act
        boolean result = securityService.canUpdateComplaint(complaint, 999L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    // ========== canDeleteComplaint Tests ==========

    @Test
    void canDeleteComplaint_WhenOwnerAndStatusOpen_ShouldReturnTrue() {
        // Arrange
        complaint.setStatus(ComplaintStatus.OPEN);

        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canDeleteComplaint_WhenOwnerButStatusNotOpen_ShouldReturnFalse() {
        // Arrange
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canDeleteComplaint_WhenAdmin_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 999L, "ADMIN");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canDeleteComplaint_WhenAcademicOfficeAffair_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 999L, "ACADEMIC_OFFICE_AFFAIR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canDeleteComplaint_WhenUnauthorizedUser_ShouldReturnFalse() {
        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 999L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canDeleteComplaint_WhenTutor_ShouldReturnFalse() {
        // Arrange
        complaint.setTargetRole(TargetRole.TUTOR);

        // Act
        boolean result = securityService.canDeleteComplaint(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isFalse();
    }

    // ========== canChangeStatus Tests ==========

    @Test
    void canChangeStatus_WhenOwnerIsStudent_ShouldReturnFalse() {
        // Act
        boolean result = securityService.canChangeStatus(complaint, 100L, "STUDENT", "IN_PROGRESS");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canChangeStatus_WhenAdmin_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canChangeStatus(complaint, 999L, "ADMIN", "RESOLVED");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canChangeStatus_WhenAcademicOfficeAffair_ShouldReturnTrue() {
        // Act
        boolean result = securityService.canChangeStatus(complaint, 999L, "ACADEMIC_OFFICE_AFFAIR", "RESOLVED");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canChangeStatus_WhenTutorAndTutorComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.TUTOR);

        // Act
        boolean result = securityService.canChangeStatus(complaint, 200L, "TUTOR", "IN_PROGRESS");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canChangeStatus_WhenSupportAndSupportComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act
        boolean result = securityService.canChangeStatus(complaint, 300L, "SUPPORT", "IN_PROGRESS");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void canChangeStatus_WhenUnauthorizedUser_ShouldReturnFalse() {
        // Act
        boolean result = securityService.canChangeStatus(complaint, 999L, "STUDENT", "RESOLVED");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void canChangeStatus_WhenTutorButNotTutorComplaint_ShouldReturnFalse() {
        // Arrange
        complaint.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);

        // Act
        boolean result = securityService.canChangeStatus(complaint, 200L, "TUTOR", "IN_PROGRESS");

        // Assert
        assertThat(result).isFalse();
    }

    // ========== shouldIncludeInList Tests ==========

    @Test
    void shouldIncludeInList_WhenStudentAndOwner_ShouldReturnTrue() {
        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 100L, "STUDENT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldIncludeInList_WhenStudentButNotOwner_ShouldReturnFalse() {
        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 999L, "STUDENT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldIncludeInList_WhenAdmin_ShouldReturnTrue() {
        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 999L, "ADMIN");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldIncludeInList_WhenAcademicOfficeAffair_ShouldReturnTrue() {
        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 999L, "ACADEMIC_OFFICE_AFFAIR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldIncludeInList_WhenTutorAndTutorComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.TUTOR);

        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldIncludeInList_WhenTutorButNotTutorComplaint_ShouldReturnFalse() {
        // Arrange
        complaint.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);

        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 200L, "TUTOR");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldIncludeInList_WhenSupportAndSupportComplaint_ShouldReturnTrue() {
        // Arrange
        complaint.setTargetRole(TargetRole.SUPPORT);

        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 300L, "SUPPORT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void shouldIncludeInList_WhenSupportButNotSupportComplaint_ShouldReturnFalse() {
        // Arrange
        complaint.setTargetRole(TargetRole.ACADEMIC_OFFICE_AFFAIR);

        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 300L, "SUPPORT");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void shouldIncludeInList_WhenUnknownRole_ShouldReturnFalse() {
        // Act
        boolean result = securityService.shouldIncludeInList(complaint, 999L, "UNKNOWN_ROLE");

        // Assert
        assertThat(result).isFalse();
    }
}
