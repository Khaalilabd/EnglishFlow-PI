package com.englishflow.messaging.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPresenceServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private UserPresenceService presenceService;

    private Long userId = 1L;
    private String onlineUsersKey = "online_users";
    private String userPresenceKey = "user_presence:1";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
    }

    @Test
    void markUserOnline_ShouldAddUserToOnlineSet() {
        // When
        presenceService.markUserOnline(userId);

        // Then
        verify(setOperations).add(eq(onlineUsersKey), eq(userId));
        verify(redisTemplate).expire(eq(userPresenceKey), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void markUserOffline_ShouldRemoveUserFromOnlineSet() {
        // When
        presenceService.markUserOffline(userId);

        // Then
        verify(setOperations).remove(eq(onlineUsersKey), eq(userId));
        verify(redisTemplate).delete(eq(userPresenceKey));
    }

    @Test
    void isUserOnline_WhenOnline_ShouldReturnTrue() {
        // Given
        when(setOperations.isMember(onlineUsersKey, userId)).thenReturn(true);

        // When
        boolean result = presenceService.isUserOnline(userId);

        // Then
        assertTrue(result);
        verify(setOperations).isMember(onlineUsersKey, userId);
    }

    @Test
    void isUserOnline_WhenOffline_ShouldReturnFalse() {
        // Given
        when(setOperations.isMember(onlineUsersKey, userId)).thenReturn(false);

        // When
        boolean result = presenceService.isUserOnline(userId);

        // Then
        assertFalse(result);
    }

    @Test
    void getOnlineUsers_ShouldReturnAllOnlineUsers() {
        // Given
        Set<Object> onlineUsers = Set.of(1L, 2L, 3L);
        when(setOperations.members(onlineUsersKey)).thenReturn(onlineUsers);

        // When
        Set<Long> result = presenceService.getOnlineUsers();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void updateHeartbeat_ShouldRefreshPresence() {
        // When
        presenceService.markUserOnline(userId);

        // Then
        verify(redisTemplate).expire(eq(userPresenceKey), eq(5L), eq(TimeUnit.MINUTES));
    }
}
