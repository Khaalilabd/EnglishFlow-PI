package com.englishflow.club.mapper;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Task;
import com.englishflow.club.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskMapperTest {
    
    private TaskMapper taskMapper;
    
    @BeforeEach
    void setUp() {
        taskMapper = Mappers.getMapper(TaskMapper.class);
    }
    
    @Test
    void testToDTO_WithValidTask() {
        // Given
        Club club = Club.builder()
                .id(1)
                .name("Test Club")
                .build();
        
        Task task = Task.builder()
                .id(1)
                .text("Complete project documentation")
                .status(TaskStatus.TODO)
                .club(club)
                .createdBy(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        TaskDTO dto = taskMapper.toDTO(task);
        
        // Then
        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Complete project documentation", dto.getText());
        assertEquals(TaskStatus.TODO, dto.getStatus());
        assertEquals(1, dto.getClubId());
        assertEquals(100, dto.getCreatedBy());
        assertNotNull(dto.getCreatedAt());
        assertNotNull(dto.getUpdatedAt());
    }
    
    @Test
    void testToDTO_WithInProgressTask() {
        // Given
        Club club = Club.builder().id(2).build();
        
        Task task = Task.builder()
                .id(2)
                .text("Review code")
                .status(TaskStatus.IN_PROGRESS)
                .club(club)
                .createdBy(200)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        TaskDTO dto = taskMapper.toDTO(task);
        
        // Then
        assertNotNull(dto);
        assertEquals(TaskStatus.IN_PROGRESS, dto.getStatus());
    }
    
    @Test
    void testToDTO_WithDoneTask() {
        // Given
        Club club = Club.builder().id(3).build();
        
        Task task = Task.builder()
                .id(3)
                .text("Deploy application")
                .status(TaskStatus.DONE)
                .club(club)
                .createdBy(300)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        TaskDTO dto = taskMapper.toDTO(task);
        
        // Then
        assertNotNull(dto);
        assertEquals(TaskStatus.DONE, dto.getStatus());
    }
    
    @Test
    void testToEntity_WithValidDTO() {
        // Given
        TaskDTO dto = TaskDTO.builder()
                .id(1)
                .text("Write unit tests")
                .status(TaskStatus.TODO)
                .clubId(1)
                .createdBy(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        // When
        Task task = taskMapper.toEntity(dto);
        
        // Then
        assertNotNull(task);
        assertNull(task.getId()); // ID is ignored in mapping
        assertEquals("Write unit tests", task.getText());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNull(task.getClub()); // Club is ignored in mapping
        assertEquals(100, task.getCreatedBy());
        assertNull(task.getCreatedAt()); // CreatedAt is ignored in mapping
        assertNull(task.getUpdatedAt()); // UpdatedAt is ignored in mapping
    }
    
    @Test
    void testToEntity_WithMinimalDTO() {
        // Given
        TaskDTO dto = TaskDTO.builder()
                .text("Simple task")
                .status(TaskStatus.TODO)
                .clubId(1)
                .build();
        
        // When
        Task task = taskMapper.toEntity(dto);
        
        // Then
        assertNotNull(task);
        assertEquals("Simple task", task.getText());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertNull(task.getCreatedBy());
    }
}
