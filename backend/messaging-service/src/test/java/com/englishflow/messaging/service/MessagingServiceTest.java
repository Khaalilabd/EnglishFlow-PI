package com.englishflow.messaging.service;

import com.englishflow.messaging.dto.*;
import com.englishflow.messaging.exception.ConversationNotFoundException;
import com.englishflow.messaging.exception.UnauthorizedAccessException;
import com.englishflow.messaging.mapper.ConversationMapper;
import com.englishflow.messaging.mapper.MessageMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessagingServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationParticipantRepository participantRepository;

    @Mock
    private MessageReadStatusRepository readStatusRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessagingService messagingService;

    private Conversation conversation;
    private Message message;
    private ConversationParticipant participant;
    private Long userId = 1L;
    private Long conversationId = 100L;

    @BeforeEach
    void setUp() {
        conversation = new Conversation();
        conversation.setId(conversationId);
        conversation.setType("DIRECT");
        conversation.setCreatedAt(LocalDateTime.now());

        participant = new ConversationParticipant();
        participant.setId(1L);
        participant.setConversation(conversation);
        participant.setUserId(userId);
        participant.setUserName("Test User");
        participant.setActive(true);

        message = new Message();
        message.setId(1L);
        message.setConversation(conversation);
        message.setSenderId(userId);
        message.setContent("Test message");
        message.setMessageType("TEXT");
        message.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void getUserConversations_ShouldReturnConversations() {
        // Given
        List<Conversation> conversations = Arrays.asList(conversation);
        when(conversationRepository.findByUserId(userId)).thenReturn(conversations);
        when(conversationMapper.toDTO(any(Conversation.class))).thenReturn(new ConversationDTO());

        // When
        List<ConversationDTO> result = messagingService.getUserConversations(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(conversationRepository).findByUserId(userId);
        verify(conversationMapper).toDTO(conversation);
    }

    @Test
    void getConversationById_WhenExists_ShouldReturnConversation() {
        // Given
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(conversationMapper.toDTO(conversation)).thenReturn(new ConversationDTO());

        // When
        ConversationDTO result = messagingService.getConversationById(conversationId, userId);

        // Then
        assertNotNull(result);
        verify(conversationRepository).findById(conversationId);
        verify(participantRepository).existsByConversationIdAndUserId(conversationId, userId);
    }

    @Test
    void getConversationById_WhenNotParticipant_ShouldThrowException() {
        // Given
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(false);

        // When & Then
        assertThrows(UnauthorizedAccessException.class, 
            () -> messagingService.getConversationById(conversationId, userId));
    }

    @Test
    void getConversationById_WhenNotExists_ShouldThrowException() {
        // Given
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ConversationNotFoundException.class, 
            () -> messagingService.getConversationById(conversationId, userId));
    }

    @Test
    void createDirectConversation_ShouldCreateConversation() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest();
        request.setType("DIRECT");
        request.setParticipantIds(Arrays.asList(userId, 2L));
        request.setParticipantNames(Arrays.asList("User1", "User2"));

        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class))).thenReturn(new ConversationDTO());

        // When
        ConversationDTO result = messagingService.createConversation(request, userId, "User1");

        // Then
        assertNotNull(result);
        verify(conversationRepository).save(any(Conversation.class));
        verify(participantRepository, times(2)).save(any(ConversationParticipant.class));
    }

    @Test
    void createGroupConversation_ShouldCreateConversation() {
        // Given
        CreateConversationRequest request = new CreateConversationRequest();
        request.setType("GROUP");
        request.setGroupName("Test Group");
        request.setParticipantIds(Arrays.asList(userId, 2L, 3L));
        request.setParticipantNames(Arrays.asList("User1", "User2", "User3"));

        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class))).thenReturn(new ConversationDTO());

        // When
        ConversationDTO result = messagingService.createConversation(request, userId, "User1");

        // Then
        assertNotNull(result);
        verify(conversationRepository).save(any(Conversation.class));
        verify(participantRepository, times(3)).save(any(ConversationParticipant.class));
    }

    @Test
    void getMessages_ShouldReturnPagedMessages() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Message> messagePage = new PageImpl<>(Arrays.asList(message));
        
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable))
            .thenReturn(messagePage);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(new MessageDTO());

        // When
        Page<MessageDTO> result = messagingService.getMessages(conversationId, userId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(messageRepository).findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    @Test
    void sendMessage_ShouldCreateAndReturnMessage() {
        // Given
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");
        request.setMessageType("TEXT");

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(messageMapper.toDTO(any(Message.class))).thenReturn(new MessageDTO());

        // When
        MessageDTO result = messagingService.sendMessage(conversationId, request, userId, "Test User");

        // Then
        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
        verify(conversationRepository).save(conversation);
    }

    @Test
    void markAsRead_ShouldUpdateReadStatus() {
        // Given
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(messageRepository.findUnreadMessages(conversationId, userId)).thenReturn(Arrays.asList(message));

        // When
        messagingService.markAsRead(conversationId, userId);

        // Then
        verify(readStatusRepository, times(1)).save(any(MessageReadStatus.class));
    }

    @Test
    void getUnreadCount_ShouldReturnCount() {
        // Given
        when(messageRepository.countUnreadMessages(userId)).thenReturn(5L);

        // When
        Long result = messagingService.getUnreadCount(userId);

        // Then
        assertEquals(5L, result);
        verify(messageRepository).countUnreadMessages(userId);
    }

    @Test
    void addParticipants_ShouldAddNewParticipants() {
        // Given
        conversation.setType("GROUP");
        AddParticipantsRequest request = new AddParticipantsRequest();
        request.setParticipantIds(Arrays.asList(3L, 4L));
        request.setParticipantNames(Arrays.asList("User3", "User4"));

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.existsByConversationIdAndUserId(conversationId, userId)).thenReturn(true);
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.of(participant));
        participant.setRole("ADMIN");

        // When
        messagingService.addParticipants(conversationId, request, userId);

        // Then
        verify(participantRepository, times(2)).save(any(ConversationParticipant.class));
    }

    @Test
    void removeParticipant_ShouldRemoveParticipant() {
        // Given
        conversation.setType("GROUP");
        Long targetUserId = 2L;
        ConversationParticipant adminParticipant = new ConversationParticipant();
        adminParticipant.setRole("ADMIN");
        adminParticipant.setUserId(userId);

        ConversationParticipant targetParticipant = new ConversationParticipant();
        targetParticipant.setUserId(targetUserId);
        targetParticipant.setRole("MEMBER");

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.of(adminParticipant));
        when(participantRepository.findByConversationIdAndUserId(conversationId, targetUserId))
            .thenReturn(Optional.of(targetParticipant));

        // When
        messagingService.removeParticipant(conversationId, targetUserId, userId);

        // Then
        verify(participantRepository).delete(targetParticipant);
    }

    @Test
    void leaveGroup_ShouldRemoveUserFromGroup() {
        // Given
        conversation.setType("GROUP");
        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.of(participant));

        // When
        messagingService.leaveGroup(conversationId, userId);

        // Then
        verify(participantRepository).delete(participant);
    }

    @Test
    void updateGroup_ShouldUpdateGroupDetails() {
        // Given
        conversation.setType("GROUP");
        UpdateGroupRequest request = new UpdateGroupRequest();
        request.setGroupName("Updated Group");
        request.setGroupDescription("New description");

        ConversationParticipant adminParticipant = new ConversationParticipant();
        adminParticipant.setRole("ADMIN");

        when(conversationRepository.findById(conversationId)).thenReturn(Optional.of(conversation));
        when(participantRepository.findByConversationIdAndUserId(conversationId, userId))
            .thenReturn(Optional.of(adminParticipant));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class))).thenReturn(new ConversationDTO());

        // When
        ConversationDTO result = messagingService.updateGroup(conversationId, request, userId);

        // Then
        assertNotNull(result);
        verify(conversationRepository).save(conversation);
    }
}
