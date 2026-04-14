package com.englishflow.event.controller;

import com.englishflow.event.scheduler.EventReminderScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test endpoint to manually trigger SMS reminders.
 * Remove in production.
 */
@RestController
@RequestMapping("/events/test")
@RequiredArgsConstructor
public class ReminderTestController {

    private final EventReminderScheduler scheduler;

    @PostMapping("/reminder-day-before")
    public ResponseEntity<String> triggerDayBeforeReminder() {
        scheduler.sendDayBeforeReminders();
        return ResponseEntity.ok("Day-before reminder triggered — check logs");
    }

    @PostMapping("/reminder-start")
    public ResponseEntity<String> triggerStartReminder() {
        scheduler.sendEventStartNotifications();
        return ResponseEntity.ok("Start reminder triggered — check logs");
    }
}
