package com.englishflow.event.scheduler;

import com.englishflow.event.client.AuthServiceClient;
import com.englishflow.event.entity.Event;
import com.englishflow.event.entity.Participant;
import com.englishflow.event.repository.EventRepository;
import com.englishflow.event.repository.ParticipantRepository;
import com.englishflow.event.service.TwilioSmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventReminderScheduler {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final AuthServiceClient authServiceClient;
    private final TwilioSmsService twilioSmsService;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm a");

    /**
     * Runs every hour — sends SMS reminder 24h before event starts.
     */
    @Scheduled(fixedRate = 3600000)
    @Transactional(readOnly = true)
    public void sendDayBeforeReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.plusHours(23);
        LocalDateTime windowEnd = now.plusHours(25);

        log.info("🔔 Checking events starting between {} and {}", windowStart, windowEnd);

        List<Event> upcomingEvents = eventRepository
                .findByStartDateBetweenAndStatus(windowStart, windowEnd, com.englishflow.event.enums.EventStatus.APPROVED);

        if (upcomingEvents.isEmpty()) {
            log.debug("No events to remind for this window");
            return;
        }

        for (Event event : upcomingEvents) {
            sendRemindersForEvent(event, "REMINDER");
        }
    }

    /**
     * Runs every minute — sends SMS when event starts.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = true)
    public void sendEventStartNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(1);
        LocalDateTime windowEnd = now.plusMinutes(1);

        List<Event> startingEvents = eventRepository
                .findByStartDateBetweenAndStatus(windowStart, windowEnd, com.englishflow.event.enums.EventStatus.APPROVED);

        for (Event event : startingEvents) {
            sendRemindersForEvent(event, "START");
        }
    }

    private void sendRemindersForEvent(Event event, String type) {
        List<Participant> participants = participantRepository.findByEventId(event.getId());
        if (participants.isEmpty()) return;

        List<Long> userIds = participants.stream()
                .map(Participant::getUserId)
                .collect(Collectors.toList());

        Map<Long, AuthServiceClient.UserInfo> userInfoMap = authServiceClient.getUsersByIds(userIds);

        String formattedDate = event.getStartDate().format(FORMATTER);

        for (Participant participant : participants) {
            AuthServiceClient.UserInfo user = userInfoMap.get(participant.getUserId());
            if (user == null || user.getPhone() == null || user.getPhone().isBlank()) continue;

            String message;
            if ("REMINDER".equals(type)) {
                message = String.format(
                    "🎉 Reminder: \"%s\" starts tomorrow on %s. Don't miss it! — Jungle in English",
                    event.getTitle(), formattedDate
                );
            } else {
                message = String.format(
                    "🚀 \"%s\" is starting NOW! Join at: %s — Jungle in English",
                    event.getTitle(),
                    event.getFormat() != null && event.getFormat().name().equals("ONLINE")
                        ? "http://localhost:4200/user-panel/events/" + event.getId() + "/live"
                        : event.getLocation()
                );
            }

            twilioSmsService.sendSms(user.getPhone(), message);
            log.info("📱 {} SMS sent to user {} for event '{}'", type, user.getId(), event.getTitle());
        }
    }
}
