package com.englishflow.complaints.service;

import com.englishflow.complaints.entity.Complaint;
import com.englishflow.complaints.enums.TargetRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for handling complaint access control and permissions
 */
@Service
@Slf4j
public class ComplaintSecurityService {
    
    /**
     * Check if a user can view a specific complaint
     */
    public boolean canViewComplaint(Complaint complaint, Long userId, String userRole) {
        // Student can view their own complaints
        if (complaint.getUserId().equals(userId)) {
            return true;
        }
        
        // Admin (ACADEMIC_OFFICE_AFFAIR) can view all complaints
        if ("ACADEMIC_OFFICE_AFFAIR".equals(userRole) || "ADMIN".equals(userRole)) {
            return true;
        }
        
        // Tutor can view complaints assigned to TUTOR role
        if ("TUTOR".equals(userRole) && complaint.getTargetRole() == TargetRole.TUTOR) {
            return true;
        }
        
        // Support can view complaints assigned to SUPPORT role
        if ("SUPPORT".equals(userRole) && complaint.getTargetRole() == TargetRole.SUPPORT) {
            return true;
        }
        
        log.warn("Access denied - User {} (role: {}) attempted to view complaint {} owned by user {}", 
                 userId, userRole, complaint.getId(), complaint.getUserId());
        return false;
    }
    
    /**
     * Check if a user can update a complaint
     */
    public boolean canUpdateComplaint(Complaint complaint, Long userId, String userRole) {
        // Student can update their own open complaints
        if (complaint.getUserId().equals(userId) && 
            (complaint.getStatus().name().equals("OPEN") || complaint.getStatus().name().equals("SUBMITTED"))) {
            return true;
        }
        
        // Admin can update any complaint
        if ("ACADEMIC_OFFICE_AFFAIR".equals(userRole) || "ADMIN".equals(userRole)) {
            return true;
        }
        
        // Tutor can update complaints assigned to them
        if ("TUTOR".equals(userRole) && complaint.getTargetRole() == TargetRole.TUTOR) {
            return true;
        }
        
        // Support can update complaints assigned to them
        if ("SUPPORT".equals(userRole) && complaint.getTargetRole() == TargetRole.SUPPORT) {
            return true;
        }
        
        log.warn("Update denied - User {} (role: {}) attempted to update complaint {}", 
                 userId, userRole, complaint.getId());
        return false;
    }
    
    /**
     * Check if a user can delete a complaint
     */
    public boolean canDeleteComplaint(Complaint complaint, Long userId, String userRole) {
        // Only student can delete their own complaint if it's still OPEN
        if (complaint.getUserId().equals(userId) && complaint.getStatus().name().equals("OPEN")) {
            return true;
        }
        
        // Admin can delete any complaint
        if ("ACADEMIC_OFFICE_AFFAIR".equals(userRole) || "ADMIN".equals(userRole)) {
            return true;
        }
        
        log.warn("Delete denied - User {} (role: {}) attempted to delete complaint {}", 
                 userId, userRole, complaint.getId());
        return false;
    }
    
    /**
     * Check if a user can change complaint status
     */
    public boolean canChangeStatus(Complaint complaint, Long userId, String userRole, String newStatus) {
        // Students cannot change status
        if (complaint.getUserId().equals(userId)) {
            log.warn("Students cannot change complaint status");
            return false;
        }
        
        // Admin can change any status
        if ("ACADEMIC_OFFICE_AFFAIR".equals(userRole) || "ADMIN".equals(userRole)) {
            return true;
        }
        
        // Tutor can change status of complaints assigned to them
        if ("TUTOR".equals(userRole) && complaint.getTargetRole() == TargetRole.TUTOR) {
            return true;
        }
        
        // Support can change status of complaints assigned to them
        if ("SUPPORT".equals(userRole) && complaint.getTargetRole() == TargetRole.SUPPORT) {
            return true;
        }
        
        log.warn("Status change denied - User {} (role: {}) attempted to change status of complaint {}", 
                 userId, userRole, complaint.getId());
        return false;
    }
    
    /**
     * Filter complaints based on user role
     */
    public boolean shouldIncludeInList(Complaint complaint, Long userId, String userRole) {
        // Student sees only their own complaints
        if ("STUDENT".equals(userRole)) {
            return complaint.getUserId().equals(userId);
        }
        
        // Admin sees all complaints
        if ("ACADEMIC_OFFICE_AFFAIR".equals(userRole) || "ADMIN".equals(userRole)) {
            return true;
        }
        
        // Tutor sees complaints assigned to TUTOR
        if ("TUTOR".equals(userRole)) {
            return complaint.getTargetRole() == TargetRole.TUTOR;
        }
        
        // Support sees complaints assigned to SUPPORT
        if ("SUPPORT".equals(userRole)) {
            return complaint.getTargetRole() == TargetRole.SUPPORT;
        }
        
        return false;
    }
}
