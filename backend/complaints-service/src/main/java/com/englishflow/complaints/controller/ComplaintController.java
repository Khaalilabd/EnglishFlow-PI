package com.englishflow.complaints.controller;

import com.englishflow.complaints.dto.ComplaintMessageDTO;
import com.englishflow.complaints.dto.ComplaintWithUserDTO;
import com.englishflow.complaints.dto.ComplaintWorkflowDTO;
import com.englishflow.complaints.dto.StudentComplaintDTO;
import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.entity.ComplaintMessage;
import com.englishflow.complaints.entity.ComplaintNotification;
import com.englishflow.complaints.entity.ComplaintWorkflow;
import com.englishflow.complaints.enums.ComplaintStatus;
import com.englishflow.complaints.repository.ComplaintNotificationRepository;
import com.englishflow.complaints.repository.ComplaintRepository;
import com.englishflow.complaints.service.AcademicComplaintService;
import com.englishflow.complaints.service.ComplaintMessageService;
import com.englishflow.complaints.service.ComplaintSecurityService;
import com.englishflow.complaints.service.ComplaintService;
import com.englishflow.complaints.service.ComplaintWorkflowService;
import com.englishflow.complaints.service.NotificationSseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
@Slf4j
public class ComplaintController {
    
    private final ComplaintService complaintService;
    private final AcademicComplaintService academicComplaintService;
    private final ComplaintWorkflowService workflowService;
    private final ComplaintMessageService messageService;
    private final ComplaintNotificationRepository notificationRepository;
    private final NotificationSseService notificationSseService;
    private final ComplaintRepository complaintRepository;
    private final ComplaintSecurityService securityService;
    
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody Complaint complaint) {
        log.info("POST /api/complaints - Creating complaint for user: {}", complaint.getUserId());
        Complaint created = complaintService.createComplaint(complaint);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/my-complaints")
    public ResponseEntity<List<StudentComplaintDTO>> getMyComplaints(@RequestParam Long userId) {
        log.info("GET /api/complaints/my-complaints - User: {}", userId);
        List<StudentComplaintDTO> complaints = complaintService.getComplaintsByUserIdWithResponder(userId);
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        log.info("GET /api/complaints - Fetching all complaints");
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("X-User-Id");
        String userRole = (String) request.getAttribute("X-User-Role");
        
        log.info("GET /api/complaints/{} - Fetching complaint (userId: {}, role: {})", id, userId, userRole);
        
        Complaint complaint = complaintService.getComplaintById(id);
        
        // Security check
        if (userId != null && userRole != null) {
            if (!securityService.canViewComplaint(complaint, userId, userRole)) {
                log.warn("Access denied for user {} (role: {}) to view complaint {}", userId, userRole, id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        return ResponseEntity.ok(complaint);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaint complaint,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("X-User-Id");
        String userRole = (String) request.getAttribute("X-User-Role");
        
        log.info("PUT /api/complaints/{} - Updating complaint (userId: {}, role: {})", id, userId, userRole);
        
        Complaint existingComplaint = complaintService.getComplaintById(id);
        
        // Security check
        if (userId != null && userRole != null) {
            if (!securityService.canUpdateComplaint(existingComplaint, userId, userRole)) {
                log.warn("Update denied for user {} (role: {}) on complaint {}", userId, userRole, id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        Complaint updated = complaintService.updateComplaint(id, complaint);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("X-User-Id");
        String userRole = (String) request.getAttribute("X-User-Role");
        
        log.info("DELETE /api/complaints/{} - Deleting complaint (userId: {}, role: {})", id, userId, userRole);
        
        Complaint complaint = complaintService.getComplaintById(id);
        log.info("Complaint details - id: {}, userId: {}, status: {}", 
                 complaint.getId(), complaint.getUserId(), complaint.getStatus());
        
        // Security check
        if (userId != null && userRole != null) {
            if (!securityService.canDeleteComplaint(complaint, userId, userRole)) {
                log.warn("Delete denied for user {} (role: {}) on complaint {} (owner: {}, status: {})", 
                         userId, userRole, id, complaint.getUserId(), complaint.getStatus());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            log.warn("No user authentication found - userId: {}, userRole: {}", userId, userRole);
        }
        
        complaintService.deleteComplaint(id);
        log.info("Complaint {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@PathVariable ComplaintStatus status) {
        log.info("GET /api/complaints/status/{} - Fetching complaints by status", status);
        List<Complaint> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getComplaintsByUserId(@PathVariable Long userId) {
        log.info("GET /api/complaints/user/{} - Fetching complaints by user", userId);
        List<Complaint> complaints = complaintService.getComplaintsByUserId(userId);
        return ResponseEntity.ok(complaints);
    }
    
    // ========== PAGINATED ENDPOINTS ==========
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<Complaint>> getComplaintsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        log.info("GET /api/complaints/paginated - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                 page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintRepository.findAll(pageable);
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/paginated/user/{userId}")
    public ResponseEntity<Page<Complaint>> getUserComplaintsPaginated(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/complaints/paginated/user/{} - page: {}, size: {}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/paginated/search")
    public ResponseEntity<Page<Complaint>> searchComplaintsPaginated(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/complaints/paginated/search - query: {}, page: {}, size: {}", query, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintRepository.searchComplaints(query, pageable);
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/paginated/filter")
    public ResponseEntity<Page<Complaint>> filterComplaintsPaginated(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ComplaintStatus status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String targetRole,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/complaints/paginated/filter - userId: {}, status: {}, category: {}, targetRole: {}", 
                 userId, status, category, targetRole);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        // Convert string parameters to enums if provided
        com.englishflow.complaints.enums.ComplaintCategory categoryEnum = 
            category != null ? com.englishflow.complaints.enums.ComplaintCategory.valueOf(category) : null;
        com.englishflow.complaints.enums.TargetRole targetRoleEnum = 
            targetRole != null ? com.englishflow.complaints.enums.TargetRole.valueOf(targetRole) : null;
        
        Page<Complaint> complaints = complaintRepository.findByFilters(
            userId, status, categoryEnum, targetRoleEnum, pageable
        );
        return ResponseEntity.ok(complaints);
    }
    
    // ========== ACADEMIC OFFICE AFFAIR ENDPOINTS ==========
    
    @GetMapping("/academic/all")
    public ResponseEntity<List<ComplaintWithUserDTO>> getAllComplaintsForAcademic() {
        log.info("GET /api/complaints/academic/all - Fetching all complaints with user info");
        List<ComplaintWithUserDTO> complaints = academicComplaintService.getAllComplaintsWithUserInfo();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/academic/filtered")
    public ResponseEntity<List<ComplaintWithUserDTO>> getComplaintsForAcademicOffice() {
        log.info("GET /api/complaints/academic/filtered - Fetching complaints for ACADEMIC_OFFICE_AFFAIR");
        List<ComplaintWithUserDTO> complaints = academicComplaintService.getComplaintsForAcademicOffice();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/tutor/complaints")
    public ResponseEntity<List<ComplaintWithUserDTO>> getComplaintsForTutor() {
        log.info("GET /api/complaints/tutor/complaints - Fetching complaints for TUTOR");
        List<ComplaintWithUserDTO> complaints = academicComplaintService.getComplaintsForTutor();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/academic/critical")
    public ResponseEntity<List<ComplaintWithUserDTO>> getCriticalComplaints() {
        log.info("GET /api/complaints/academic/critical - Fetching critical complaints");
        List<ComplaintWithUserDTO> complaints = academicComplaintService.getCriticalComplaints();
        return ResponseEntity.ok(complaints);
    }
    
    @GetMapping("/academic/overdue")
    public ResponseEntity<List<ComplaintWithUserDTO>> getOverdueComplaints() {
        log.info("GET /api/complaints/academic/overdue - Fetching overdue complaints");
        List<ComplaintWithUserDTO> allComplaints = academicComplaintService.getAllComplaintsWithUserInfo();
        List<ComplaintWithUserDTO> overdue = allComplaints.stream()
                .filter(ComplaintWithUserDTO::getIsOverdue)
                .toList();
        return ResponseEntity.ok(overdue);
    }
    
    // ========== WORKFLOW & HISTORY ENDPOINTS ==========
    
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ComplaintWorkflow>> getComplaintHistory(@PathVariable Long id) {
        log.info("GET /api/complaints/{}/history - Fetching complaint history", id);
        List<ComplaintWorkflow> history = workflowService.getComplaintHistory(id);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/{id}/history-with-names")
    public ResponseEntity<List<ComplaintWorkflowDTO>> getComplaintHistoryWithNames(@PathVariable Long id) {
        log.info("GET /api/complaints/{}/history-with-names - Fetching complaint history with actor names", id);
        List<ComplaintWorkflowDTO> history = workflowService.getComplaintHistoryWithActorNames(id);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/{id}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("X-User-Id");
        String userRole = (String) httpRequest.getAttribute("X-User-Role");
        
        log.info("POST /api/complaints/{}/status - Updating status (userId: {}, role: {})", id, userId, userRole);
        log.info("Request data: {}", request);
        
        try {
            Complaint complaint = complaintService.getComplaintById(id);
            ComplaintStatus oldStatus = complaint.getStatus();
            
            String newStatusStr = (String) request.get("status");
            if (newStatusStr == null) {
                log.error("Status is null in request");
                return ResponseEntity.badRequest().build();
            }
            
            // Security check
            if (userId != null && userRole != null) {
                if (!securityService.canChangeStatus(complaint, userId, userRole, newStatusStr)) {
                    log.warn("Status change denied for user {} (role: {}) on complaint {}", userId, userRole, id);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }
            
            Long actorId = request.get("actorId") != null ? Long.valueOf(request.get("actorId").toString()) : null;
            String actorRole = (String) request.get("actorRole");
            String comment = (String) request.getOrDefault("comment", "");
            
            log.info("Changing status from {} to {}", oldStatus, newStatusStr);
            
            complaint.setStatus(ComplaintStatus.valueOf(newStatusStr));
            
            if (request.containsKey("response") && request.get("response") != null) {
                String response = (String) request.get("response");
                if (response != null && !response.trim().isEmpty()) {
                    complaint.setResponse(response);
                    complaint.setResponderId(actorId);
                    complaint.setResponderRole(actorRole);
                }
            }
            
            Complaint updated = complaintService.updateComplaint(id, complaint);
            log.info("Complaint status updated successfully in database");
            
            // Record workflow - catch any errors here to not fail the whole request
            if (actorId != null && actorRole != null) {
                try {
                    log.info("🔔 Calling recordStatusChange for complaint {} from {} to {}", id, oldStatus, newStatusStr);
                    workflowService.recordStatusChange(updated, oldStatus, actorId, actorRole, comment);
                    log.info("✅ Workflow recorded successfully");
                } catch (Exception e) {
                    log.error("❌ Error recording workflow, but complaint was updated successfully", e);
                    log.error("❌ Error details - actorId: {}, actorRole: {}, comment: {}", actorId, actorRole, comment);
                    // Don't fail the request if workflow recording fails
                }
            } else {
                log.warn("⚠️ Skipping workflow recording - actorId: {}, actorRole: {}", actorId, actorRole);
            }
            
            log.info("Status update completed successfully");
            return ResponseEntity.ok(updated);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error updating complaint status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // ========== NOTIFICATION ENDPOINTS ==========
    
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<ComplaintNotification>> getUserNotifications(@PathVariable Long userId) {
        log.info("GET /api/complaints/notifications/{} - Fetching notifications", userId);
        List<ComplaintNotification> notifications = 
                notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        log.info("PUT /api/complaints/notifications/{}/read - Marking as read", notificationId);
        ComplaintNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/notifications/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount(@PathVariable Long userId) {
        log.info("GET /api/complaints/notifications/{}/count - Getting unread count", userId);
        long count = notificationRepository.countByRecipientIdAndIsReadFalse(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
    
    // ========== MESSAGE ENDPOINTS ==========
    
    @PostMapping("/{id}/messages")
    public ResponseEntity<ComplaintMessageDTO> createMessage(
            @PathVariable Long id,
            @RequestBody ComplaintMessage message) {
        log.info("POST /api/complaints/{}/messages - Creating message", id);
        message.setComplaintId(id);
        ComplaintMessageDTO created = messageService.createMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<ComplaintMessageDTO>> getMessages(@PathVariable Long id) {
        log.info("GET /api/complaints/{}/messages - Fetching messages", id);
        List<ComplaintMessageDTO> messages = messageService.getMessagesByComplaintId(id);
        return ResponseEntity.ok(messages);
    }
    
    // ========== SSE NOTIFICATION ENDPOINTS ==========
    
    /**
     * Stream notifications for a specific user via SSE
     * Usage: EventSource('/api/complaints/notifications/stream/{userId}')
     */
    @GetMapping(value = "/notifications/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotificationsForUser(@PathVariable Long userId) {
        log.info("GET /api/complaints/notifications/stream/{} - Creating SSE connection for user", userId);
        SseEmitter emitter = notificationSseService.createEmitterForUser(userId);
        log.info("SSE emitter created successfully for user: {}", userId);
        return emitter;
    }
    
    /**
     * Stream notifications for a specific role via SSE
     * Usage: EventSource('/api/complaints/notifications/stream/role/{role}')
     */
    @GetMapping(value = "/notifications/stream/role/{role}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotificationsForRole(@PathVariable String role) {
        log.info("GET /api/complaints/notifications/stream/role/{} - Creating SSE connection for role", role);
        SseEmitter emitter = notificationSseService.createEmitterForRole(role);
        log.info("SSE emitter created successfully for role: {}", role);
        return emitter;
    }
    
    /**
     * Get active SSE connections count for monitoring
     */
    @GetMapping("/notifications/stream/stats")
    public ResponseEntity<Map<String, Object>> getStreamStats() {
        log.info("GET /api/complaints/notifications/stream/stats - Getting SSE statistics");
        // This would require adding methods to NotificationSseService to get stats
        return ResponseEntity.ok(Map.of(
            "message", "SSE streaming is active",
            "timestamp", LocalDateTime.now()
        ));
    }
}
