package com.englishflow.club.service;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.entity.Club;
import com.englishflow.club.entity.Task;
import com.englishflow.club.enums.TaskStatus;
import com.englishflow.club.repository.ClubRepository;
import com.englishflow.club.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ClubRepository clubRepository;
    private final MemberService memberService;
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByClubId(Integer clubId, Long userId) {
        // Check if user is a member of the club
        if (!memberService.isMember(clubId, userId)) {
            throw new RuntimeException("Only club members can view tasks");
        }
        
        return taskRepository.findByClubId(clubId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByClubIdAndStatus(Integer clubId, TaskStatus status) {
        return taskRepository.findByClubIdAndStatus(clubId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Integer id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return convertToDTO(task);
    }
    
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO, Long userId) {
        Club club = clubRepository.findById(taskDTO.getClubId())
                .orElseThrow(() -> new RuntimeException("Club not found with id: " + taskDTO.getClubId()));
        
        // Check if user is the president of the club
        if (!memberService.isPresident(taskDTO.getClubId(), userId)) {
            throw new RuntimeException("Only the president can create tasks");
        }
        
        Task task = Task.builder()
                .text(taskDTO.getText())
                .status(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.TODO)
                .club(club)
                .createdBy(taskDTO.getCreatedBy())
                .build();
        
        Task savedTask = taskRepository.save(task);
        return convertToDTO(savedTask);
    }
    
    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Check if user is the president of the club
        if (!memberService.isPresident(task.getClub().getId(), userId)) {
            throw new RuntimeException("Only the president can update tasks");
        }
        
        // Update only non-null fields (partial update)
        if (taskDTO.getText() != null) {
            task.setText(taskDTO.getText());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToDTO(updatedTask);
    }
    
    @Transactional
    public void deleteTask(Integer id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        
        // Check if user is the president of the club
        if (!memberService.isPresident(task.getClub().getId(), userId)) {
            throw new RuntimeException("Only the president can delete tasks");
        }
        
        taskRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public long countTasksByStatus(Integer clubId, TaskStatus status) {
        return taskRepository.countByClubIdAndStatus(clubId, status);
    }
    
    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .text(task.getText())
                .status(task.getStatus())
                .clubId(task.getClub().getId())
                .createdBy(task.getCreatedBy())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
