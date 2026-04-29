package com.englishflow.messaging.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPresenceServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @InjectMocks
    private UserPresenceService userPresenceService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void markUserOnline_ShouldSetPresenceInRedis() {
        // Arrange
        Long userId = 1L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        userPresenceService.markUserOnline(userId);

        // Assert
        verify(setOperations).add(eq("messaging:online_users"), eq("1"));
        verify(valueOperations).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void markUserOffline_ShouldRemovePresenceFromRedis() {
        // Arrange
        Long userId = 1L;
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Act
        userPresenceService.markUserOffline(userId);

        // Assert
        verify(setOperations).remove("messaging:online_users", "1");
        verify(valueOperations).set(anyString(), anyString(), any());
    }

    @Test
    void isUserOnline_WhenUserIsOnline_ShouldReturnTrue() {
        // Arrange
        Long userId = 1L;
        when(setOperations.isMember("messaging:online_users", "1")).thenReturn(true);

        // Act
        boolean result = userPresenceService.isUserOnline(userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void isUserOnline_WhenUserIsOffline_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        when(setOperations.isMember("messaging:online_users", "1")).thenReturn(false);

        // Act
        boolean result = userPresenceService.isUserOnline(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void isUserOnline_WhenRedisThrowsException_ShouldReturnFalse() {
        // Arrange
        Long userId = 1L;
        when(setOperations.isMember(anyString(), anyString())).thenThrow(new RuntimeException("Redis error"));

        // Act
        boolean result = userPresenceService.isUserOnline(userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getOnlineUsers_ShouldReturnSetOfUserIds() {
        // Arrange
        Set<String> onlineUsers = new HashSet<>();
        onlineUsers.add("1");
        onlineUsers.add("2");
        onlineUsers.add("3");
        when(setOperations.members("messaging:online_users")).thenReturn(onlineUsers);

        // Act
        Set<Long> result = userPresenceService.getOnlineUsers();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void getOnlineUsers_WhenNoUsersOnline_ShouldReturnEmptySet() {
        // Arrange
        when(setOperations.members("messaging:online_users")).thenReturn(null);

        // Act
        Set<Long> result = userPresenceService.getOnlineUsers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void refreshUserPresence_ShouldCallMarkUserOnline() {
        // Arrange
        Long userId = 1L;

        // Act
        userPresenceService.refreshUserPresence(userId);

        // Assert
        verify(setOperations).add(eq("messaging:online_users"), eq("1"));
    }

    @Test
    void filterOnlineUsers_ShouldReturnOnlyOnlineUsers() {
        // Arrange
        Set<Long> userIds = Set.of(1L, 2L, 3L);
        when(setOperations.isMember("messaging:online_users", "1")).thenReturn(true);
        when(setOperations.isMember("messaging:online_users", "2")).thenReturn(false);
        when(setOperations.isMember("messaging:online_users", "3")).thenReturn(true);

        // Act
        Set<Long> result = userPresenceService.filterOnlineUsers(userIds);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(3L));
        assertFalse(result.contains(2L));
    }
}
