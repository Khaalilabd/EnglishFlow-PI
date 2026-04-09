package com.englishflow.auth.controller;

import com.englishflow.auth.dto.recruitment.*;
import com.englishflow.auth.service.RecruitmentService;
import com.englishflow.auth.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final SecurityUtil securityUtil;

    // Public endpoints - Application submission

    @PostMapping("/apply/step1")
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationStep1Request request) {
        ApplicationResponse response = recruitmentService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/apply/step2")
    public ResponseEntity<ApplicationResponse> updateQualifications(@Valid @RequestBody ApplicationStep2Request request) {
        ApplicationResponse response = recruitmentService.updateQualifications(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/apply/step3")
    public ResponseEntity<ApplicationResponse> updatePresentation(@Valid @RequestBody ApplicationStep3Request request) {
        ApplicationResponse response = recruitmentService.updatePresentation(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/apply/{applicationId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long applicationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String documentType) throws IOException {
        
        DocumentResponse response = recruitmentService.uploadDocument(applicationId, file, documentType);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/apply/{applicationId}/submit")
    public ResponseEntity<ApplicationResponse> submitApplication(@PathVariable Long applicationId) {
        ApplicationResponse response = recruitmentService.submitApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply/{applicationId}/accept-terms")
    public ResponseEntity<ApplicationResponse> acceptTerms(@PathVariable Long applicationId) {
        ApplicationResponse response = recruitmentService.acceptTerms(applicationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/apply/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplication(@PathVariable Long applicationId) {
        ApplicationResponse response = recruitmentService.getApplication(applicationId);
        return ResponseEntity.ok(response);
    }

    // Admin/Recruiter endpoints

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        List<ApplicationResponse> applications = recruitmentService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStatus(@PathVariable String status) {
        List<ApplicationResponse> applications = recruitmentService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{applicationId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateStatusRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.updateStatus(applicationId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{applicationId}/score")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<ApplicationResponse> scoreApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody ScoreApplicationRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.scoreApplication(applicationId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{applicationId}/interview")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<ApplicationResponse> scheduleInterview(
            @PathVariable Long applicationId,
            @Valid @RequestBody ScheduleInterviewRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.scheduleInterview(applicationId, request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate-meeting-link")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<MeetingLinkResponse> generateMeetingLink(
            @Valid @RequestBody GenerateMeetingLinkRequest request) {
        
        MeetingLinkResponse response = recruitmentService.generateMeetingLink(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available-platforms")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<Map<String, Boolean>> getAvailablePlatforms() {
        Map<String, Boolean> platforms = recruitmentService.getAvailablePlatforms();
        return ResponseEntity.ok(platforms);
    }


    @PostMapping("/{applicationId}/notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<NoteResponse> addNote(
            @PathVariable Long applicationId,
            @Valid @RequestBody AddNoteRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        NoteResponse response = recruitmentService.addNote(applicationId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{applicationId}/accept")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApplicationResponse> acceptApplication(@PathVariable Long applicationId) {
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.acceptApplication(applicationId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{applicationId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @PathVariable Long applicationId,
            @Valid @RequestBody RejectApplicationRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.rejectApplication(applicationId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<RecruitmentService.ApplicationStatistics> getStatistics() {
        RecruitmentService.ApplicationStatistics stats = recruitmentService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<ApplicationResponse> getApplicationByUserId(@PathVariable Long userId) {
        ApplicationResponse response = recruitmentService.getApplicationByUserId(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/my-application")
    public ResponseEntity<ApplicationResponse> getMyApplication() {
        Long userId = securityUtil.getCurrentUserId();
        ApplicationResponse response = recruitmentService.getApplicationByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // Calendar endpoints

    @PostMapping("/calendar/availability")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<CalendarAvailabilityResponse> getCalendarAvailability(
            @Valid @RequestBody CalendarAvailabilityRequest request) {
        
        Long userId = securityUtil.getCurrentUserId();
        CalendarAvailabilityResponse response = recruitmentService.getCalendarAvailability(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/calendar/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<List<CalendarEventResponse>> getUpcomingInterviews() {
        Long userId = securityUtil.getCurrentUserId();
        List<CalendarEventResponse> events = recruitmentService.getUpcomingInterviews(userId);
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/calendar/{scheduleId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<Void> cancelInterview(
            @PathVariable Long scheduleId,
            @RequestParam(required = false) String reason) {
        
        recruitmentService.cancelInterviewSchedule(scheduleId, reason);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{applicationId}/interview")
    @PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_OFFICE_AFFAIR')")
    public ResponseEntity<Void> cancelInterviewByApplicationId(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String reason) {
        
        recruitmentService.cancelInterviewByApplicationId(applicationId, reason);
        return ResponseEntity.noContent().build();
    }

    // Google Calendar OAuth Test Endpoint
    @GetMapping("/test-google-calendar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testGoogleCalendar() {
        Map<String, Object> result = recruitmentService.testGoogleCalendarConnection();
        return ResponseEntity.ok(result);
    }
}
