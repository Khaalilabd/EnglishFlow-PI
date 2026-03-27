package com.englishflow.auth.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for tracking custom business metrics
 */
@Service
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;
    
    // Authentication Metrics
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;
    private final Counter registrationCounter;
    private final Counter activationCounter;
    private final Counter passwordResetCounter;
    
    // OAuth2 Metrics
    private final Counter oauth2LoginCounter;
    
    // Invitation Metrics
    private final Counter invitationSentCounter;
    private final Counter invitationAcceptedCounter;
    
    // Session Metrics
    private final Counter sessionCreatedCounter;
    private final Counter sessionTerminatedCounter;
    private final Counter suspiciousSessionCounter;
    
    // Security Metrics
    private final Counter rateLimitExceededCounter;
    private final Counter invalidTokenCounter;
    
    // Email Metrics
    private final Counter emailSentCounter;
    private final Counter emailFailedCounter;
    
    // Timers
    private final Timer loginTimer;
    private final Timer registrationTimer;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Number of successful login attempts")
                .register(meterRegistry);
                
        this.loginFailureCounter = Counter.builder("auth.login.failure")
                .description("Number of failed login attempts")
                .register(meterRegistry);
                
        this.registrationCounter = Counter.builder("auth.registration")
                .description("Number of user registrations")
                .register(meterRegistry);
                
        this.activationCounter = Counter.builder("auth.activation")
                .description("Number of account activations")
                .register(meterRegistry);
                
        this.passwordResetCounter = Counter.builder("auth.password.reset")
                .description("Number of password resets")
                .register(meterRegistry);
                
        this.oauth2LoginCounter = Counter.builder("auth.oauth2.login")
                .description("Number of OAuth2 logins")
                .register(meterRegistry);
                
        this.invitationSentCounter = Counter.builder("auth.invitation.sent")
                .description("Number of invitations sent")
                .register(meterRegistry);
                
        this.invitationAcceptedCounter = Counter.builder("auth.invitation.accepted")
                .description("Number of invitations accepted")
                .register(meterRegistry);
                
        this.sessionCreatedCounter = Counter.builder("auth.session.created")
                .description("Number of sessions created")
                .register(meterRegistry);
                
        this.sessionTerminatedCounter = Counter.builder("auth.session.terminated")
                .description("Number of sessions terminated")
                .register(meterRegistry);
                
        this.suspiciousSessionCounter = Counter.builder("auth.session.suspicious")
                .description("Number of suspicious sessions detected")
                .register(meterRegistry);
                
        this.rateLimitExceededCounter = Counter.builder("auth.ratelimit.exceeded")
                .description("Number of rate limit violations")
                .register(meterRegistry);
                
        this.invalidTokenCounter = Counter.builder("auth.token.invalid")
                .description("Number of invalid token attempts")
                .register(meterRegistry);
                
        this.emailSentCounter = Counter.builder("auth.email.sent")
                .description("Number of emails sent successfully")
                .register(meterRegistry);
                
        this.emailFailedCounter = Counter.builder("auth.email.failed")
                .description("Number of failed email attempts")
                .register(meterRegistry);
        
        // Initialize timers
        this.loginTimer = Timer.builder("auth.login.duration")
                .description("Time taken for login operations")
                .register(meterRegistry);
                
        this.registrationTimer = Timer.builder("auth.registration.duration")
                .description("Time taken for registration operations")
                .register(meterRegistry);
    }

    // Authentication Metrics
    public void recordLoginSuccess() {
        loginSuccessCounter.increment();
        log.debug("Login success metric recorded");
    }

    public void recordLoginFailure() {
        loginFailureCounter.increment();
        log.debug("Login failure metric recorded");
    }

    public void recordRegistration() {
        registrationCounter.increment();
        log.debug("Registration metric recorded");
    }

    public void recordActivation() {
        activationCounter.increment();
        log.debug("Activation metric recorded");
    }

    public void recordPasswordReset() {
        passwordResetCounter.increment();
        log.debug("Password reset metric recorded");
    }

    public void recordOAuth2Login() {
        oauth2LoginCounter.increment();
        log.debug("OAuth2 login metric recorded");
    }

    // Invitation Metrics
    public void recordInvitationSent() {
        invitationSentCounter.increment();
        log.debug("Invitation sent metric recorded");
    }

    public void recordInvitationAccepted() {
        invitationAcceptedCounter.increment();
        log.debug("Invitation accepted metric recorded");
    }

    // Session Metrics
    public void recordSessionCreated() {
        sessionCreatedCounter.increment();
        log.debug("Session created metric recorded");
    }

    public void recordSessionTerminated() {
        sessionTerminatedCounter.increment();
        log.debug("Session terminated metric recorded");
    }

    public void recordSuspiciousSession() {
        suspiciousSessionCounter.increment();
        log.warn("Suspicious session detected and recorded");
    }

    // Security Metrics
    public void recordRateLimitExceeded() {
        rateLimitExceededCounter.increment();
        log.warn("Rate limit exceeded metric recorded");
    }

    public void recordInvalidToken() {
        invalidTokenCounter.increment();
        log.debug("Invalid token metric recorded");
    }

    // Email Metrics
    public void recordEmailSent() {
        emailSentCounter.increment();
        log.debug("Email sent metric recorded");
    }

    public void recordEmailFailed() {
        emailFailedCounter.increment();
        log.warn("Email failed metric recorded");
    }

    // Timer Methods
    public void recordLoginDuration(long durationMs) {
        loginTimer.record(durationMs, TimeUnit.MILLISECONDS);
        log.debug("Login duration recorded: {}ms", durationMs);
    }

    public void recordRegistrationDuration(long durationMs) {
        registrationTimer.record(durationMs, TimeUnit.MILLISECONDS);
        log.debug("Registration duration recorded: {}ms", durationMs);
    }

    // Gauge for active sessions (to be called periodically)
    public void updateActiveSessionsGauge(int count) {
        meterRegistry.gauge("auth.sessions.active", count);
        log.debug("Active sessions gauge updated: {}", count);
    }

    // Gauge for total users (to be called periodically)
    public void updateTotalUsersGauge(long count) {
        meterRegistry.gauge("auth.users.total", count);
        log.debug("Total users gauge updated: {}", count);
    }
}
