package com.englishflow.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GeoIpServiceTest {

    private GeoIpService geoIpService;

    @BeforeEach
    void setUp() {
        geoIpService = new GeoIpService();
    }

    @Test
    void getLocationInfo_WithValidIp_ShouldReturnLocationInfo() {
        // Arrange
        String ipAddress = "8.8.8.8"; // Google DNS

        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo(ipAddress);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCountry());
    }

    @Test
    void getLocationInfo_WithLocalhostIp_ShouldReturnLocalNetwork() {
        // Arrange
        String ipAddress = "127.0.0.1";

        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo(ipAddress);

        // Assert
        assertNotNull(result);
        assertEquals("Local Network", result.getCountry());
    }

    @Test
    void getLocationInfo_WithNullIp_ShouldReturnLocalNetwork() {
        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo(null);

        // Assert
        assertNotNull(result);
        assertEquals("Local Network", result.getCountry());
    }

    @Test
    void getLocationInfo_WithEmptyIp_ShouldReturnLocalNetwork() {
        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo("");

        // Assert
        assertNotNull(result);
        assertEquals("Local Network", result.getCountry());
    }

    @Test
    void getLocationInfo_WithInvalidIp_ShouldReturnUnknown() {
        // Arrange
        String ipAddress = "invalid-ip";

        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo(ipAddress);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCountry());
    }

    @Test
    void getLocationInfo_WithPrivateIp_ShouldReturnLocalNetwork() {
        // Arrange
        String ipAddress = "192.168.1.1";

        // Act
        GeoIpService.LocationInfo result = geoIpService.getLocationInfo(ipAddress);

        // Assert
        assertNotNull(result);
        assertEquals("Local Network", result.getCountry());
    }
}
