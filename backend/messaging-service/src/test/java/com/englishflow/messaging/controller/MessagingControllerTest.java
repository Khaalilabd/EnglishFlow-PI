package com.englishflow.messaging.controller;

import com.englishflow.messaging.dto.*;
import com.englishflow.messaging.service.MessagingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessagingController.class)
class MessagingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessagingService messagingService;

    private ConversationDTO conversationDTO;
    private MessageDTO messageDTO;

    @BeforeEach
    void setUp() {
        conversationDTO = new ConversationDTO();
        conversationDTO.setId(1L);
        conversationDTO.setType("DIRECT");
        conversationDTO.setCreatedAt(LocalDateTime.now());

        messageDTO = new MessageDTO();
        messageDTO.setId(1L);
        messageDTO.setConversationId(1L);
        messageDTO.setSenderId(1L);
        messageDTO.setContent("Test message");
        messageDTO.setMessageType("TEXT");
        messageDTO.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "1")
    void getUserConversations_ShouldReturnConversations() throws Exception {
        // Given
        List<ConversationDTO> conversations = Arrays.asList(conversationDTO);
        when(messagingService.getUserConversations(1L)).thenReturn(conversations);

        // When & Then
        mockMvc.perform(get("/api/messaging/conversations")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("DIRECT"));
    }

    @Test
    @WithMockUser(username = "1")
    void getConversationById_ShouldReturnConversation() throws Exception {
        // Given
        when(messagingService.getConversationById(1L, 1L)).thenReturn(conversationDTO);

        // When & Then
        mockMvc.perform(get("/api/messaging/conversations/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("DIRECT"));
    }

    @Test
    @WithMockUser(username = "1")
    void createConversation_ShouldReturnCreatedConversation() throws Exception {
        // Given
        CreateConversationRequest request = new CreateConversationRequest();
        request.setType("DIRECT");
        request.setParticipantIds(Arrays.asList(1L, 2L));
        request.setParticipantNames(Arrays.asList("User1", "User2"));

        when(messagingService.createConversation(any(), eq(1L), anyString()))
            .thenReturn(conversationDTO);

        // When & Then
        mockMvc.perform(post("/api/messaging/conversations")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "1")
    void getMessages_ShouldReturnPagedMessages() throws Exception {
        // Given
        Page<MessageDTO> messagePage = new PageImpl<>(Arrays.asList(messageDTO), PageRequest.of(0, 20), 1);
        when(messagingService.getMessages(eq(1L), eq(1L), any())).thenReturn(messagePage);

        // When & Then
        mockMvc.perform(get("/api/messaging/conversations/1/messages")
                .param("page", "0")
                .param("size", "20")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].content").value("Test message"));
    }

    @Test
    @WithMockUser(username = "1")
    void sendMessage_ShouldReturnCreatedMessage() throws Exception {
        // Given
        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");
        request.setMessageType("TEXT");

        when(messagingService.sendMessage(eq(1L), any(), eq(1L), anyString()))
            .thenReturn(messageDTO);

        // When & Then
        mockMvc.perform(post("/api/messaging/conversations/1/messages")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value("Test message"));
    }

    @Test
    @WithMockUser(username = "1")
    void markAsRead_ShouldReturnSuccess() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/messaging/conversations/1/mark-read")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1")
    void getUnreadCount_ShouldReturnCount() throws Exception {
        // Given
        when(messagingService.getUnreadCount(1L)).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/messaging/unread-count")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(5));
    }
}
