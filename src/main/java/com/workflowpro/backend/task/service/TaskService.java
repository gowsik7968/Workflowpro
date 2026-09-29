package com.workflowpro.backend.task.service;

import com.workflowpro.backend.activity.service.ActivityService;
import com.workflowpro.backend.notification.repository.NotificationRepository;
import com.workflowpro.backend.notification.service.NotificationService;
import com.workflowpro.backend.project.entity.Project;
import com.workflowpro.backend.project.repository.ProjectRepository;
import com.workflowpro.backend.task.comment.repository.TaskCommentRepository;
import com.workflowpro.backend.task.dto.TaskRequestDTO;
import com.workflowpro.backend.task.dto.TaskResponseDTO;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.team.entity.Team;
import com.workflowpro.backend.team.repository.TeamRepository;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserReopository userRepository;
    private final NotificationService notificationService;
    private final ActivityService activityService;
    private final TaskCommentRepository taskCommentRepository;
    private final NotificationRepository notificationRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            TeamRepository teamRepository,
            UserReopository userRepository,
            NotificationService notificationService,
            ActivityService activityService,
            TaskCommentRepository taskCommentRepository,
            NotificationRepository notificationRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.activityService = activityService;
        this.taskCommentRepository = taskCommentRepository;
        this.notificationRepository = notificationRepository;
    }

    // =====================================
    // GET ALL TASKS
    // =====================================

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getAllTasks() {

        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // =====================================
    // GET TASK BY ID
    // =====================================

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long id) {

        Task task = findTask(id);

        return mapToResponseDTO(task);
    }

    // =====================================
    // CREATE TASK
    // USER / TEAM_LEAD / MANAGER / ADMIN
    // =====================================

    public TaskResponseDTO createTask(
            TaskRequestDTO dto,
            Long createdById
    ) {

        User creator = userRepository.findById(createdById)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Creator not found with ID: "
                                        + createdById
                        )
                );

        Task task = new Task();

        mapDTOToTask(dto, task);

        task.setCreatedBy(creator);

        Task savedTask =
                taskRepository.save(task);

        // Assignment notification
        if (savedTask.getAssignedTo() != null) {

            notificationService
                    .createTaskAssignedNotification(
                            savedTask.getAssignedTo(),
                            savedTask
                    );
        }

        // Activity
        activityService.createActivity(
                creator,
                "CREATE",
                creator.getFullName()
                        + " created task \""
                        + savedTask.getTitle()
                        + "\"",
                "TASK",
                savedTask.getId()
        );

        return mapToResponseDTO(savedTask);
    }

    // =====================================
    // UPDATE TASK
    // =====================================

    public TaskResponseDTO updateTask(
            Long id,
            TaskRequestDTO dto
    ) {

        Task task = findTask(id);

        User currentUser =
                getCurrentLoggedInUser();

        checkUpdatePermission(
                task,
                currentUser
        );

        User previousAssignee =
                task.getAssignedTo();

        Long previousAssigneeId =
                previousAssignee != null
                        ? previousAssignee.getId()
                        : null;

        mapDTOToTask(dto, task);

        Task updatedTask =
                taskRepository.save(task);

        User newAssignee =
                updatedTask.getAssignedTo();

        Long newAssigneeId =
                newAssignee != null
                        ? newAssignee.getId()
                        : null;

        boolean assigneeChanged =
                newAssigneeId != null
                        && !newAssigneeId.equals(
                        previousAssigneeId
                );

        // =====================================
        // ASSIGNMENT PERMISSION
        // =====================================

        if (assigneeChanged) {

            checkAssignPermission(currentUser);

            notificationService
                    .createTaskAssignedNotification(
                            newAssignee,
                            updatedTask
                    );

            activityService.createActivity(
                    currentUser,
                    "ASSIGN",
                    currentUser.getFullName()
                            + " assigned task \""
                            + updatedTask.getTitle()
                            + "\" to "
                            + newAssignee.getFullName(),
                    "TASK",
                    updatedTask.getId()
            );
        }

        // =====================================
        // UPDATE ACTIVITY
        // =====================================

        activityService.createActivity(
                currentUser,
                "UPDATE",
                currentUser.getFullName()
                        + " updated task \""
                        + updatedTask.getTitle()
                        + "\"",
                "TASK",
                updatedTask.getId()
        );

        return mapToResponseDTO(updatedTask);
    }

    // =====================================
    // DELETE TASK
    // TEAM_LEAD / ADMIN
    // =====================================

    public void deleteTask(Long id) {

        Task task = findTask(id);

        User currentUser =
                getCurrentLoggedInUser();

        checkDeletePermission(currentUser);

        String taskTitle =
                task.getTitle();

        Long taskId =
                task.getId();

        // Delete comments first
        taskCommentRepository
                .deleteByTask_Id(taskId);

        // Delete notifications
        notificationRepository
                .deleteByTask_Id(taskId);

        // Delete task
        taskRepository.delete(task);

        // Activity
        activityService.createActivity(
                currentUser,
                "DELETE",
                currentUser.getFullName()
                        + " deleted task \""
                        + taskTitle
                        + "\"",
                "TASK",
                taskId
        );
    }

    // =====================================
    // CHECK UPDATE PERMISSION
    // =====================================

    private void checkUpdatePermission(
            Task task,
            User currentUser
    ) {

        String role =
                currentUser.getRole();

        // ADMIN can update everything
        if ("ADMIN".equalsIgnoreCase(role)) {
            return;
        }

        // MANAGER can update
        if ("MANAGER".equalsIgnoreCase(role)) {
            return;
        }

        // TEAM_LEAD can update
        if ("TEAM_LEAD".equalsIgnoreCase(role)) {
            return;
        }

        // USER can update only own/assigned task
        if ("USER".equalsIgnoreCase(role)) {

            boolean isCreator =
                    task.getCreatedBy() != null
                            && task.getCreatedBy()
                            .getId()
                            .equals(currentUser.getId());

            boolean isAssignee =
                    task.getAssignedTo() != null
                            && task.getAssignedTo()
                            .getId()
                            .equals(currentUser.getId());

            if (isCreator || isAssignee) {
                return;
            }
        }

        throw new AccessDeniedException(
                "You do not have permission to update this task"
        );
    }

    // =====================================
    // CHECK ASSIGN PERMISSION
    // TEAM_LEAD / MANAGER / ADMIN
    // =====================================

    private void checkAssignPermission(
            User currentUser
    ) {

        String role =
                currentUser.getRole();

        if ("TEAM_LEAD".equalsIgnoreCase(role)
                || "MANAGER".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            return;
        }

        throw new AccessDeniedException(
                "Only TEAM_LEAD, MANAGER or ADMIN can assign tasks"
        );
    }

    // =====================================
    // CHECK DELETE PERMISSION
    // TEAM_LEAD / ADMIN
    // =====================================

    private void checkDeletePermission(
            User currentUser
    ) {

        String role =
                currentUser.getRole();

        if ("TEAM_LEAD".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            return;
        }

        throw new AccessDeniedException(
                "Only TEAM_LEAD or ADMIN can delete tasks"
        );
    }

    // =====================================
    // GET CURRENT USER
    // =====================================

    private User getCurrentLoggedInUser() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Logged-in user not found"
                        )
                );
    }

    // =====================================
    // FIND TASK
    // =====================================

    private Task findTask(Long id) {

        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Task not found with ID: "
                                        + id
                        )
                );
    }

    // =====================================
    // DTO → ENTITY
    // =====================================

    private void mapDTOToTask(
            TaskRequestDTO dto,
            Task task
    ) {

        task.setTitle(dto.getTitle());

        task.setDescription(
                dto.getDescription()
        );

        task.setStatus(
                dto.getStatus() == null
                        ? "TODO"
                        : dto.getStatus()
        );

        task.setPriority(
                dto.getPriority() == null
                        ? "MEDIUM"
                        : dto.getPriority()
        );

        task.setDueDate(
                dto.getDueDate()
        );

        // =====================================
        // PROJECT
        // =====================================

        Project project =
                projectRepository
                        .findById(dto.getProjectId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Project not found with ID: "
                                                + dto.getProjectId()
                                )
                        );

        task.setProject(project);

        // =====================================
        // TEAM
        // =====================================

        Team team = null;

        if (dto.getTeamId() != null) {

            team =
                    teamRepository
                            .findById(dto.getTeamId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Team not found with ID: "
                                                    + dto.getTeamId()
                                    )
                            );
        }

        task.setTeam(team);

        // =====================================
        // ASSIGNEE
        // =====================================

        if (dto.getAssignedToId() != null) {

            if (team == null) {

                throw new IllegalArgumentException(
                        "Please select a team before assigning a task"
                );
            }

            User assignee =
                    userRepository
                            .findById(dto.getAssignedToId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Assigned user not found with ID: "
                                                    + dto.getAssignedToId()
                                    )
                            );

            boolean isOwner =
                    team.getOwner() != null
                            && team.getOwner()
                            .getId()
                            .equals(assignee.getId());

            boolean isMember =
                    team.getMembers() != null
                            && team.getMembers()
                            .stream()
                            .anyMatch(member ->
                                    member.getId()
                                            .equals(
                                                    assignee.getId()
                                            )
                            );

            if (!isOwner && !isMember) {

                throw new IllegalArgumentException(
                        "Assignee must be the team owner or a member of the selected team"
                );
            }

            task.setAssignedTo(assignee);

        } else {

            task.setAssignedTo(null);
        }
    }

    // =====================================
    // ENTITY → RESPONSE DTO
    // =====================================

    private TaskResponseDTO mapToResponseDTO(
            Task task
    ) {

        TaskResponseDTO dto =
                new TaskResponseDTO();

        dto.setId(task.getId());

        dto.setTitle(task.getTitle());

        dto.setDescription(
                task.getDescription()
        );

        dto.setStatus(
                task.getStatus()
        );

        dto.setPriority(
                task.getPriority()
        );

        dto.setDueDate(
                task.getDueDate()
        );

        dto.setCreatedAt(
                task.getCreatedAt()
        );

        if (task.getProject() != null) {

            dto.setProjectId(
                    task.getProject().getId()
            );

            dto.setProjectName(
                    task.getProject().getName()
            );
        }

        if (task.getTeam() != null) {

            dto.setTeamId(
                    task.getTeam().getId()
            );

            dto.setTeamName(
                    task.getTeam().getName()
            );
        }

        if (task.getAssignedTo() != null) {

            dto.setAssignedToId(
                    task.getAssignedTo().getId()
            );

            dto.setAssignedToName(
                    task.getAssignedTo()
                            .getFullName()
            );
        }

        if (task.getCreatedBy() != null) {

            dto.setCreatedById(
                    task.getCreatedBy().getId()
            );

            dto.setCreatedByName(
                    task.getCreatedBy()
                            .getFullName()
            );
        }

        return dto;
    }
}