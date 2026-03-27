package com.englishflow.complaints.service;

import com.englishflow.complaints.entity.ComplaintNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationSseService {
    
    // Store SSE emitters by userId
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    
    // Store SSE emitters by role
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> roleEmitters = new ConcurrentHashMap<>();
    
    /**
     * Create a new SSE connection for a user
     */
    public SseEmitter createEmitterForUser(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // No timeout
        
        userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        emitter.onCompletion(() -> removeEmitterForUser(userId, emitter));
        emitter.onTimeout(() -> removeEmitterForUser(userId, emitter));
        emitter.onError(e -> removeEmitterForUser(userId, emitter));
        
        log.info("SSE emitter created for user: {}", userId);
        
        // Send initial connection message
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("Connected to notification stream"));
        } catch (IOException e) {
            log.error("Error sending initial message to user {}", userId, e);
        }
        
        return emitter;
    }
    
    /**
     * Create a new SSE connection for a role
     */
    public SseEmitter createEmitterForRole(String role) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        roleEmitters.computeIfAbsent(role, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        emitter.onCompletion(() -> removeEmitterForRole(role, emitter));
        emitter.onTimeout(() -> removeEmitterForRole(role, emitter));
        emitter.onError(e -> removeEmitterForRole(role, emitter));
        
        log.info("SSE emitter created for role: {}", role);
        
        try {
            emitter.send(SseEmitter.event()
                .name("connected")
                .data("Connected to role notification stream"));
        } catch (IOException e) {
            log.error("Error sending initial message to role {}", role, e);
        }
        
        return emitter;
    }
    
    /**
     * Send notification to a specific user
     */
    public void sendNotificationToUser(Long userId, ComplaintNotification notification) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);
        
        if (emitters == null || emitters.isEmpty()) {
            log.debug("No active SSE connections for user: {}", userId);
            return;
        }
        
        log.info("Sending notification to user {} via {} emitter(s)", userId, emitters.size());
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
                log.debug("Notification sent successfully to user: {}", userId);
            } catch (IOException e) {
                log.error("Error sending notification to user {}", userId, e);
                removeEmitterForUser(userId, emitter);
            }
        }
    }
    
    /**
     * Send notification to all users with a specific role
     */
    public void sendNotificationToRole(String role, ComplaintNotification notification) {
        CopyOnWriteArrayList<SseEmitter> emitters = roleEmitters.get(role);
        
        if (emitters == null || emitters.isEmpty()) {
            log.debug("No active SSE connections for role: {}", role);
            return;
        }
        
        log.info("Sending notification to role {} via {} emitter(s)", role, emitters.size());
        
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
                log.debug("Notification sent successfully to role: {}", role);
            } catch (IOException e) {
                log.error("Error sending notification to role {}", role, e);
                removeEmitterForRole(role, emitter);
            }
        }
    }
    
    /**
     * Remove emitter for a user
     */
    private void removeEmitterForUser(Long userId, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                userEmitters.remove(userId);
            }
            log.info("SSE emitter removed for user: {}", userId);
        }
    }
    
    /**
     * Remove emitter for a role
     */
    private void removeEmitterForRole(String role, SseEmitter emitter) {
        CopyOnWriteArrayList<SseEmitter> emitters = roleEmitters.get(role);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                roleEmitters.remove(role);
            }
            log.info("SSE emitter removed for role: {}", role);
        }
    }
    
    /**
     * Get count of active connections for a user
     */
    public int getActiveConnectionsForUser(Long userId) {
        CopyOnWriteArrayList<SseEmitter> emitters = userEmitters.get(userId);
        return emitters != null ? emitters.size() : 0;
    }
    
    /**
     * Get count of active connections for a role
     */
    public int getActiveConnectionsForRole(String role) {
        CopyOnWriteArrayList<SseEmitter> emitters = roleEmitters.get(role);
        return emitters != null ? emitters.size() : 0;
    }
}
