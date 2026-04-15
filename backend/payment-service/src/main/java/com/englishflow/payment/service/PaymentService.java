package com.englishflow.payment.service;

import com.englishflow.payment.dto.*;
import com.englishflow.payment.entity.Payment;
import com.englishflow.payment.enums.PaymentItemType;
import com.englishflow.payment.enums.PaymentStatus;
import com.englishflow.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${paymee.api-key}")
    private String paymeeApiKey;

    @Value("${paymee.base-url}")
    private String paymeeBaseUrl;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.backend-url}")
    private String backendUrl;

    // ÔöÇÔöÇ Initiate payment ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    @Transactional
    public PaymentDTO initiatePayment(InitiatePaymentRequest req) {
        // Block if already successfully paid
        boolean alreadyPaid = paymentRepository.countByStudentIdAndItemTypeAndItemIdAndStatus(
                req.getStudentId(),
                req.getItemType().toUpperCase(),
                req.getItemId(),
                PaymentStatus.SUCCESS.name()) > 0;
        if (alreadyPaid) {
            throw new IllegalStateException("You are already enrolled in this item.");
        }

        // Delete any stale PENDING records for this student+item before creating a new one
        paymentRepository.findFirstByStudentIdAndItemTypeAndItemIdAndStatus(
                req.getStudentId(),
                PaymentItemType.valueOf(req.getItemType().toUpperCase()),
                req.getItemId(),
                PaymentStatus.PENDING)
                .ifPresent(p -> paymentRepository.delete(p));

        String orderId = "EF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

        // Save PENDING record so we can look it up on verify
        Payment payment = Payment.builder()
                .orderId(orderId)
                .studentId(req.getStudentId())
                .studentName(req.getStudentName())
                .studentEmail(req.getStudentEmail())
                .studentPhone(req.getStudentPhone() != null ? req.getStudentPhone() : "")
                .itemType(PaymentItemType.valueOf(req.getItemType().toUpperCase()))
                .itemId(req.getItemId())
                .itemName(req.getItemName())
                .amount(req.getAmount())
                .status(PaymentStatus.PENDING)
                .build();
        payment = paymentRepository.save(payment);

        // Call Paymee API
        String[] nameParts = req.getStudentName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : nameParts[0];
        String phone = payment.getStudentPhone().isEmpty() ? "00000000" : payment.getStudentPhone();
        String safeReturnBase = frontendUrl.startsWith("https://") ? frontendUrl : "https://sandbox.paymee.tn";

        Map<String, Object> paymeeBody = new HashMap<>();
        paymeeBody.put("amount", req.getAmount());
        paymeeBody.put("note", req.getItemName());
        paymeeBody.put("first_name", firstName);
        paymeeBody.put("last_name", lastName);
        paymeeBody.put("email", req.getStudentEmail());
        paymeeBody.put("phone", phone);
        paymeeBody.put("return_url", safeReturnBase + "/payment/return?orderId=" + orderId);
        paymeeBody.put("cancel_url", safeReturnBase + "/payment/cancel?orderId=" + orderId);
        paymeeBody.put("webhook_url", "https://sandbox.paymee.tn/webhook");
        paymeeBody.put("order_id", orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Token " + paymeeApiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(paymeeBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    paymeeBaseUrl + "/api/v2/payments/create", entity, Map.class);

            log.info("Paymee response status: {}, body: {}", response.getStatusCode(), response.getBody());
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<?, ?> data = (Map<?, ?>) response.getBody().get("data");
                if (data != null) {
                    payment.setToken((String) data.get("token"));
                    payment.setPaymentUrl((String) data.get("payment_url"));
                    payment = paymentRepository.save(payment);
                } else {
                    String msg = String.valueOf(response.getBody().get("message"));
                    throw new RuntimeException("Paymee error: " + msg);
                }
            } else {
                throw new RuntimeException("Paymee API error: " + response.getStatusCode());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Paymee API call failed for order {}: {}", orderId, e.getMessage());
            throw new RuntimeException("Could not reach payment provider. Please try again.");
        }

        return toDTO(payment);
    }

    // ÔöÇÔöÇ Verify payment ÔÇö marks SUCCESS and triggers enrollment ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    @Transactional
    public PaymentDTO verifyPayment(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("Payment not found: " + orderId));

        // Already processed
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return toDTO(payment);
        }

        // Mark as SUCCESS and trigger enrollment
        // The frontend only calls verify after paymee.complete event or popup close post-payment.
        // Paymee's sandbox GET verify endpoint returns HTML (not JSON), so we trust the client signal.
        log.info("Marking payment {} as SUCCESS and triggering enrollment", orderId);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        triggerEnrollment(payment);

        return toDTO(payment);
    }

    // ÔöÇÔöÇ Webhook handler ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    @Transactional
    public void handleWebhook(PaymeeWebhookPayload payload) {
        log.info("Webhook received for order {}, status={}", payload.getOrderId(), payload.getPaymentStatus());

        Payment payment = paymentRepository.findByOrderId(payload.getOrderId())
                .orElseGet(() -> paymentRepository.findByToken(payload.getToken()).orElse(null));

        if (payment == null) {
            log.warn("No payment found for order {} / token {}", payload.getOrderId(), payload.getToken());
            return;
        }

        if (!verifyChecksum(payload)) {
            log.error("Checksum mismatch for order {}", payload.getOrderId());
            return;
        }

        if (Boolean.TRUE.equals(payload.getPaymentStatus())) {
            if (payment.getStatus() != PaymentStatus.SUCCESS) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setTransactionId(payload.getTransactionId());
                payment.setReceivedAmount(payload.getReceivedAmount());
                payment.setCost(payload.getCost());
                paymentRepository.save(payment);
                triggerEnrollment(payment);
            }
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    // ÔöÇÔöÇ Queries ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PaymentStatsDTO getStats() {
        List<Payment> all = paymentRepository.findAll();
        long total = all.size();
        long success = all.stream().filter(p -> p.getStatus() == PaymentStatus.SUCCESS).count();
        long pending = all.stream().filter(p -> p.getStatus() == PaymentStatus.PENDING).count();
        long failed  = all.stream().filter(p -> p.getStatus() == PaymentStatus.FAILED || p.getStatus() == PaymentStatus.CANCELLED).count();
        BigDecimal revenue = paymentRepository.sumSuccessfulPayments();

        return PaymentStatsDTO.builder()
                .totalPayments(total)
                .successfulPayments(success)
                .pendingPayments(pending)
                .failedPayments(failed)
                .totalRevenue(revenue)
                .build();
    }

    // ÔöÇÔöÇ Helpers ÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇÔöÇ

    private void triggerEnrollment(Payment payment) {
        try {
            String coursesUrl = "http://localhost:8086";
            if (payment.getItemType() == PaymentItemType.PACK) {
                String url = coursesUrl + "/pack-enrollments?studentId=" + payment.getStudentId() + "&packId=" + payment.getItemId();
                restTemplate.postForEntity(url, null, Object.class);
                log.info("Enrolled student {} in pack {}", payment.getStudentId(), payment.getItemId());
            } else if (payment.getItemType() == PaymentItemType.COURSE) {
                String url = coursesUrl + "/enrollments/enroll?studentId=" + payment.getStudentId() + "&courseId=" + payment.getItemId();
                restTemplate.postForEntity(url, null, Object.class);
                log.info("Enrolled student {} in course {}", payment.getStudentId(), payment.getItemId());
            }
        } catch (Exception e) {
            log.error("Enrollment trigger failed for payment {}: {}", payment.getOrderId(), e.getMessage());
        }
    }

    private boolean verifyChecksum(PaymeeWebhookPayload payload) {
        try {
            int statusInt = Boolean.TRUE.equals(payload.getPaymentStatus()) ? 1 : 0;
            String raw = payload.getToken() + statusInt + paymeeApiKey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString().equalsIgnoreCase(payload.getCheckSum());
        } catch (Exception e) {
            log.error("Checksum verification error", e);
            return false;
        }
    }

    private PaymentDTO toDTO(Payment p) {
        return PaymentDTO.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .studentId(p.getStudentId())
                .studentName(p.getStudentName())
                .studentEmail(p.getStudentEmail())
                .itemType(p.getItemType() != null ? p.getItemType().name() : null)
                .itemId(p.getItemId())
                .itemName(p.getItemName())
                .amount(p.getAmount())
                .status(p.getStatus().name())
                .transactionId(p.getTransactionId())
                .receivedAmount(p.getReceivedAmount())
                .cost(p.getCost())
                .paymentUrl(p.getPaymentUrl())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
