package com.englishflow.club.service;

import com.englishflow.club.client.AuthServiceClient;
import com.englishflow.club.dto.ExpenseDTO;
import com.englishflow.club.dto.MembershipRequestDTO;
import com.englishflow.club.dto.UserInfoDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.MembershipRequest;
import com.englishflow.club.enums.ClubHistoryType;
import com.englishflow.club.enums.MembershipRequestStatus;
import com.englishflow.club.exception.ClubFullException;
import com.englishflow.club.exception.ClubNotFoundException;
import com.englishflow.club.exception.DuplicateMemberException;
import com.englishflow.club.exception.UnauthorizedException;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.MemberRepository;
import com.englishflow.club.repository.MembershipRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipRequestService {
    
    private final MembershipRequestRepository requestRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final WebSocketNotificationService wsNotificationService;
    private final AuthServiceClient authServiceClient;
    private final ClubHistoryService clubHistoryService;
    private final ExpenseService expenseService;
    
    @Transactional
    public MembershipRequestDTO createRequest(Integer clubId, Long userId, String message, String motivationLetter, String studentSkills) {
        log.info("Creating membership request for user {} to club {}", userId, clubId);
        
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new ClubNotFoundException(clubId));
        
        // Check if user is already a member
        if (memberRepository.existsByClubIdAndUserId(clubId, userId)) {
            throw new DuplicateMemberException("User is already a member of this club");
        }
        
        // Check if there's already a request (any status)
        Optional<MembershipRequest> existingRequest = requestRepository.findByClubIdAndUserId(clubId, userId);
        if (existingRequest.isPresent()) {
            MembershipRequest existing = existingRequest.get();
            
            if (existing.getStatus() == MembershipRequestStatus.PENDING) {
                throw new DuplicateMemberException("A pending request already exists");
            }

            if (existing.getStatus() == MembershipRequestStatus.PAYMENT_PENDING) {
                throw new DuplicateMemberException("Your request has been approved, please proceed with the payment");
            }
            
            // If there's an old request (approved/rejected), delete it to allow a new one
            log.info("Deleting old membership request (status: {}) for user {} to club {}", 
                existing.getStatus(), userId, clubId);
            requestRepository.delete(existing);
        }
        
        MembershipRequest request = MembershipRequest.builder()
                .club(club)
                .userId(userId)
                .message(message)
                .motivationLetter(motivationLetter)
                .studentSkills(studentSkills)
                .status(MembershipRequestStatus.PENDING)
                .build();
        
        MembershipRequest savedRequest = requestRepository.save(request);
        
        // Get user info for notification and email
        UserInfoDTO userInfo = authServiceClient.getUserInfo(userId);
        String userName = userInfo.getFirstName() + " " + userInfo.getLastName();
        
        // Send WebSocket notification to club president
        wsNotificationService.notifyNewMembershipRequest(
            clubId.longValue(),
            club.getName(),
            userId,
            userName
        );
        
        // Send email notification to the user
        try {
            authServiceClient.sendClubMembershipRequestPendingEmail(
                userInfo.getEmail(),
                userInfo.getFirstName(),
                club.getName(),
                message
            );
            log.info("Email notification sent to user {} for membership request", userId);
        } catch (Exception e) {
            log.error("Failed to send email notification to user {}: {}", userId, e.getMessage());
            // Don't fail the request if email fails
        }
        
        log.info("Membership request created with id {}", savedRequest.getId());
        return toDTO(savedRequest);
    }
    
    @Transactional(readOnly = true)
    public Double getTotalConfirmedPayments(Integer clubId) {
        return requestRepository.findByClubId(clubId).stream()
                .filter(r -> r.getStatus() == MembershipRequestStatus.APPROVED && r.getPaymentConfirmedAt() != null)
                .mapToDouble(r -> {
                    Double fee = r.getClub().getRegistrationFee();
                    return fee != null ? fee : 0.0;
                })
                .sum();
    }

    /**
     * Backfill missing income entries in treasury for all confirmed payments.
     * Safe to call multiple times — skips requests that already have an expense entry.
     */
    @Transactional
    public int backfillTreasuryIncomeEntries(Integer clubId) {
        List<MembershipRequest> confirmed = requestRepository.findByClubId(clubId).stream()
                .filter(r -> r.getStatus() == MembershipRequestStatus.APPROVED && r.getPaymentConfirmedAt() != null)
                .collect(Collectors.toList());

        int created = 0;
        for (MembershipRequest r : confirmed) {
            Double fee = r.getClub().getRegistrationFee();
            if (fee == null || fee <= 0) continue;

            // Check if an income entry already exists for this payment token
            String token = r.getPaymentToken() != null ? r.getPaymentToken() : "MANUAL_" + r.getId();
            boolean alreadyExists = expenseService.existsIncomeEntryForToken(clubId, token);
            if (alreadyExists) continue;

            ExpenseDTO incomeEntry = ExpenseDTO.builder()
                    .clubId(r.getClub().getId())
                    .designation("Registration fee from member #" + r.getUserId())
                    .amount(fee)
                    .expenseDate(r.getPaymentConfirmedAt())
                    .createdBy(r.getUserId())
                    .notes("REGISTRATION_FEE_INCOME | payment: " + token)
                    .source("REGISTRATION_FEE")
                    .build();
            expenseService.createIncomeEntry(incomeEntry);
            created++;
            log.info("Backfilled income entry for membership request {} (user {}, amount {} TND)", r.getId(), r.getUserId(), fee);
        }
        log.info("Backfill complete for club {}: {} entries created", clubId, created);
        return created;
    }

    @Transactional(readOnly = true)
    public List<MembershipRequestDTO> getAllRequestsForClub(Integer clubId) {
        return requestRepository.findByClubId(clubId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MembershipRequestDTO> getPendingRequestsForClub(Integer clubId) {
        log.debug("Fetching pending requests for club {}", clubId);
        List<MembershipRequest> requests = requestRepository.findByClubIdAndStatus(clubId, MembershipRequestStatus.PENDING);
        
        // Get all user IDs
        List<Long> userIds = requests.stream()
                .map(MembershipRequest::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        // Fetch user info in batch
        Map<Long, UserInfoDTO> userInfoMap = authServiceClient.getUserInfoBatch(userIds);
        
        // Convert to DTOs with user info
        return requests.stream()
                .map(request -> toDTO(request, userInfoMap.get(request.getUserId())))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MembershipRequestDTO> getUserRequests(Long userId) {
        log.debug("Fetching requests for user {}", userId);
        return requestRepository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MembershipRequestDTO approveRequest(Integer requestId, Long reviewerId) {
        log.info("Approving membership request {} by user {}", requestId, reviewerId);
        
        MembershipRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Check if reviewer has management role (President, Vice President, or Secretary)
        if (!memberService.hasManagementRole(request.getClub().getId(), reviewerId)) {
            throw new UnauthorizedException("Only President, Vice President, or Secretary can approve membership requests");
        }
        
        if (request.getStatus() != MembershipRequestStatus.PENDING) {
            throw new RuntimeException("Request has already been reviewed");
        }
        
        // Check if club is full
        if (request.getClub().isFull()) {
            throw new ClubFullException(request.getClub().getMaxMembers());
        }

        Double fee = request.getClub().getRegistrationFee();
        boolean hasFee = fee != null && fee > 0;

        // Set status and deadline
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        if (hasFee) {
            request.setStatus(MembershipRequestStatus.PAYMENT_PENDING);
            request.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        } else {
            request.setStatus(MembershipRequestStatus.APPROVED);
        }
        requestRepository.save(request);

        // Add member immediately (provisional access if fee required)
        memberService.addMemberToClub(request.getClub().getId(), request.getUserId());
        log.info("User {} added to club {} - hasFee: {}, deadline: {}", request.getUserId(), request.getClub().getId(), hasFee, request.getPaymentDeadline());

        if (!hasFee) {
            log.info("Membership request {} approved directly (no fee)", requestId);
            return toDTO(request);
        }
        
        // Get user info to send payment email
        UserInfoDTO userInfo = authServiceClient.getUserInfo(request.getUserId());
        
        // Send payment email with link to payment page
        try {
            String paymentLink = "http://localhost:4200/user-panel/club-payment/" + request.getId();
            authServiceClient.sendClubPaymentRequiredEmail(
                userInfo.getEmail(),
                userInfo.getFirstName(),
                request.getClub().getName(),
                request.getClub().getRegistrationFee(),
                paymentLink
            );
            log.info("Payment email sent to user {} for membership request {}", request.getUserId(), requestId);
        } catch (Exception e) {
            log.error("Failed to send payment email to user {}: {}", request.getUserId(), e.getMessage());
        }
        
        log.info("Membership request {} set to PAYMENT_PENDING", requestId);
        return toDTO(request);
    }
    
    @Transactional(readOnly = true)
    public MembershipRequestDTO getRequestById(Integer requestId) {
        MembershipRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return toDTO(request);
    }

    @Transactional
    public MembershipRequestDTO confirmPayment(Integer requestId, String paymentMethod, String paymentToken) {
        log.info("Confirming payment for membership request {} via {}", requestId, paymentMethod);

        MembershipRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Allow idempotent confirmation: if already APPROVED with same token, return existing
        if (request.getStatus() == MembershipRequestStatus.APPROVED) {
            log.info("Payment already confirmed for request {}, returning existing data", requestId);
            return toDTO(request);
        }

        if (request.getStatus() != MembershipRequestStatus.PAYMENT_PENDING) {
            throw new RuntimeException("Request is not awaiting payment");
        }

        request.setStatus(MembershipRequestStatus.APPROVED);
        request.setPaymentMethod(paymentMethod);
        request.setPaymentToken(paymentToken);
        request.setPaymentConfirmedAt(LocalDateTime.now());

        MembershipRequest updatedRequest = requestRepository.save(request);

        // User was already added as provisional member during approveRequest()
        // No need to add again, just confirm the payment status

        // Log history
        clubHistoryService.logHistory(
            request.getClub().getId().longValue(),
            request.getUserId(),
            ClubHistoryType.PAYMENT_CONFIRMED,
            "Payment Confirmed",
            "Registration fee paid: " + request.getClub().getRegistrationFee() + " DT via " + paymentMethod,
            null,
            paymentToken,
            request.getUserId()
        );

        // Auto-create income entry in treasury
        Double fee = request.getClub().getRegistrationFee();
        if (fee != null && fee > 0) {
            ExpenseDTO incomeEntry = ExpenseDTO.builder()
                    .clubId(request.getClub().getId())
                    .designation("Registration fee from member #" + request.getUserId())
                    .amount(fee)
                    .expenseDate(LocalDateTime.now())
                    .createdBy(request.getUserId())
                    .notes("REGISTRATION_FEE_INCOME | payment: " + paymentToken)
                    .source("REGISTRATION_FEE")
                    .build();
            expenseService.createIncomeEntry(incomeEntry);
            log.info("Income entry created in treasury for membership request {}, amount: {} TND", requestId, fee);
        }

        log.info("Payment confirmed for membership request {}, user {} added to club {}", 
            requestId, request.getUserId(), request.getClub().getId());
        return toDTO(updatedRequest);
    }

    @Transactional
    public MembershipRequestDTO rejectRequest(Integer requestId, Long reviewerId, String comment) {
        log.info("Rejecting membership request {} by user {}", requestId, reviewerId);
        
        MembershipRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        // Check if reviewer has management role (President, Vice President, or Secretary)
        if (!memberService.hasManagementRole(request.getClub().getId(), reviewerId)) {
            throw new UnauthorizedException("Only President, Vice President, or Secretary can reject membership requests");
        }
        
        if (request.getStatus() != MembershipRequestStatus.PENDING) {
            throw new RuntimeException("Request has already been reviewed");
        }
        
        request.setStatus(MembershipRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        request.setReviewComment(comment);
        
        MembershipRequest updatedRequest = requestRepository.save(request);
        
        log.info("Membership request {} rejected", requestId);
        return toDTO(updatedRequest);
    }
    
    private MembershipRequestDTO toDTO(MembershipRequest request) {
        // Fetch user info from auth service
        UserInfoDTO userInfo = authServiceClient.getUserInfo(request.getUserId());
        return toDTO(request, userInfo);
    }
    
    private MembershipRequestDTO toDTO(MembershipRequest request, UserInfoDTO userInfo) {
        String userName = "User " + request.getUserId();
        String userEmail = "user" + request.getUserId() + "@example.com";
        
        if (userInfo != null) {
            userName = userInfo.getFirstName() + " " + userInfo.getLastName();
            userEmail = userInfo.getEmail();
        }
        
        return MembershipRequestDTO.builder()
                .id(request.getId())
                .clubId(request.getClub().getId())
                .clubName(request.getClub().getName())
                .registrationFee(request.getClub().getRegistrationFee())
                .userId(request.getUserId())
                .userName(userName)
                .userEmail(userEmail)
                .status(request.getStatus())
                .message(request.getMessage())
                .motivationLetter(request.getMotivationLetter())
                .studentSkills(request.getStudentSkills())
                .requestedAt(request.getRequestedAt())
                .reviewedAt(request.getReviewedAt())
                .reviewedBy(request.getReviewedBy())
                .reviewComment(request.getReviewComment())
                .paymentMethod(request.getPaymentMethod())
                .paymentToken(request.getPaymentToken())
                .paymentConfirmedAt(request.getPaymentConfirmedAt())
                .paymentDeadline(request.getPaymentDeadline())
                .build();
    }
}
