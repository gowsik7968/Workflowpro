package com.workflowpro.backend.task.comment.repository;

import com.workflowpro.backend.task.comment.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository
        extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTask_IdOrderByCreatedAtAsc(Long taskId);

    // Delete all comments belonging to a task
    void deleteByTask_Id(Long taskId);

    // Delete all comments written by a user
    void deleteByUser_Id(Long userId);
}