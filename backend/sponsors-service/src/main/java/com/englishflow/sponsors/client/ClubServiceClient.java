package com.englishflow.sponsors.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClubServiceClient {

    private final RestTemplate restTemplate;

    @Value("${club.service.url:http://localhost:8085}")
    private String clubServiceUrl;

    /**
     * Returns the userId of the club president, or null if not found.
     */
    public Long getClubPresidentUserId(Integer clubId) {
        try {
            String url = clubServiceUrl + "/members/club/" + clubId;
            List members = restTemplate.getForObject(url, List.class);
            if (members == null) return null;
            for (Object m : members) {
                if (m instanceof Map<?, ?> member) {
                    Object rank = member.get("rank");
                    if ("PRESIDENT".equals(rank)) {
                        Object userId = member.get("userId");
                        if (userId instanceof Number n) return n.longValue();
                        if (userId instanceof String s) return Long.parseLong(s);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get club president for clubId {}: {}", clubId, e.getMessage());
        }
        return null;
    }

    /**
     * Creates an expense entry in the club treasury for the sponsorship allocation.
     */
    public void createSponsorshipExpense(Integer clubId, Double amount, String sponsorName) {
        try {
            String url = clubServiceUrl + "/expenses";

            java.util.Map<String, Object> body = new java.util.HashMap<>();
            body.put("clubId", clubId);
            body.put("designation", "💰 Sponsorship received from " + sponsorName);
            body.put("amount", amount);
            body.put("expenseDate", java.time.LocalDateTime.now().toString());
            body.put("createdBy", 0);   // 0 = system / automatic
            body.put("notes", "SPONSORSHIP_INCOME — Automatic allocation from sponsor contribution approved by Academic Manager");

            restTemplate.postForObject(url, body, Object.class);
            log.info("Sponsorship expense created for club {} — {} DT from {}", clubId, amount, sponsorName);
        } catch (Exception e) {
            log.error("Failed to create sponsorship expense for club {}: {}", clubId, e.getMessage());
        }
    }
}
