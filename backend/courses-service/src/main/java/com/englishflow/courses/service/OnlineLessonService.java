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
    
    public List<com.englishflow.courses.dto.TutorOnlineLessonDTO> getTutorScheduledLessons(Long tutorId) {
        List<LessonTimeAssignment> assignments = timeAssignmentRepository.findByTutorId(tutorId);
        
        return assignments.stream()
            .map(assignment -> {
                Lesson lesson = assignment.getLesson();
                Chapter chapter = lesson.getChapter();
                Course course = chapter.getCourse();
                
                com.englishflow.courses.dto.TutorOnlineLessonDTO dto = new com.englishflow.courses.dto.TutorOnlineLessonDTO();
                dto.setLessonId(lesson.getId());
                dto.setLessonTitle(lesson.getTitle());
                dto.setCourseTitle(course.getTitle());
                dto.setDayOfWeek(assignment.getDayOfWeek());
                dto.setStartTime(assignment.getStartTime());
                dto.setEndTime(assignment.getEndTime());
                
                return dto;
            })
            .collect(Collectors.toList());
    }
}
