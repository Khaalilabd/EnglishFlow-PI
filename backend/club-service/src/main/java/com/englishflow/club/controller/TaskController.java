package com.englishflow.club.controller;

import com.englishflow.club.dto.TaskDTO;
import com.englishflow.club.enums.TaskStatus;
import com.englishflow.club.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    
    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<TaskDTO>> getTasksByClubId(
            @PathVariable Integer clubId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.getTasksByClubId(clubId, userId));
    }
    
    @GetMapping("/club/{clubId}/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByClubIdAndStatus(
            @PathVariable Integer clubId,
            @PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.getTasksByClubIdAndStatus(clubId, status));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Integer id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }
    
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @Valid @RequestBody TaskDTO taskDTO,
            @RequestParam Long userId) {
        TaskDTO createdTask = taskService.createTask(taskDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Integer id,
            @RequestBody TaskDTO taskDTO,
            @RequestParam Long userId) {
        TaskDTO updatedTask = taskService.updateTask(id, taskDTO, userId);
        return ResponseEntity.ok(updatedTask);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Integer id,
            @RequestParam Long userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/club/{clubId}/count/{status}")
    public ResponseEntity<Long> countTasksByStatus(
            @PathVariable Integer clubId,
            @PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.countTasksByStatus(clubId, status));
    }
}
