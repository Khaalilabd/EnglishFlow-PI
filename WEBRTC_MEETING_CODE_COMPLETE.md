# WebRTC Meeting System - Complete Code Documentation

This document contains all the code for the WebRTC video/audio meeting functionality in the EnglishFlow application.

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Backend - Signaling Server](#backend-signaling-server)
3. [Backend - Java Controllers & Services](#backend-java-controllers--services)
4. [Frontend - Meeting Components](#frontend-meeting-components)
5. [Frontend - Services](#frontend-services)

---

## Architecture Overview

The system uses:
- **WebRTC** for peer-to-peer video/audio streaming
- **Socket.IO** for signaling (offer/answer/ICE candidate exchange)
- **Node.js** signaling server on port 3001
- **Spring Boot** backend for meeting session management
- **Angular** frontend components for tutor and student interfaces

### Flow:
1. Tutor creates meeting → Gets roomId
2. Student joins via invite link
3. Socket.IO handles WebRTC signaling
4. Peer-to-peer connection established
5. Video/audio streams exchanged

---

## Backend - Signaling Server

### File: `backend/webrtc-signaling/server.js`

```javascript
const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());

const server = http.createServer(app);
const io = new Server(server, {
  cors: { origin: '*', methods: ['GET', 'POST'] },
  // Increase ping timeout to avoid false disconnects
  pingTimeout: 60000,
  pingInterval: 25000
});

/**
 * rooms: Map<roomId, Map<socketId, { name, socketId, role, lessonId }>>
 *
 * Research finding: ICE candidates can arrive before the remote peer has
 * called setRemoteDescription. We buffer them per-socket and flush when
 * the target peer is ready (i.e. when they send their first offer/answer).
 *
 * iceCandidateBuffer: Map<targetSocketId, Array<{ from, candidate }>>
 */
const rooms = new Map();
const iceCandidateBuffer = new Map();

// Track active meetings by lessonId
const activeMeetings = new Map();

// ── Health / debug endpoints ──────────────────────────────────────────────────

app.get('/health', (_req, res) => res.json({ status: 'ok', rooms: rooms.size }));

app.get('/room/:roomId', (req, res) => {
  const room = rooms.get(req.params.roomId);
  if (!room) return res.json({ participants: 0, peers: [] });
  const peers = [...room.values()].map(p => ({ name: p.name, role: p.role, socketId: p.socketId }));
  res.json({ participants: room.size, peers });
});

app.get('/api/active-meeting/:lessonId', (req, res) => {
  const meeting = activeMeetings.get(req.params.lessonId);
  if (meeting) {
    res.json({ active: true, roomId: meeting.roomId, startedAt: meeting.startedAt });
  } else {
    res.json({ active: false, roomId: null, startedAt: null });
  }
});

// ── Socket.IO ─────────────────────────────────────────────────────────────────

io.on('connection', (socket) => {
  console.log(`[+] Connected: ${socket.id}`);

  // ── join-room ──────────────────────────────────────────────────────────────
  socket.on('join-room', ({ roomId, userName, role, lessonId }) => {
    if (!roomId || !userName) return;

    socket.join(roomId);

    if (!rooms.has(roomId)) rooms.set(roomId, new Map());
    rooms.get(roomId).set(socket.id, {
      name: userName,
      socketId: socket.id,
      role: role || 'student',
      lessonId: lessonId || null
    });

    // Track active meeting when tutor joins with a lessonId
    if (role === 'tutor' && lessonId) {
      activeMeetings.set(String(lessonId), {
        roomId,
        tutorSocketId: socket.id,
        startedAt: new Date().toISOString()
      });
    }

    // Send existing peers to the new joiner
    const others = [...rooms.get(roomId).values()].filter(p => p.socketId !== socket.id);
    socket.emit('room-peers', others);

    // Notify existing peers about the new joiner
    socket.to(roomId).emit('peer-joined', {
      socketId: socket.id,
      name: userName,
      role: role || 'student'
    });

    console.log(`[Room ${roomId}] ${userName} (${role}) joined — ${rooms.get(roomId).size} total`);

    // Flush any buffered ICE candidates for this socket
    if (iceCandidateBuffer.has(socket.id)) {
      const buffered = iceCandidateBuffer.get(socket.id);
      console.log(`[ICE Buffer] Flushing ${buffered.length} buffered candidates to ${socket.id}`);
      buffered.forEach(({ from, candidate }) => {
        socket.emit('ice-candidate', { from, candidate });
      });
      iceCandidateBuffer.delete(socket.id);
    }
  });

  // ── WebRTC signaling relay ─────────────────────────────────────────────────

  /**
   * offer — relay SDP offer from one peer to another.
   * Research: The server must be a pure relay. It must NOT modify the SDP.
   * Both initial offers and renegotiation offers use the same event.
   */
  // Perfect negotiation: relay description (offer or answer) between peers
  socket.on('description', ({ to, from, description }) => {
    if (!to || !description) return;
    console.log(`[Description:${description.type}] ${from || socket.id} → ${to}`);
    io.to(to).emit('description', { from: from || socket.id, description });
  });

  socket.on('offer', ({ to, offer, from, fromName }) => {
    if (!to || !offer) return;
    console.log(`[Offer] ${fromName || from} → ${to}`);
    io.to(to).emit('offer', { from: from || socket.id, fromName: fromName || '', offer });
  });

  socket.on('answer', ({ to, answer, from }) => {
    if (!to || !answer) return;
    console.log(`[Answer] ${from || socket.id} → ${to}`);
    io.to(to).emit('answer', { from: from || socket.id, answer });
  });

  /**
   * ice-candidate — relay ICE candidates between peers.
   *
   * Research finding (critical): ICE candidates can arrive at the signaling
   * server BEFORE the target peer has called setRemoteDescription. If we just
   * forward them immediately, the client will call addIceCandidate() before
   * the remote description is set, causing "Cannot add ICE candidate" errors
   * and a broken connection.
   *
   * Fix: Check if the target socket is currently connected. If not (they
   * haven't joined yet), buffer the candidate and flush it when they join.
   * If they ARE connected, forward immediately — the client-side code is
   * responsible for its own buffering after setRemoteDescription.
   */
  socket.on('ice-candidate', ({ to, candidate, from }) => {
    if (!to || !candidate) return;

    const targetSocket = io.sockets.sockets.get(to);

    if (targetSocket) {
      // Target is connected — forward immediately
      targetSocket.emit('ice-candidate', { from: from || socket.id, candidate });
    } else {
      // Target not connected yet — buffer the candidate
      if (!iceCandidateBuffer.has(to)) iceCandidateBuffer.set(to, []);
      iceCandidateBuffer.get(to).push({ from: from || socket.id, candidate });
      console.log(`[ICE Buffer] Buffered candidate for ${to} (not yet connected)`);
    }
  });

  // ── Media state broadcast ──────────────────────────────────────────────────

  socket.on('media-state', ({ roomId, audio, video, screen }) => {
    if (!roomId) return;
    socket.to(roomId).emit('peer-media-state', {
      socketId: socket.id,
      audio: !!audio,
      video: !!video,
      screen: !!screen
    });
  });

  // ── Chat ───────────────────────────────────────────────────────────────────

  socket.on('chat-message', ({ roomId, message, senderName, timestamp }) => {
    if (!roomId || !message) return;
    io.to(roomId).emit('chat-message', {
      message,
      senderName: senderName || 'Unknown',
      timestamp: timestamp || new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      socketId: socket.id
    });
  });

  // ── Tutor controls ─────────────────────────────────────────────────────────

  socket.on('mute-peer', ({ targetSocketId, roomId }) => {
    if (!targetSocketId) return;
    io.to(targetSocketId).emit('force-mute');
    if (roomId) socket.to(roomId).emit('peer-was-muted', { socketId: targetSocketId });
  });

  socket.on('kick-peer', ({ targetSocketId, roomId }) => {
    if (!targetSocketId) return;
    io.to(targetSocketId).emit('force-kick');
    if (roomId) socket.to(roomId).emit('peer-was-kicked', { socketId: targetSocketId });
  });

  // ── Disconnect ─────────────────────────────────────────────────────────────

  socket.on('disconnecting', () => {
    for (const roomId of socket.rooms) {
      if (!rooms.has(roomId)) continue;

      const peer = rooms.get(roomId).get(socket.id);
      if (!peer) continue;

      const isTutor = peer.role === 'tutor';
      const lessonId = peer.lessonId;

      // Remove from room
      rooms.get(roomId).delete(socket.id);
      if (rooms.get(roomId).size === 0) {
        rooms.delete(roomId);
        console.log(`[Room ${roomId}] Closed (empty)`);
      }

      // Clean up active meeting tracking
      if (isTutor && lessonId) {
        const meeting = activeMeetings.get(String(lessonId));
        if (meeting && meeting.tutorSocketId === socket.id) {
          activeMeetings.delete(String(lessonId));
        }
      }

      // Notify remaining peers
      if (isTutor) {
        socket.to(roomId).emit('tutor-left');
        console.log(`[Room ${roomId}] Tutor ${peer.name} left`);
      } else {
        socket.to(roomId).emit('peer-left', { socketId: socket.id });
        console.log(`[Room ${roomId}] ${peer.name} left`);
      }
    }

    // Clean up any buffered ICE candidates for this socket
    iceCandidateBuffer.delete(socket.id);
  });

  socket.on('disconnect', (reason) => {
    console.log(`[-] Disconnected: ${socket.id} (${reason})`);
  });
});

// ── Start ─────────────────────────────────────────────────────────────────────

const PORT = process.env.PORT || 3001;
server.listen(PORT, () => {
  console.log(`EnglishFlow Signaling Server running on port ${PORT}`);
});
```

---

## Backend - Java Controllers & Services


### File: `backend/courses-service/src/main/java/com/englishflow/courses/controller/OnlineMeetingSessionController.java`

```java
package com.englishflow.courses.controller;

import com.englishflow.courses.dto.OnlineMeetingSessionDTO;
import com.englishflow.courses.service.OnlineMeetingSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/meeting-sessions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OnlineMeetingSessionController {
    
    private final OnlineMeetingSessionService sessionService;
    
    @PostMapping
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> request) {
        try {
            Long lessonId = Long.valueOf(request.get("lessonId").toString());
            String roomId = request.get("roomId").toString();
            String inviteLink = request.get("inviteLink").toString();
            Long tutorId = Long.valueOf(request.get("tutorId").toString());
            
            OnlineMeetingSessionDTO session = sessionService.createSession(lessonId, roomId, inviteLink, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(session);
        } catch (Exception e) {
            log.error("Error creating meeting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create meeting session: " + e.getMessage()));
        }
    }
    
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<?> getActiveSessionByLessonId(@PathVariable Long lessonId) {
        try {
            Optional<OnlineMeetingSessionDTO> session = sessionService.getActiveSessionByLessonId(lessonId);
            if (session.isPresent()) {
                return ResponseEntity.ok(session.get());
            } else {
                return ResponseEntity.ok(Map.of("active", false));
            }
        } catch (Exception e) {
            log.error("Error getting meeting session for lesson {}", lessonId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get meeting session: " + e.getMessage()));
        }
    }
    
    @GetMapping("/room/{roomId}")
    public ResponseEntity<?> getActiveSessionByRoomId(@PathVariable String roomId) {
        try {
            Optional<OnlineMeetingSessionDTO> session = sessionService.getActiveSessionByRoomId(roomId);
            if (session.isPresent()) {
                return ResponseEntity.ok(session.get());
            } else {
                return ResponseEntity.ok(Map.of("active", false));
            }
        } catch (Exception e) {
            log.error("Error getting meeting session for room {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get meeting session: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<?> endSession(@PathVariable Long lessonId) {
        try {
            sessionService.endSession(lessonId);
            return ResponseEntity.ok(Map.of("message", "Meeting session ended successfully"));
        } catch (Exception e) {
            log.error("Error ending meeting session for lesson {}", lessonId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end meeting session: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<?> endSessionByRoomId(@PathVariable String roomId) {
        try {
            sessionService.endSessionByRoomId(roomId);
            return ResponseEntity.ok(Map.of("message", "Meeting session ended successfully"));
        } catch (Exception e) {
            log.error("Error ending meeting session for room {}", roomId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to end meeting session: " + e.getMessage()));
        }
    }
}
```

### File: `backend/courses-service/src/main/java/com/englishflow/courses/controller/OnlineLessonController.java`

```java
package com.englishflow.courses.controller;

import com.englishflow.courses.dto.OnlineLessonConfigDTO;
import com.englishflow.courses.dto.LessonSessionDTO;
import com.englishflow.courses.entity.OnlineLesson;
import com.englishflow.courses.service.OnlineLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/online-lessons")
@RequiredArgsConstructor
public class OnlineLessonController {
    
    private final OnlineLessonService onlineLessonService;
    
    @PostMapping("/configure")
    public ResponseEntity<OnlineLesson> configureOnlineLesson(@RequestBody OnlineLessonConfigDTO config) {
        OnlineLesson lesson = onlineLessonService.configureOnlineLesson(config);
        return ResponseEntity.ok(lesson);
    }
    
    @PostMapping("/generate-sessions")
    public ResponseEntity<String> generateSessions(@RequestParam(defaultValue = "4") int weeks) {
        onlineLessonService.generateSessionsForNextWeeks(weeks);
        return ResponseEntity.ok("Sessions generated for next " + weeks + " weeks");
    }
    
    @GetMapping("/student/{studentId}/upcoming")
    public ResponseEntity<List<LessonSessionDTO>> getUpcomingSessionsForStudent(@PathVariable Long studentId) {
        List<LessonSessionDTO> sessions = onlineLessonService.getUpcomingSessionsForStudent(studentId);
        return ResponseEntity.ok(sessions);
    }
    
    @PostMapping("/sessions/{sessionId}/attendance")
    public ResponseEntity<String> recordAttendance(
            @PathVariable Long sessionId,
            @RequestParam Long studentId,
            @RequestParam String joinTime,
            @RequestParam String leaveTime) {
        
        onlineLessonService.recordAttendance(
            sessionId,
            studentId,
            LocalTime.parse(joinTime),
            LocalTime.parse(leaveTime)
        );
        
        return ResponseEntity.ok("Attendance recorded");
    }
    
    @PostMapping("/{lessonId}/assign-time-slot")
    public ResponseEntity<?> assignTimeSlot(
            @PathVariable Long lessonId,
            @RequestParam Long tutorId,
            @RequestBody com.englishflow.courses.dto.AssignTimeSlotRequest request) {
        try {
            System.out.println("=== ASSIGN TIME SLOT DEBUG ===");
            System.out.println("Lesson ID: " + lessonId);
            System.out.println("Tutor ID: " + tutorId);
            System.out.println("Request: " + request);
            System.out.println("Day: " + request.getDayOfWeek());
            System.out.println("Start: " + request.getStartTime());
            System.out.println("End: " + request.getEndTime());
            
            java.time.LocalTime startTime = java.time.LocalTime.parse(request.getStartTime());
            java.time.LocalTime endTime = java.time.LocalTime.parse(request.getEndTime());
            
            System.out.println("Parsed times - Start: " + startTime + ", End: " + endTime);
            
            com.englishflow.courses.entity.LessonTimeAssignment assignment = 
                onlineLessonService.assignTimeSlot(lessonId, tutorId, request.getDayOfWeek(), startTime, endTime);
            
            System.out.println("Assignment created successfully: " + assignment.getId());
            return ResponseEntity.ok(assignment);
        } catch (Exception e) {
            System.err.println("ERROR in assignTimeSlot: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    @GetMapping("/{lessonId}/time-assignment")
    public ResponseEntity<?> getTimeAssignment(@PathVariable Long lessonId) {
        com.englishflow.courses.entity.LessonTimeAssignment assignment = 
            onlineLessonService.getTimeAssignment(lessonId);
        
        if (assignment == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(assignment);
    }
    
    @DeleteMapping("/{lessonId}/time-assignment")
    public ResponseEntity<String> removeTimeAssignment(@PathVariable Long lessonId) {
        onlineLessonService.removeTimeAssignment(lessonId);
        return ResponseEntity.ok("Time assignment removed");
    }
}
```

### File: `backend/courses-service/src/main/java/com/englishflow/courses/service/OnlineLessonService.java`

```java
package com.englishflow.courses.service;

import com.englishflow.courses.dto.OnlineLessonConfigDTO;
import com.englishflow.courses.dto.LessonSessionDTO;
import com.englishflow.courses.entity.*;
import com.englishflow.courses.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OnlineLessonService {
    
    private final OnlineLessonRepository onlineLessonRepository;
    private final LessonSessionRepository sessionRepository;
    private final SessionAttendanceRepository attendanceRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonTimeAssignmentRepository timeAssignmentRepository;
    private final TutorAvailabilitySlotService availabilitySlotService;
    
    public OnlineLesson configureOnlineLesson(OnlineLessonConfigDTO config) {
        Lesson lesson = lessonRepository.findById(config.getLessonId())
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        OnlineLesson onlineLesson = new OnlineLesson();
        onlineLesson.setLesson(lesson);
        onlineLesson.setDurationMinutes(config.getDuration());
        onlineLesson.setTimezone(config.getTimezone());
        onlineLesson.setStartDate(config.getStartDate());
        onlineLesson.setEndDate(config.getEndDate());
        
        OnlineLesson saved = onlineLessonRepository.save(onlineLesson);
        
        // Create schedules
        if (config.getSchedules() != null) {
            for (OnlineLessonConfigDTO.ScheduleDTO scheduleDTO : config.getSchedules()) {
                LessonSchedule schedule = new LessonSchedule();
                schedule.setOnlineLesson(saved);
                schedule.setDayOfWeek(scheduleDTO.getDayOfWeek());
                schedule.setTime(LocalTime.parse(scheduleDTO.getTime()));
                saved.getSchedules().add(schedule);
            }
        }
        
        return onlineLessonRepository.save(saved);
    }
    
    public void generateSessionsForNextWeeks(int weeks) {
        List<OnlineLesson> activeLessons = onlineLessonRepository.findActiveLessonsOnDate(LocalDate.now());
        
        for (OnlineLesson lesson : activeLessons) {
            generateSessionsForLesson(lesson, weeks);
        }
    }
    
    private void generateSessionsForLesson(OnlineLesson lesson, int weeks) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plus(weeks, ChronoUnit.WEEKS);
        
        LocalDate currentDate = today;
        while (currentDate.isBefore(endDate) || currentDate.isEqual(endDate)) {
            final LocalDate dateToCheck = currentDate; // Create final variable for lambda
            int dayOfWeek = currentDate.getDayOfWeek().getValue() % 7; // Convert to 0=Sunday
            
            for (LessonSchedule schedule : lesson.getSchedules()) {
                if (schedule.getDayOfWeek() == dayOfWeek &&
                    !dateToCheck.isBefore(lesson.getStartDate()) &&
                    (lesson.getEndDate() == null || !dateToCheck.isAfter(lesson.getEndDate()))) {
                    
                    // Check if session already exists
                    boolean exists = sessionRepository.findByOnlineLessonId(lesson.getId())
                        .stream()
                        .anyMatch(s -> s.getSessionDate().equals(dateToCheck) && 
                                     s.getSessionTime().equals(schedule.getTime()));
                    
                    if (!exists) {
                        LessonSession session = new LessonSession();
                        session.setOnlineLesson(lesson);
                        session.setSessionDate(dateToCheck);
                        session.setSessionTime(schedule.getTime());
                        session.setStatus("scheduled");
                        sessionRepository.save(session);
                    }
                }
            }
            
            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
        }
    }
    
    public List<LessonSessionDTO> getUpcomingSessionsForStudent(Long studentId) {
        // Get all courses the student is enrolled in
        List<Course> enrolledCourses = courseRepository.findEnrolledCoursesByStudentId(studentId);
        
        List<LessonSessionDTO> upcomingSessions = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (Course course : enrolledCourses) {
            for (Chapter chapter : course.getChapters()) {
                for (Lesson lesson : chapter.getLessons()) {
                    if (lesson.getLessonType().name().equals("ONLINE")) {
                        OnlineLesson onlineLesson = onlineLessonRepository.findByLessonId(lesson.getId())
                            .orElse(null);
                        
                        if (onlineLesson != null) {
                            List<LessonSession> sessions = sessionRepository
                                .findUpcomingSessionsForLesson(onlineLesson.getId(), today);
                            
                            for (LessonSession session : sessions) {
                                LessonSessionDTO dto = new LessonSessionDTO();
                                dto.setId(session.getId());
                                dto.setOnlineLessonId(onlineLesson.getId());
                                dto.setSessionDate(session.getSessionDate());
                                dto.setSessionTime(session.getSessionTime());
                                dto.setStatus(session.getStatus());
                                dto.setMeetingUrl(session.getMeetingUrl());
                                dto.setCourseName(course.getTitle());
                                dto.setLessonTitle(lesson.getTitle());
                                dto.setDurationMinutes(onlineLesson.getDurationMinutes());
                                dto.setTimezone(onlineLesson.getTimezone());
                                
                                upcomingSessions.add(dto);
                            }
                        }
                    }
                }
            }
        }
        
        return upcomingSessions.stream()
            .sorted((a, b) -> {
                int dateCompare = a.getSessionDate().compareTo(b.getSessionDate());
                if (dateCompare != 0) return dateCompare;
                return a.getSessionTime().compareTo(b.getSessionTime());
            })
            .collect(Collectors.toList());
    }
    
    public void recordAttendance(Long sessionId, Long studentId, LocalTime joinTime, LocalTime leaveTime) {
        LessonSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        SessionAttendance attendance = attendanceRepository.findBySessionIdAndStudentId(sessionId, studentId)
            .orElse(new SessionAttendance());
        
        attendance.setSession(session);
        attendance.setStudentId(studentId);
        attendance.setJoinedAt(java.time.LocalDateTime.of(session.getSessionDate(), joinTime));
        attendance.setLeftAt(java.time.LocalDateTime.of(session.getSessionDate(), leaveTime));
        
        // Calculate attendance percentage
        long totalDuration = java.time.temporal.ChronoUnit.MINUTES.between(
            session.getSessionTime(), 
            session.getSessionTime().plus(session.getOnlineLesson().getDurationMinutes(), java.time.temporal.ChronoUnit.MINUTES)
        );
        
        long attendedDuration = java.time.temporal.ChronoUnit.MINUTES.between(joinTime, leaveTime);
        java.math.BigDecimal percentage = java.math.BigDecimal.valueOf((double) attendedDuration / totalDuration * 100)
            .setScale(2, java.math.RoundingMode.HALF_UP);
        
        attendance.setAttendancePercentage(percentage);
        
        // Mark as attended if >= 80%
        if (percentage.compareTo(java.math.BigDecimal.valueOf(80)) >= 0) {
            attendance.setAttendanceStatus("attended");
        } else if (attendedDuration > 0) {
            attendance.setAttendanceStatus("partial");
        } else {
            attendance.setAttendanceStatus("absent");
        }
        
        attendanceRepository.save(attendance);
    }
    
    public LessonTimeAssignment assignTimeSlot(Long lessonId, Long tutorId, 
                                                com.englishflow.courses.enums.DayOfWeek dayOfWeek, 
                                                LocalTime startTime, LocalTime endTime) {
        // Validate lesson exists and is ONLINE type
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new RuntimeException("Lesson not found"));
        
        if (lesson.getLessonType() != com.englishflow.courses.enums.LessonType.ONLINE) {
            throw new RuntimeException("Can only assign time slots to ONLINE lessons");
        }
        
        // Check if tutor has availability configured
        if (!availabilitySlotService.tutorHasAvailability(tutorId)) {
            throw new RuntimeException("Tutor must configure availability first");
        }
        
        // Check if slot is available
        if (!availabilitySlotService.isSlotAvailable(tutorId, dayOfWeek, startTime)) {
            throw new RuntimeException("This time slot is already booked");
        }
        
        // Remove existing assignment if any
        timeAssignmentRepository.findByLessonId(lessonId)
            .ifPresent(timeAssignmentRepository::delete);
        
        // Create new assignment
        LessonTimeAssignment assignment = new LessonTimeAssignment();
        assignment.setLesson(lesson);
        assignment.setTutorId(tutorId);
        assignment.setDayOfWeek(dayOfWeek);
        assignment.setStartTime(startTime);
        assignment.setEndTime(endTime);
        
        return timeAssignmentRepository.save(assignment);
    }
    
    public LessonTimeAssignment getTimeAssignment(Long lessonId) {
        return timeAssignmentRepository.findByLessonId(lessonId).orElse(null);
    }
    
    public void removeTimeAssignment(Long lessonId) {
        timeAssignmentRepository.findByLessonId(lessonId)
            .ifPresent(timeAssignmentRepository::delete);
    }
}
```

---

## Frontend - Meeting Components
