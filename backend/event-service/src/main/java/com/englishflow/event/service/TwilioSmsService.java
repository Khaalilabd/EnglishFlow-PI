package com.englishflow.event.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromNumber;

    private boolean enabled = false;

    @PostConstruct
    public void init() {
        log.info("Twilio init — SID: '{}', Token length: {}, Phone: '{}'",
            accountSid, authToken != null ? authToken.length() : 0, fromNumber);
        if (accountSid != null && !accountSid.isBlank()
                && authToken != null && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            enabled = true;
            log.info("✅ Twilio SMS service initialized");
        } else {
            log.warn("⚠️ Twilio credentials not configured — SMS reminders disabled");
        }
    }

    /**
     * Send an SMS to a phone number.
     * @param to   recipient phone number in E.164 format (e.g. +21612345678)
     * @param body message text
     */
    public void sendSms(String to, String body) {
        if (!enabled) {
            log.warn("Twilio not configured — skipping SMS to {}", to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("No phone number provided — skipping SMS");
            return;
        }
        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    body
            ).create();
            log.info("✅ SMS sent to {} — SID: {}", to, message.getSid());
        } catch (Exception e) {
            log.error("❌ Failed to send SMS to {}: {}", to, e.getMessage());
        }
    }
}
