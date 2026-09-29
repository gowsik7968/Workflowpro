package com.workflowpro.backend.task.comment.service;

import com.workflowpro.backend.activity.service.ActivityService;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.task.comment.dto.TaskCommentRequestDTO;
import com.workflowpro.backend.task.comment.dto.TaskCommentResponseDTO;
import com.workflowpro.backend.task.comment.entity.TaskComment;
import com.workflowpro.backend.task.comment.repository.TaskCommentRepository;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final UserReopository userRepository;
    private final ActivityService activityService;

    public TaskCommentService(
            TaskCommentRepository taskCommentRepository,
            TaskRepository taskRepository,
            UserReopository userRepository,
            ActivityService activityService
    ) {
        this.taskCommentRepository = taskCommentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    // =====================================
    // GET TASK COMMENTS
    // =====================================

    @Transactional(readOnly = true)
    public List<TaskCommentResponseDTO> getTaskComments(
            Long taskId
    ) {

        return taskCommentRepository
                .findByTask_IdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =====================================
    // ADD COMMENT
    // =====================================

    public TaskCommentResponseDTO addComment(
            Long taskId,
            TaskCommentRequestDTO dto,
            String userEmail
    ) {

        // ---------------------------------
        // FIND TASK
        // ---------------------------------

        Task task = taskRepository
                .findById(taskId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found with ID: " + taskId
                        )
                );

        // ---------------------------------
        // FIND LOGGED-IN USER
        // ---------------------------------

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        // ---------------------------------
        // CREATE COMMENT
        // ---------------------------------

        TaskComment comment = new TaskComment();

        comment.setTask(task);
        comment.setUser(user);
        comment.setContent(dto.getContent());

        TaskComment savedComment =
                taskCommentRepository.save(comment);

        // =====================================
        // CREATE ACTIVITY LOG
        // =====================================

        activityService.createActivity(
                user,
                "COMMENT",
                user.getFullName()
                        + " added a comment to task \""
                        + task.getTitle()
                        + "\"",
                "TASK",
                task.getId()
        );

        // ---------------------------------
        // RETURN COMMENT DTO
        // ---------------------------------

        return convertToDTO(savedComment);
    }

    // =====================================
    // CONVERT ENTITY TO DTO
    // =====================================

    private TaskCommentResponseDTO convertToDTO(
            TaskComment comment
    ) {

        String authorName = null;

        if (comment.getUser() != null) {

            authorName =
                    comment.getUser().getFullName();
        }

        return new TaskCommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                authorName,
                comment.getCreatedAt()
        );
    }
}