package com.englishflow.messaging.controller;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.ConversationDTO;
import com.englishflow.messaging.dto.MessageDTO;
import com.englishflow.messaging.dto.ParticipantDTO;
import com.englishflow.messaging.dto.SendMessageRequest;
import com.englishflow.messaging.exception.MessageValidationException;
import com.englishflow.messaging.exception.UnauthorizedAccessException;
import com.englishflow.messaging.model.Message;
import com.englishflow.messaging.service.MessagingService;
import com.englishflow.messaging.service.UserPresenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private MessagingService messagingService;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private UserPresenceService userPresenceService;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @Mock
    private Principal principal;

    @InjectMocks
    private WebSocketController webSocketController;

    private MessageDTO messageDTO;
    private ConversationDTO conversationDTO;
    private AuthServiceClient.UserInfo userInfo;

    @BeforeEach
    void setUp() {
        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setConversationId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setSenderName("Test User");
        messageDTO.setContent("Test message");
        messageDTO.setCreatedAt(LocalDateTime.now());

        conversationDTO = new ConversationDTO();
        conversationDTO.setId(1L);
        conversationDTO.setParticipants(new ArrayList<>());

        ParticipantDTO participant1 = new ParticipantDTO();
        participant1.setUserId(1L);
        participant1.setUserName("User 1");

        ParticipantDTO participant2 = new ParticipantDTO();
        participant2.setUserId(2L);
        participant2.setUserName("User 2");

        conversationDTO.setParticipants(Arrays.asList(participant1, participant2));

        userInfo = new AuthServiceClient.UserInfo();
        userInfo.setId(1L);
        userInfo.setFirstName("Test");
        userInfo.setLastName("User");
        userInfo.setEmail("test@example.com");
    }

    @Test
    void sendMessage_ShouldSendMessageSuccessfully() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");
        request.setMessageType(Message.MessageType.TEXT);

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenReturn(userInfo);
        when(messagingService.sendMessage(eq(1L), any(SendMessageRequest.class), eq(1L), anyString(), nullable(String.class)))
                .thenReturn(messageDTO);
        when(messagingService.getConversation(1L, 1L)).thenReturn(conversationDTO);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendMessage(1L, request, headerAccessor, principal);

        // Assert
        verify(messagingService).sendMessage(eq(1L), any(SendMessageRequest.class), eq(1L), anyString(), nullable(String.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1"), (Object) any(MessageDTO.class));
        verify(messagingTemplate, atLeastOnce()).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void sendMessage_WithNoPrincipal_ShouldSendError() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");

        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendMessage(1L, request, headerAccessor, null);

        // Assert
        verify(messagingService, never()).sendMessage(anyLong(), any(), anyLong(), anyString(), anyString());
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1/error"), (Object) any());
    }

    @Test
    void sendMessage_WithUnauthorizedAccess_ShouldSendError() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenReturn(userInfo);
        when(messagingService.sendMessage(eq(1L), any(SendMessageRequest.class), eq(1L), anyString(), nullable(String.class)))
                .thenThrow(new UnauthorizedAccessException("Not authorized"));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendMessage(1L, request, headerAccessor, principal);

        // Assert
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1/error"), (Object) any());
    }

    @Test
    void sendMessage_WithValidationError_ShouldSendError() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("");

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenReturn(userInfo);
        when(messagingService.sendMessage(eq(1L), any(SendMessageRequest.class), eq(1L), anyString(), nullable(String.class)))
                .thenThrow(new MessageValidationException("Content cannot be empty"));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendMessage(1L, request, headerAccessor, principal);

        // Assert
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1/error"), (Object) any());
    }

    @Test
    void sendTypingIndicator_ShouldBroadcastTypingStatus() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("isTyping", true);

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenReturn(userInfo);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendTypingIndicator(1L, payload, principal);

        // Assert
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1/typing"), (Object) any());
    }

    @Test
    void sendTypingIndicator_WithNoPrincipal_ShouldNotBroadcast() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("isTyping", true);

        // Act
        webSocketController.sendTypingIndicator(1L, payload, null);

        // Assert
        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void markUserOnline_ShouldMarkUserAndBroadcast() {
        // Arrange
        when(principal.getName()).thenReturn("1");
        doNothing().when(userPresenceService).markUserOnline(1L);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.markUserOnline(principal);

        // Assert
        verify(userPresenceService).markUserOnline(1L);
        verify(messagingTemplate).convertAndSend(eq("/topic/presence"), (Object) any());
    }

    @Test
    void markUserOnline_WithNoPrincipal_ShouldNotMark() {
        // Arrange & Act
        webSocketController.markUserOnline(null);

        // Assert
        verify(userPresenceService, never()).markUserOnline(anyLong());
        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }

    @Test
    void heartbeat_ShouldRefreshUserPresence() {
        // Arrange
        when(principal.getName()).thenReturn("1");
        doNothing().when(userPresenceService).refreshUserPresence(1L);

        // Act
        webSocketController.heartbeat(principal);

        // Assert
        verify(userPresenceService).refreshUserPresence(1L);
    }

    @Test
    void heartbeat_WithNoPrincipal_ShouldNotRefresh() {
        // Arrange & Act
        webSocketController.heartbeat(null);

        // Assert
        verify(userPresenceService, never()).refreshUserPresence(anyLong());
    }

    @Test
    void sendMessage_WithException_ShouldSendError() {
        // Arrange
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenThrow(new RuntimeException("Service error"));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), (Object) any());

        // Act
        webSocketController.sendMessage(1L, request, headerAccessor, principal);

        // Assert
        verify(messagingTemplate).convertAndSend(eq("/topic/conversation/1/error"), (Object) any());
    }

    @Test
    void sendTypingIndicator_WithException_ShouldNotThrow() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("isTyping", true);

        when(principal.getName()).thenReturn("1");
        when(authServiceClient.getUserInfo(1L)).thenThrow(new RuntimeException("Service error"));

        // Act - should not throw exception
        webSocketController.sendTypingIndicator(1L, payload, principal);

        // Assert
        verify(messagingTemplate, never()).convertAndSend(anyString(), (Object) any());
    }
}
