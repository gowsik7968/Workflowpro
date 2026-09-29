package com.workflowpro.backend.task.comment.controller;

import com.workflowpro.backend.task.comment.dto.TaskCommentRequestDTO;
import com.workflowpro.backend.task.comment.dto.TaskCommentResponseDTO;
import com.workflowpro.backend.task.comment.service.TaskCommentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentService commentService;

    public TaskCommentController(TaskCommentService commentService) {
        this.commentService = commentService;
    }

    // Get all comments for a task
    @GetMapping
    public ResponseEntity<List<TaskCommentResponseDTO>> getComments(
            @PathVariable Long taskId
    ) {
        return ResponseEntity.ok(
                commentService.getTaskComments(taskId)
        );
    }

    // Add comment to a task
    @PostMapping
    public ResponseEntity<TaskCommentResponseDTO> addComment(
            @PathVariable Long taskId,
            @RequestBody TaskCommentRequestDTO dto,
            Principal principal
    ) {
        return ResponseEntity.status(201).body(
                commentService.addComment(
                        taskId,
                        dto,
                        principal.getName()
                )
        );
    }
}