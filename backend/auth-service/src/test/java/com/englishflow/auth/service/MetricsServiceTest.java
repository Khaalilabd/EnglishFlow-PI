package com.englishflow.auth.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MetricsServiceTest {

    private MeterRegistry meterRegistry;
    private MetricsService metricsService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        metricsService = new MetricsService(meterRegistry);
    }

    @Test
    void recordLoginSuccess_ShouldIncrementCounter() {
        // Act
        metricsService.recordLoginSuccess();
        metricsService.recordLoginSuccess();

        // Assert
        Counter counter = meterRegistry.find("auth.login.success").counter();
        assertNotNull(counter);
        assertEquals(2.0, counter.count());
    }

    @Test
    void recordLoginFailure_ShouldIncrementCounter() {
        // Act
        metricsService.recordLoginFailure();

        // Assert
        Counter counter = meterRegistry.find("auth.login.failure").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordRegistration_ShouldIncrementCounter() {
        // Act
        metricsService.recordRegistration();

        // Assert
        Counter counter = meterRegistry.find("auth.registration").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordActivation_ShouldIncrementCounter() {
        // Act
        metricsService.recordActivation();

        // Assert
        Counter counter = meterRegistry.find("auth.activation").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordPasswordReset_ShouldIncrementCounter() {
        // Act
        metricsService.recordPasswordReset();

        // Assert
        Counter counter = meterRegistry.find("auth.password.reset").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordOAuth2Login_ShouldIncrementCounter() {
        // Act
        metricsService.recordOAuth2Login();

        // Assert
        Counter counter = meterRegistry.find("auth.oauth2.login").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordInvitationSent_ShouldIncrementCounter() {
        // Act
        metricsService.recordInvitationSent();

        // Assert
        Counter counter = meterRegistry.find("auth.invitation.sent").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordSessionCreated_ShouldIncrementCounter() {
        // Act
        metricsService.recordSessionCreated();

        // Assert
        Counter counter = meterRegistry.find("auth.session.created").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordRateLimitExceeded_ShouldIncrementCounter() {
        // Act
        metricsService.recordRateLimitExceeded();

        // Assert
        Counter counter = meterRegistry.find("auth.ratelimit.exceeded").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordEmailSent_ShouldIncrementCounter() {
        // Act
        metricsService.recordEmailSent();

        // Assert
        Counter counter = meterRegistry.find("auth.email.sent").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordEmailFailed_ShouldIncrementCounter() {
        // Act
        metricsService.recordEmailFailed();

        // Assert
        Counter counter = meterRegistry.find("auth.email.failed").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void recordLoginDuration_ShouldRecordTimer() {
        // Act
        metricsService.recordLoginDuration(150);

        // Assert
        Timer timer = meterRegistry.find("auth.login.duration").timer();
        assertNotNull(timer);
        assertEquals(1, timer.count());
    }

    @Test
    void updateActiveSessionsGauge_ShouldUpdateGauge() {
        // Act
        metricsService.updateActiveSessionsGauge(42);

        // Assert
        Double gaugeValue = meterRegistry.find("auth.sessions.active").gauge().value();
        assertNotNull(gaugeValue);
        assertEquals(42.0, gaugeValue);
    }

    @Test
    void updateTotalUsersGauge_ShouldUpdateGauge() {
        // Act
        metricsService.updateTotalUsersGauge(1000L);

        // Assert
        Double gaugeValue = meterRegistry.find("auth.users.total").gauge().value();
        assertNotNull(gaugeValue);
        assertEquals(1000.0, gaugeValue);
    }
}
