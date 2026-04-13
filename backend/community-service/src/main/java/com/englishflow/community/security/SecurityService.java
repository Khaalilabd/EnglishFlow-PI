package com.englishflow.community.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecurityService {

    /**
     * Check if user can modify a resource (post, topic, etc.)
     * User can modify if they are the owner or an admin
     */
    public boolean canModifyResource(Long userId, String userRole, Long resourceOwnerId) {
        if (userId == null || userRole == null) {
            log.warn("User not authenticated");
            return false;
        }
        
        // Admin can modify anything
        if ("ADMIN".equals(userRole)) {
            return true;
        }
        
        // Owner can modify their own resources
        if (userId.equals(resourceOwnerId)) {
            return true;
        }
        
        log.warn("User {} with role {} cannot modify resource owned by {}", userId, userRole, resourceOwnerId);
        return false;
    }

    /**
     * Check if user can delete a resource
     * Only admin or owner can delete
     */
    public boolean canDeleteResource(Long userId, String userRole, Long resourceOwnerId) {
        return canModifyResource(userId, userRole, resourceOwnerId);
    }

    /**
     * Check if user has admin role
     */
    public boolean isAdmin(String userRole) {
        return "ADMIN".equals(userRole);
    }

    /**
     * Check if user has teacher role
     */
    public boolean isTeacher(String userRole) {
        return "TEACHER".equals(userRole);
    }

    /**
     * Check if user has student role
     */
    public boolean isStudent(String userRole) {
        return "STUDENT".equals(userRole);
    }

    /**
     * Check if user can moderate content (admin or teacher)
     */
    public boolean canModerate(String userRole) {
        return "ADMIN".equals(userRole) || "TEACHER".equals(userRole);
    }
}
