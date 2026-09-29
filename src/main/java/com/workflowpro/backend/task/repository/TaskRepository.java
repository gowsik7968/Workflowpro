package com.workflowpro.backend.task.repository;

import com.workflowpro.backend.task.entity.Task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository
        extends JpaRepository<Task, Long> {

    // Tasks belonging to a team
    List<Task> findByTeam_Id(Long teamId);

    // Tasks belonging to a project
    List<Task> findByProject_Id(Long projectId);

    // Tasks created by a user
    List<Task> findByCreatedBy_Id(Long userId);

    // Tasks assigned to a user
    List<Task> findByAssignedTo_Id(Long userId);
}