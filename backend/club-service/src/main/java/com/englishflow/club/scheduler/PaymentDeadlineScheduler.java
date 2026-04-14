package com.englishflow.club.scheduler;

import com.englishflow.club.entity.MembershipRequest;
import com.englishflow.club.enums.ClubHistoryType;
import com.englishflow.club.enums.MembershipRequestStatus;
import com.englishflow.club.repository.MembershipRequestRepository;
import com.englishflow.club.service.ClubHistoryService;
import com.englishflow.club.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentDeadlineScheduler {

    private final MembershipRequestRepository requestRepository;
    private final MemberService memberService;
    private final ClubHistoryService clubHistoryService;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireUnpaidRequests() {
        List<MembershipRequest> expired = requestRepository.findExpiredPaymentRequests(LocalDateTime.now());

        if (expired.isEmpty()) {
            log.info("No expired payment requests found.");
            return;
        }

        log.info("Found {} expired payment request(s) to process.", expired.size());

        for (MembershipRequest request : expired) {
            try {
                Integer clubId = request.getClub().getId();
                Long userId = request.getUserId();

                // Remove member from club
                memberService.removeMemberByUserAndClub(clubId, userId);

                // Mark request as EXPIRED
                request.setStatus(MembershipRequestStatus.EXPIRED);
                requestRepository.save(request);

                // Log history
                clubHistoryService.logHistory(
                    clubId.longValue(),
                    userId,
                    ClubHistoryType.MEMBER_REMOVED,
                    "Payment Deadline Expired",
                    "Member removed automatically: payment not completed within 3 days.",
                    MembershipRequestStatus.PAYMENT_PENDING.name(),
                    MembershipRequestStatus.EXPIRED.name(),
                    null
                );

                log.info("User {} removed from club {} — payment deadline expired.", userId, clubId);
            } catch (Exception e) {
                log.error("Failed to expire request {} for user {}: {}", request.getId(), request.getUserId(), e.getMessage());
            }
        }
    }
}
