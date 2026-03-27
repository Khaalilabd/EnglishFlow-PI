package com.englishflow.club.repository;

import com.englishflow.club.entity.Task;
import com.englishflow.club.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    
    List<Task> findByClubId(Integer clubId);
    
    List<Task> findByClubIdAndStatus(Integer clubId, TaskStatus status);
    
    long countByClubIdAndStatus(Integer clubId, TaskStatus status);
}
