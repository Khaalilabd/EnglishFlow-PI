package com.englishflow.payment.controller;

import com.englishflow.payment.dto.*;
import com.englishflow.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentDTO> initiate(@Valid @RequestBody InitiatePaymentRequest req) {
        return ResponseEntity.ok(paymentService.initiatePayment(req));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody PaymeeWebhookPayload payload) {
        paymentService.handleWebhook(payload);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/{orderId}")
    public ResponseEntity<PaymentDTO> verify(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(paymentService.verifyPayment(orderId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentDTO>> byStudent(@PathVariable("studentId") Long studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudent(studentId));
    }

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> all() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/stats")
    public ResponseEntity<PaymentStatsDTO> stats() {
        return ResponseEntity.ok(paymentService.getStats());
    }
}
