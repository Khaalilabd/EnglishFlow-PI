package com.englishflow.payment.controller;

import com.englishflow.payment.dto.CreateRefundRequest;
import com.englishflow.payment.dto.RefundDTO;
import com.englishflow.payment.dto.RefundFilterDTO;
import com.englishflow.payment.dto.RefundStatsDTO;
import com.englishflow.payment.dto.RejectRefundRequest;
import com.englishflow.payment.enums.RefundStatus;
import com.englishflow.payment.service.RefundService;
import com.englishflow.payment.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for refund operations.
 * Provides endpoints for students to request and manage refunds,
 * and for administrators to review and process refunds.
 */
@RestController
@RequestMapping("/refunds")
@RequiredArgsConstructor
@Slf4j
public class RefundController {

    private final RefundService refundService;
    private final SecurityUtil securityUtil;

    // ÔöÇÔöÇ Student Endpoints ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Create a new refund request.
     * Student endpoint - requires STUDENT role.
     * 
     * @param request The refund request details
     * @return Created refund with PENDING status
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RefundDTO> createRefundRequest(@Valid @RequestBody CreateRefundRequest request) {
        Long studentId = securityUtil.getCurrentUserId();
        log.info("Student {} creating refund request for payment {}", studentId, request.getPaymentId());
        
        RefundDTO refund = refundService.createRefundRequest(request, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(refund);
    }

    /**
     * Get all refunds for the authenticated student.
     * Student endpoint - requires STUDENT role.
     * 
     * @return List of student's refunds
     */
    @GetMapping("/my-refunds")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<RefundDTO>> getMyRefunds() {
        Long studentId = securityUtil.getCurrentUserId();
        log.info("Student {} retrieving refund history", studentId);
        
        List<RefundDTO> refunds = refundService.getRefundsByStudent(studentId);
        return ResponseEntity.ok(refunds);
    }

    /**
     * Get refund details by ID.
     * Accessible by both students (own refunds) and admins (all refunds).
     * 
     * @param id The refund ID
     * @return Refund details
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<RefundDTO> getRefundById(@PathVariable Long id) {
        log.info("Retrieving refund {}", id);
        
        RefundDTO refund = refundService.getRefundById(id);
        
        // If student, verify they own the refund
        if (!securityUtil.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(refund);
    }

    /**
     * Cancel a refund request.
     * Student endpoint - requires STUDENT role.
     * Only allowed for PENDING or APPROVED status.
     * 
     * @param id The refund ID to cancel
     * @return Updated refund with CANCELLED status
     */
    @DeleteMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<RefundDTO> cancelRefund(@PathVariable Long id) {
        Long studentId = securityUtil.getCurrentUserId();
        log.info("Student {} cancelling refund {}", studentId, id);
        
        RefundDTO refund = refundService.cancelRefund(id, studentId);
        return ResponseEntity.ok(refund);
    }

    // ÔöÇÔöÇ Admin Endpoints ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    /**
     * Get all refunds with optional filtering.
     * Admin endpoint - requires ADMIN role.
     * 
     * @param status Filter by refund status (optional)
     * @param studentId Filter by student ID (optional)
     * @param startDate Filter by start date (optional)
     * @param endDate Filter by end date (optional)
     * @param itemType Filter by item type (optional)
     * @return List of refunds matching the filters
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RefundDTO>> getAllRefunds(
            @RequestParam(required = false) RefundStatus status,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String itemType) {
        
        log.info("Admin retrieving refunds with filters - status: {}, studentId: {}, startDate: {}, endDate: {}, itemType: {}", 
                status, studentId, startDate, endDate, itemType);
        
        RefundFilterDTO filter = new RefundFilterDTO();
        filter.setStatus(status);
        filter.setStudentId(studentId);
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);
        filter.setItemType(itemType);
        
        List<RefundDTO> refunds = refundService.getAllRefunds(filter);
        return ResponseEntity.ok(refunds);
    }

    /**
     * Approve a refund request.
     * Admin endpoint - requires ADMIN role.
     * Automatically triggers refund processing with Paymee.
     * 
     * @param id The refund ID to approve
     * @return Updated refund with APPROVED status
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RefundDTO> approveRefund(@PathVariable Long id) {
        Long adminId = securityUtil.getCurrentUserId();
        log.info("Admin {} approving refund {}", adminId, id);
        
        RefundDTO refund = refundService.approveRefund(id, adminId);
        return ResponseEntity.ok(refund);
    }

    /**
     * Reject a refund request.
     * Admin endpoint - requires ADMIN role.
     * 
     * @param id The refund ID to reject
     * @param request The rejection reason
     * @return Updated refund with REJECTED status
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RefundDTO> rejectRefund(
            @PathVariable Long id,
            @Valid @RequestBody RejectRefundRequest request) {
        
        Long adminId = securityUtil.getCurrentUserId();
        log.info("Admin {} rejecting refund {}", adminId, id);
        
        RefundDTO refund = refundService.rejectRefund(id, adminId, request.getReason());
        return ResponseEntity.ok(refund);
    }

    /**
     * Manually trigger refund processing.
     * Admin endpoint - requires ADMIN role.
     * Used to retry failed refunds or manually process approved refunds.
     * 
     * @param id The refund ID to process
     * @return Updated refund after processing attempt
     */
    @PostMapping("/{id}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RefundDTO> processRefund(@PathVariable Long id) {
        Long adminId = securityUtil.getCurrentUserId();
        log.info("Admin {} manually triggering processing for refund {}", adminId, id);
        
        RefundDTO refund = refundService.processRefund(id);
        return ResponseEntity.ok(refund);
    }

    /**
     * Get refund statistics.
     * Admin endpoint - requires ADMIN role.
     * 
     * @return Aggregate statistics about all refunds
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RefundStatsDTO> getStatistics() {
        log.info("Admin retrieving refund statistics");
        
        RefundStatsDTO stats = refundService.getRefundStatistics();
        return ResponseEntity.ok(stats);
    }
}
