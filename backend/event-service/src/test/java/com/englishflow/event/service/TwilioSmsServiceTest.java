package com.englishflow.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TwilioSmsServiceTest {

    @InjectMocks
    private TwilioSmsService twilioSmsService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", "");
        ReflectionTestUtils.setField(twilioSmsService, "authToken", "");
        ReflectionTestUtils.setField(twilioSmsService, "fromNumber", "+1234567890");
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
    }

    @Test
    void init_ShouldSetEnabledToFalse_WhenCredentialsAreEmpty() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", "");
        ReflectionTestUtils.setField(twilioSmsService, "authToken", "");

        // Act
        twilioSmsService.init();

        // Assert
        Boolean enabled = (Boolean) ReflectionTestUtils.getField(twilioSmsService, "enabled");
        assertFalse(enabled);
    }

    @Test
    void init_ShouldSetEnabledToFalse_WhenAccountSidIsNull() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", null);
        ReflectionTestUtils.setField(twilioSmsService, "authToken", "test-token");

        // Act
        twilioSmsService.init();

        // Assert
        Boolean enabled = (Boolean) ReflectionTestUtils.getField(twilioSmsService, "enabled");
        assertFalse(enabled);
    }

    @Test
    void init_ShouldSetEnabledToFalse_WhenAuthTokenIsNull() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", "test-sid");
        ReflectionTestUtils.setField(twilioSmsService, "authToken", null);

        // Act
        twilioSmsService.init();

        // Assert
        Boolean enabled = (Boolean) ReflectionTestUtils.getField(twilioSmsService, "enabled");
        assertFalse(enabled);
    }

    @Test
    void init_ShouldSetEnabledToFalse_WhenAccountSidIsBlank() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", "   ");
        ReflectionTestUtils.setField(twilioSmsService, "authToken", "test-token");

        // Act
        twilioSmsService.init();

        // Assert
        Boolean enabled = (Boolean) ReflectionTestUtils.getField(twilioSmsService, "enabled");
        assertFalse(enabled);
    }

    @Test
    void init_ShouldSetEnabledToFalse_WhenAuthTokenIsBlank() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "accountSid", "test-sid");
        ReflectionTestUtils.setField(twilioSmsService, "authToken", "   ");

        // Act
        twilioSmsService.init();

        // Assert
        Boolean enabled = (Boolean) ReflectionTestUtils.getField(twilioSmsService, "enabled");
        assertFalse(enabled);
    }

    @Test
    void sendSms_ShouldNotSendSms_WhenTwilioIsNotEnabled() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
        String to = "+21612345678";
        String body = "Test message";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> twilioSmsService.sendSms(to, body));
    }

    @Test
    void sendSms_ShouldNotSendSms_WhenPhoneNumberIsNull() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
        String to = null;
        String body = "Test message";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> twilioSmsService.sendSms(to, body));
    }

    @Test
    void sendSms_ShouldNotSendSms_WhenPhoneNumberIsBlank() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
        String to = "   ";
        String body = "Test message";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> twilioSmsService.sendSms(to, body));
    }

    @Test
    void sendSms_ShouldNotSendSms_WhenPhoneNumberIsEmpty() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
        String to = "";
        String body = "Test message";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> twilioSmsService.sendSms(to, body));
    }

    @Test
    void sendSms_ShouldHandleValidPhoneNumber_WhenDisabled() {
        // Arrange
        ReflectionTestUtils.setField(twilioSmsService, "enabled", false);
        String to = "+21612345678";
        String body = "Test message";

        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> twilioSmsService.sendSms(to, body));
    }
}
