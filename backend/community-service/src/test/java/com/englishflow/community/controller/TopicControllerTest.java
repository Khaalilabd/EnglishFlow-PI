package com.englishflow.community.controller;

import com.englishflow.community.dto.CreateTopicRequest;
import com.englishflow.community.dto.TopicDTO;
import com.englishflow.community.service.PermissionService;
import com.englishflow.community.service.TopicService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

    @Mock
    private TopicService topicService;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private TopicController topicController;

    private CreateTopicRequest createRequest;
    private TopicDTO topicDTO;

    @BeforeEach
    void setUp() {
        createRequest = new CreateTopicRequest();
        createRequest.setTitle("Test Topic");
        createRequest.setContent("Test Content");
        createRequest.setUserId(100L);
        createRequest.setUserName("Test User");
        createRequest.setSubCategoryId(10L);

        topicDTO = new TopicDTO();
        topicDTO.setId(1L);
        topicDTO.setTitle("Test Topic");
        topicDTO.setContent("Test Content");
        topicDTO.setUserId(100L);
    }

    @Test
    void getTopicsBySubCategory_Success_ReturnsPagedTopics() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<TopicDTO> topicPage = new PageImpl<>(Arrays.asList(topicDTO));
        when(topicService.getTopicsBySubCategory(eq(10L), eq("recent"), any(Pageable.class)))
                .thenReturn(topicPage);

        // Act
        ResponseEntity<Page<TopicDTO>> response = topicController.getTopicsBySubCategory(10L, 0, 20, "recent");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        verify(topicService, times(1)).getTopicsBySubCategory(eq(10L), eq("recent"), any(Pageable.class));
    }

    @Test
    void getTopicById_Success_ReturnsTopic() {
        // Arrange
        when(topicService.getTopicById(1L, 100L)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.getTopicById(1L, 100L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(topicService, times(1)).getTopicById(1L, 100L);
    }

    @Test
    void updateTopic_Success_ReturnsUpdatedTopic() {
        // Arrange
        when(topicService.updateTopic(1L, 100L, createRequest)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.updateTopic(1L, 100L, createRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(topicService, times(1)).updateTopic(1L, 100L, createRequest);
    }

    @Test
    void deleteTopic_Success_ReturnsNoContent() {
        // Arrange
        doNothing().when(topicService).deleteTopic(1L, 100L);

        // Act
        ResponseEntity<Void> response = topicController.deleteTopic(1L, 100L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(topicService, times(1)).deleteTopic(1L, 100L);
    }

    @Test
    void pinTopic_Success_ReturnsPinnedTopic() {
        // Arrange
        topicDTO.setIsPinned(true);
        when(topicService.pinTopic(1L)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.pinTopic(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getIsPinned());
        verify(topicService, times(1)).pinTopic(1L);
    }

    @Test
    void unpinTopic_Success_ReturnsUnpinnedTopic() {
        // Arrange
        topicDTO.setIsPinned(false);
        when(topicService.unpinTopic(1L)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.unpinTopic(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getIsPinned());
        verify(topicService, times(1)).unpinTopic(1L);
    }

    @Test
    void lockTopic_Success_ReturnsLockedTopic() {
        // Arrange
        topicDTO.setIsLocked(true);
        when(topicService.lockTopic(1L)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.lockTopic(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getIsLocked());
        verify(topicService, times(1)).lockTopic(1L);
    }

    @Test
    void unlockTopic_Success_ReturnsUnlockedTopic() {
        // Arrange
        topicDTO.setIsLocked(false);
        when(topicService.unlockTopic(1L)).thenReturn(topicDTO);

        // Act
        ResponseEntity<TopicDTO> response = topicController.unlockTopic(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getIsLocked());
        verify(topicService, times(1)).unlockTopic(1L);
    }
}
