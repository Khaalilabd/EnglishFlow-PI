package com.englishflow.messaging.service;

import com.englishflow.messaging.client.AuthServiceClient;
import com.englishflow.messaging.dto.*;
import com.englishflow.messaging.exception.ConversationNotFoundException;
import com.englishflow.messaging.exception.MessageValidationException;
import com.englishflow.messaging.exception.UnauthorizedAccessException;
import com.englishflow.messaging.model.*;
import com.englishflow.messaging.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private ConversationParticipantRepository participantRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageReadStatusRepository readStatusRepository;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private MessageReactionService reactionService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private UserPresenceService userPresenceService;

    @InjectMocks
    private MessagingService messagingService;

    private Conversation conversation;
    private ConversationParticipant participant;
    private Message message;

    @BeforeEach
    void setUp() {
        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setType(Conversation.ConversationType.DIRECT);
        conversation.setTitle("Test Conversation");
        conversation.setCreatedBy(1L);
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setParticipants(new ArrayList<>());
        conversation.setMessages(new ArrayList<>());

        participant = new ConversationParticipant();
        participant.setId(1L);
        participant.setConversation(conversation);
        participant.setUserId(1L);
        participant.setUserName("Test User");
        participant.setUserEmail("test@example.com");
        participant.setUserRole("STUDENT");
        participant.setIsActive(true);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.MEMBER);
        participant.setJoinedAt(LocalDateTime.now());

        message = new Message();
        message.setId(1L);
        message.setConversation(conversation);
        message.setSenderId(1L);
        message.setSenderName("Test User");
        message.setContent("Test message");
        message.setMessageType(Message.MessageType.TEXT);
        message.setIsEdited(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setReadStatuses(new ArrayList<>());
        message.setReactions(new ArrayList<>());
    }

    @Test
    void getUserConversations_ShouldReturnConversationList() {
        // Arrange
        Long userId = 1L;
        conversation.getParticipants().add(participant);
        when(conversationRepository.findByUserId(userId)).thenReturn(Arrays.asList(conversation));
        when(userPresenceService.isUserOnline(anyLong())).thenReturn(true);

        // Act
        List<ConversationDTO> result = messagingService.getUserConversations(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(conversationRepository).findByUserId(userId);
    }

    @Test
    void getConversation_WhenUserIsParticipant_ShouldReturnConversation() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 1L;
        conversation.getParticipants().add(participant);
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(conversationRepository.findByIdWithParticipants(conversationId)).thenReturn(Optional.of(conversation));
        when(userPresenceService.isUserOnline(anyLong())).thenReturn(true);

        // Act
        ConversationDTO result = messagingService.getConversation(conversationId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(conversationId, result.getId());
        verify(participantRepository).existsByConversationIdAndUserId(conversationId, userId);
    }

    @Test
    void getConversation_WhenUserIsNotParticipant_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 2L;
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> 
            messagingService.getConversation(conversationId, userId)
        );
    }

    @Test
    void sendMessage_TextMessage_ShouldSendSuccessfully() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");
        request.setMessageType(Message.MessageType.TEXT);
        
        conversation.getParticipants().add(participant);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(reactionService.getReactionSummary(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        MessageDTO result = messagingService.sendMessage(conversationId, request, senderId, 
            "Test User", null);

        // Assert
        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    void sendMessage_EmptyTextMessage_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("");
        request.setMessageType(Message.MessageType.TEXT);

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void markAsRead_ShouldUpdateLastReadAt() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 1L;
        
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.of(participant));
        when(messageRepository.findMessagesByConversationId(conversationId))
            .thenReturn(Arrays.asList(message));

        // Act
        messagingService.markAsRead(conversationId, userId);

        // Assert
        verify(participantRepository).save(any(ConversationParticipant.class));
        assertNotNull(participant.getLastReadAt());
    }

    @Test
    void getUnreadCount_ShouldReturnCount() {
        // Arrange
        Long userId = 1L;
        when(messageRepository.countUnreadMessages(userId)).thenReturn(5L);

        // Act
        Long result = messagingService.getUnreadCount(userId);

        // Assert
        assertEquals(5L, result);
        verify(messageRepository).countUnreadMessages(userId);
    }

    @Test
    void hasAccessToConversation_WhenUserIsParticipant_ShouldReturnTrue() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 1L;
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);

        // Act
        boolean result = messagingService.hasAccessToConversation(conversationId, userId);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAccessToConversation_WhenUserIsNotParticipant_ShouldReturnFalse() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 2L;
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(false);

        // Act
        boolean result = messagingService.hasAccessToConversation(conversationId, userId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getMessages_WhenUserIsParticipant_ShouldReturnMessages() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 1L;
        int page = 0;
        int size = 20;
        
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(messageRepository.findMessagesByConversationId(eq(conversationId), any(Pageable.class)))
            .thenReturn(messagePage);
        when(reactionService.getReactionSummary(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        Page<MessageDTO> result = messagingService.getMessages(conversationId, userId, page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(participantRepository).existsByConversationIdAndUserId(conversationId, userId);
    }

    @Test
    void sendMessage_EmojiMessage_ShouldSendSuccessfully() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("");
        request.setMessageType(Message.MessageType.EMOJI);
        request.setEmojiCode("U+1F600");
        
        conversation.getParticipants().add(participant);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(reactionService.getReactionSummary(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        MessageDTO result = messagingService.sendMessage(conversationId, request, senderId, 
            "Test User", null);

        // Assert
        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_FileMessage_ShouldSendSuccessfully() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("File caption");
        request.setMessageType(Message.MessageType.FILE);
        request.setFileUrl("http://example.com/file.pdf");
        request.setFileName("document.pdf");
        request.setFileSize(1024L);
        
        conversation.getParticipants().add(participant);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(reactionService.getReactionSummary(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        MessageDTO result = messagingService.sendMessage(conversationId, request, senderId, 
            "Test User", null);

        // Assert
        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void sendMessage_VoiceMessage_ShouldSendSuccessfully() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setMessageType(Message.MessageType.VOICE);
        request.setFileUrl("http://example.com/voice.mp3");
        request.setVoiceDuration(30);
        
        conversation.getParticipants().add(participant);
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, senderId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(reactionService.getReactionSummary(anyLong(), any())).thenReturn(new ArrayList<>());

        // Act
        MessageDTO result = messagingService.sendMessage(conversationId, request, senderId, 
            "Test User", null);

        // Assert
        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void createConversation_DirectConversation_ShouldCreateNew() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setType(Conversation.ConversationType.DIRECT);
        request.setParticipantIds(Arrays.asList(2L));
        
        Long currentUserId = 1L;
        AuthServiceClient.UserInfo userInfo = new AuthServiceClient.UserInfo();
        userInfo.setFirstName("Other");
        userInfo.setLastName("User");
        userInfo.setEmail("other@example.com");
        userInfo.setRole("STUDENT");
        
        when(conversationRepository.findDirectConversation(currentUserId, 2L)).thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(authServiceClient.getUserInfo(2L)).thenReturn(userInfo);
        when(participantRepository.save(any(ConversationParticipant.class))).thenReturn(participant);
        when(conversationRepository.findByIdWithParticipants(any())).thenReturn(Optional.of(conversation));

        // Act
        ConversationDTO result = messagingService.createConversation(request, currentUserId, 
            "Current User", "current@example.com", "STUDENT", null);

        // Assert
        assertNotNull(result);
        verify(conversationRepository).save(any(Conversation.class));
        verify(participantRepository, atLeast(2)).save(any(ConversationParticipant.class));
    }

    @Test
    void updateGroup_ShouldUpdateGroupDetails() {
        // Arrange
        Long conversationId = 1L;
        Long currentUserId = 1L;
        UpdateGroupRequest request = new UpdateGroupRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        
        conversation.setType(Conversation.ConversationType.GROUP);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.ADMIN);
        conversation.getParticipants().add(participant);
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, currentUserId))
                .thenReturn(Optional.of(participant));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(userPresenceService.isUserOnline(anyLong())).thenReturn(true);

        // Act
        ConversationDTO result = messagingService.updateGroup(conversationId, request, currentUserId);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", conversation.getTitle());
        assertEquals("Updated Description", conversation.getDescription());
        verify(conversationRepository).save(conversation);
    }

    @Test
    void leaveGroup_ShouldMarkParticipantInactive() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 1L;
        
        conversation.setType(Conversation.ConversationType.GROUP);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.MEMBER);
        
        ConversationParticipant adminParticipant = new ConversationParticipant();
        adminParticipant.setUserId(2L);
        adminParticipant.setParticipantRole(ConversationParticipant.ParticipantRole.ADMIN);
        adminParticipant.setIsActive(true);
        
        conversation.getParticipants().add(participant);
        conversation.getParticipants().add(adminParticipant);
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(participant));

        // Act
        messagingService.leaveGroup(conversationId, userId);

        // Assert
        assertFalse(participant.getIsActive());
        verify(participantRepository).save(participant);
    }

    @Test
    void addParticipantsToGroup_ShouldAddParticipants() {
        // Arrange
        Long conversationId = 1L;
        Long currentUserId = 1L;
        AddParticipantsRequest request = new AddParticipantsRequest();
        request.setParticipantIds(Arrays.asList(2L, 3L));
        
        conversation.setType(Conversation.ConversationType.GROUP);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.ADMIN);
        conversation.getParticipants().add(participant);
        
        AuthServiceClient.UserInfo userInfo = new AuthServiceClient.UserInfo();
        userInfo.setFirstName("New");
        userInfo.setLastName("User");
        userInfo.setEmail("new@example.com");
        userInfo.setRole("STUDENT");
        
        when(conversationRepository.findByIdWithParticipants(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, currentUserId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.existsByConversationIdAndUserId(eq(conversationId), anyLong())).thenReturn(false);
        when(authServiceClient.getUserInfo(anyLong())).thenReturn(userInfo);
        when(participantRepository.save(any(ConversationParticipant.class))).thenReturn(participant);
        when(userPresenceService.isUserOnline(anyLong())).thenReturn(true);

        // Act
        ConversationDTO result = messagingService.addParticipantsToGroup(conversationId, request, currentUserId);

        // Assert
        assertNotNull(result);
        verify(participantRepository, atLeast(2)).save(any(ConversationParticipant.class));
    }

    @Test
    void removeParticipantFromGroup_ShouldRemoveParticipant() {
        // Arrange
        Long conversationId = 1L;
        Long participantId = 2L;
        Long currentUserId = 1L;
        
        conversation.setType(Conversation.ConversationType.GROUP);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.ADMIN);
        
        ConversationParticipant targetParticipant = new ConversationParticipant();
        targetParticipant.setUserId(participantId);
        targetParticipant.setIsActive(true);
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, currentUserId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.findByConversationIdAndUserId(conversationId, participantId))
                .thenReturn(Optional.of(targetParticipant));

        // Act
        messagingService.removeParticipantFromGroup(conversationId, participantId, currentUserId);

        // Assert
        assertFalse(targetParticipant.getIsActive());
        verify(participantRepository).save(targetParticipant);
    }

    @Test
    void promoteToAdmin_ShouldPromoteParticipant() {
        // Arrange
        Long conversationId = 1L;
        Long participantId = 2L;
        Long currentUserId = 1L;
        
        conversation.setType(Conversation.ConversationType.GROUP);
        participant.setParticipantRole(ConversationParticipant.ParticipantRole.ADMIN);
        
        ConversationParticipant targetParticipant = new ConversationParticipant();
        targetParticipant.setUserId(participantId);
        targetParticipant.setParticipantRole(ConversationParticipant.ParticipantRole.MEMBER);
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, currentUserId))
                .thenReturn(Optional.of(participant));
        when(participantRepository.findByConversationIdAndUserId(conversationId, participantId))
                .thenReturn(Optional.of(targetParticipant));

        // Act
        messagingService.promoteToAdmin(conversationId, participantId, currentUserId);

        // Assert
        assertEquals(ConversationParticipant.ParticipantRole.ADMIN, targetParticipant.getParticipantRole());
        verify(participantRepository).save(targetParticipant);
    }

    @Test
    void sendMessage_WithNullContent_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent(null);
        request.setMessageType(Message.MessageType.TEXT);

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void sendMessage_WithTooLongContent_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("a".repeat(6000)); // Plus de 5000 caractères
        request.setMessageType(Message.MessageType.TEXT);

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void sendMessage_EmojiWithoutCode_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setMessageType(Message.MessageType.EMOJI);
        request.setEmojiCode(null);

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void sendMessage_FileWithoutUrl_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setMessageType(Message.MessageType.FILE);
        request.setFileName("document.pdf");

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void sendMessage_VoiceWithoutDuration_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 1L;
        SendMessageRequest request = new SendMessageRequest();
        request.setMessageType(Message.MessageType.VOICE);
        request.setFileUrl("http://example.com/voice.mp3");

        // Act & Assert
        assertThrows(MessageValidationException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void sendMessage_UserNotParticipant_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long senderId = 2L;
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Test message");
        request.setMessageType(Message.MessageType.TEXT);
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, senderId)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> 
            messagingService.sendMessage(conversationId, request, senderId, "Test User", null)
        );
    }

    @Test
    void getConversation_WhenConversationNotFound_ShouldThrowException() {
        // Arrange
        Long conversationId = 999L;
        Long userId = 1L;
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(conversationRepository.findByIdWithParticipants(conversationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ConversationNotFoundException.class, () -> 
            messagingService.getConversation(conversationId, userId)
        );
    }

    @Test
    void markAsRead_UserNotParticipant_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 2L;
        
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> 
            messagingService.markAsRead(conversationId, userId)
        );
    }

    @Test
    void getMessages_WhenUserIsNotParticipant_ShouldThrowException() {
        // Arrange
        Long conversationId = 1L;
        Long userId = 2L;
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(false);

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () -> 
            messagingService.getMessages(conversationId, userId, 0, 20)
        );
    }

    @Test
    void createConversation_DirectConversationExists_ShouldReturnExisting() {
        // Arrange
        CreateConversationRequest request = new CreateConversationRequest();
        request.setType(Conversation.ConversationType.DIRECT);
        request.setParticipantIds(Arrays.asList(2L));
        
        Long currentUserId = 1L;
        conversation.getParticipants().add(participant);
        when(conversationRepository.findDirectConversation(currentUserId, 2L)).thenReturn(Optional.of(conversation));
        when(userPresenceService.isUserOnline(anyLong())).thenReturn(true);

        // Act
        ConversationDTO result = messagingService.createConversation(request, currentUserId, 
            "Current User", "current@example.com", "STUDENT", null);

        // Assert
        assertNotNull(result);
        verify(conversationRepository, never()).save(any(Conversation.class));
    }
}
