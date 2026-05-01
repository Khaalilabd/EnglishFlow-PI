package com.englishflow.sponsors.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ClubServiceClient clubServiceClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(clubServiceClient, "clubServiceUrl", "http://localhost:8085");
    }

    @Test
    void getClubPresidentUserId_WhenPresidentExists_ShouldReturnUserId() {
        // Arrange
        Integer clubId = 1;
        Map<String, Object> president = new HashMap<>();
        president.put("userId", 123);
        president.put("rank", "PRESIDENT");

        Map<String, Object> member = new HashMap<>();
        member.put("userId", 456);
        member.put("rank", "MEMBER");

        List<Map<String, Object>> members = Arrays.asList(president, member);

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(members);

        // Act
        Long result = clubServiceClient.getClubPresidentUserId(clubId);

        // Assert
        assertEquals(123L, result);
        verify(restTemplate).getForObject(eq("http://localhost:8085/members/club/1"), eq(List.class));
    }

    @Test
    void getClubPresidentUserId_WhenPresidentUserIdIsString_ShouldParseAndReturn() {
        // Arrange
        Integer clubId = 1;
        Map<String, Object> president = new HashMap<>();
        president.put("userId", "789");
        president.put("rank", "PRESIDENT");

        List<Map<String, Object>> members = Collections.singletonList(president);

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(members);

        // Act
        Long result = clubServiceClient.getClubPresidentUserId(clubId);

        // Assert
        assertEquals(789L, result);
    }

    @Test
    void getClubPresidentUserId_WhenNoPresidentExists_ShouldReturnNull() {
        // Arrange
        Integer clubId = 1;
        Map<String, Object> member = new HashMap<>();
        member.put("userId", 456);
        member.put("rank", "MEMBER");

        List<Map<String, Object>> members = Collections.singletonList(member);

        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(members);

        // Act
        Long result = clubServiceClient.getClubPresidentUserId(clubId);

        // Assert
        assertNull(result);
    }

    @Test
    void getClubPresidentUserId_WhenMembersListIsNull_ShouldReturnNull() {
        // Arrange
        Integer clubId = 1;
        when(restTemplate.getForObject(anyString(), eq(List.class))).thenReturn(null);

        // Act
        Long result = clubServiceClient.getClubPresidentUserId(clubId);

        // Assert
        assertNull(result);
    }

    @Test
    void getClubPresidentUserId_WhenExceptionOccurs_ShouldReturnNull() {
        // Arrange
        Integer clubId = 1;
        when(restTemplate.getForObject(anyString(), eq(List.class)))
                .thenThrow(new RestClientException("Connection error"));

        // Act
        Long result = clubServiceClient.getClubPresidentUserId(clubId);

        // Assert
        assertNull(result);
    }

    @Test
    void createSponsorshipExpense_ShouldCallRestTemplate() {
        // Arrange
        Integer clubId = 1;
        Double amount = 1000.0;
        String sponsorName = "Test Sponsor";

        when(restTemplate.postForObject(anyString(), any(), eq(Object.class))).thenReturn(new Object());

        // Act
        clubServiceClient.createSponsorshipExpense(clubId, amount, sponsorName);

        // Assert
        verify(restTemplate).postForObject(
                eq("http://localhost:8085/expenses"),
                argThat(body -> {
                    if (body instanceof Map<?, ?> map) {
                        return map.get("clubId").equals(clubId) &&
                               map.get("amount").equals(amount) &&
                               ((String) map.get("designation")).contains(sponsorName);
                    }
                    return false;
                }),
                eq(Object.class)
        );
    }

    @Test
    void createSponsorshipExpense_WhenExceptionOccurs_ShouldHandleGracefully() {
        // Arrange
        Integer clubId = 1;
        Double amount = 1000.0;
        String sponsorName = "Test Sponsor";

        when(restTemplate.postForObject(anyString(), any(), eq(Object.class)))
                .thenThrow(new RestClientException("Connection error"));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> clubServiceClient.createSponsorshipExpense(clubId, amount, sponsorName));
    }
}
