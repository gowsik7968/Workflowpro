
package com.workflowpro.backend.task.controller;

import com.workflowpro.backend.task.dto.TaskRequestDTO;
import com.workflowpro.backend.task.dto.TaskResponseDTO;
import com.workflowpro.backend.task.service.TaskService;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final UserReopository userRepository;

    public TaskController(
            TaskService taskService,
            UserReopository userRepository) {

        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    // GET: Get all tasks
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {

        List<TaskResponseDTO> tasks =
                taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    // GET: Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id) {

        TaskResponseDTO task =
                taskService.getTaskById(id);

        return ResponseEntity.ok(task);
    }

    // POST: Create a new task
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request,
            Principal principal) {

        // Get authenticated user's email from JWT security context
        String email = principal.getName();

        // Find logged-in user from database
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Authenticated user not found"
                        ));

        // Creator ID comes from authenticated user
        TaskResponseDTO createdTask =
                taskService.createTask(
                        request,
                        currentUser.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdTask);
    }

    // PUT: Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        TaskResponseDTO updatedTask =
                taskService.updateTask(id, request);

        return ResponseEntity.ok(updatedTask);
    }

    // DELETE: Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}