package com.englishflow.club.service;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Task;
import com.englishflow.club.enums.ClubCategory;
import com.englishflow.club.enums.ClubStatus;
import com.englishflow.club.enums.TaskStatus;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ClubRepository clubRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ClubHistoryService clubHistoryService;

    @InjectMocks
    private TaskService taskService;

    private Club testClub;
    private Task testTask;
    private TaskDTO testTaskDTO;

    @BeforeEach
    void setUp() {
        testClub = Club.builder()
                .id(1)
                .name("Test Club")
                .description("Test Description")
                .category(ClubCategory.CONVERSATION)
                .maxMembers(20)
                .status(ClubStatus.APPROVED)
                .createdBy(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTask = Task.builder()
                .id(1)
                .text("Test Task")
                .status(TaskStatus.TODO)
                .club(testClub)
                .createdBy(100)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testTaskDTO = TaskDTO.builder()
                .id(1)
                .text("Test Task")
                .status(TaskStatus.TODO)
                .clubId(1)
                .createdBy(100)
                .build();
    }

    @Test
    void getTasksByClubId_WhenUserIsMember_ShouldReturnTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(memberService.isMember(1, 100L)).thenReturn(true);
        when(taskRepository.findByClubId(1)).thenReturn(tasks);

        // Act
        List<TaskDTO> result = taskService.getTasksByClubId(1, 100L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getText());
        verify(taskRepository, times(1)).findByClubId(1);
    }

    @Test
    void getTasksByClubId_WhenUserNotMember_ShouldThrowException() {
        // Arrange
        when(memberService.isMember(1, 100L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTasksByClubId(1, 100L));
        verify(taskRepository, never()).findByClubId(1);
    }

    @Test
    void getTasksByClubId_WhenUserIsAcademicManager_ShouldReturnTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByClubId(1)).thenReturn(tasks);

        // Act
        List<TaskDTO> result = taskService.getTasksByClubId(1, 100L, "ACADEMIC_OFFICE_AFFAIR");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(memberService, never()).isMember(anyInt(), anyLong());
    }

    @Test
    void getTasksByClubIdAndStatus_ShouldReturnFilteredTasks() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(taskRepository.findByClubIdAndStatus(1, TaskStatus.TODO)).thenReturn(tasks);

        // Act
        List<TaskDTO> result = taskService.getTasksByClubIdAndStatus(1, TaskStatus.TODO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.TODO, result.get(0).getStatus());
        verify(taskRepository, times(1)).findByClubIdAndStatus(1, TaskStatus.TODO);
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));

        // Act
        TaskDTO result = taskService.getTaskById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getText());
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void getTaskById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.getTaskById(999));
    }

    @Test
    void createTask_WhenUserHasManagementRole_ShouldCreateTask() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        doNothing().when(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), any(), anyString(), anyLong());

        // Act
        TaskDTO result = taskService.createTask(testTaskDTO, 100L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getText());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(clubHistoryService, times(1)).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), any(), anyString(), anyLong());
    }

    @Test
    void createTask_WhenUserNoManagementRole_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(1)).thenReturn(Optional.of(testClub));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(testTaskDTO, 100L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void createTask_WhenClubNotFound_ShouldThrowException() {
        // Arrange
        when(clubRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.createTask(testTaskDTO, 100L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_WhenValid_ShouldUpdateTask() {
        // Arrange
        TaskDTO updateDTO = TaskDTO.builder()
                .text("Updated Task")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(true);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        doNothing().when(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), anyString(), anyString(), anyLong());

        // Act
        TaskDTO result = taskService.updateTask(1, updateDTO, 100L);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(clubHistoryService, times(1)).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), anyString(), anyString(), anyLong());
    }

    @Test
    void updateTask_WhenUserNoManagementRole_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.updateTask(1, testTaskDTO, 100L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_WhenValid_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1);
        doNothing().when(clubHistoryService).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), anyString(), any(), anyLong());

        // Act
        taskService.deleteTask(1, 100L);

        // Assert
        verify(taskRepository, times(1)).deleteById(1);
        verify(clubHistoryService, times(1)).logHistory(anyLong(), anyLong(), any(), anyString(), anyString(), anyString(), any(), anyLong());
    }

    @Test
    void deleteTask_WhenUserNoManagementRole_ShouldThrowException() {
        // Arrange
        when(taskRepository.findById(1)).thenReturn(Optional.of(testTask));
        when(memberService.hasManagementRole(1, 100L)).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(1, 100L));
        verify(taskRepository, never()).deleteById(1);
    }

    @Test
    void countTasksByStatus_ShouldReturnCount() {
        // Arrange
        when(taskRepository.countByClubIdAndStatus(1, TaskStatus.TODO)).thenReturn(5L);

        // Act
        long result = taskService.countTasksByStatus(1, TaskStatus.TODO);

        // Assert
        assertEquals(5L, result);
        verify(taskRepository, times(1)).countByClubIdAndStatus(1, TaskStatus.TODO);
    }
}
