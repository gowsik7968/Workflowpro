package com.workflowpro.backend.admin.service;

import com.workflowpro.backend.activity.repository.ActivityRepository;
import com.workflowpro.backend.admin.dto.AdminUserResponseDTO;
import com.workflowpro.backend.admin.dto.ChangeRoleRequest;
import com.workflowpro.backend.notification.repository.NotificationRepository;
import com.workflowpro.backend.task.comment.repository.TaskCommentRepository;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.team.entity.Team;
import com.workflowpro.backend.team.repository.TeamRepository;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminUserService {

    private final UserReopository userRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final NotificationRepository notificationRepository;
    private final ActivityRepository activityRepository;

    public AdminUserService(
            UserReopository userRepository,
            TeamRepository teamRepository,
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository,
            NotificationRepository notificationRepository,
            ActivityRepository activityRepository
    ) {

        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.notificationRepository = notificationRepository;
        this.activityRepository = activityRepository;
    }

    // =====================================
    // GET ALL USERS
    // =====================================

    @Transactional(readOnly = true)
    public List<AdminUserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // =====================================
    // CHANGE USER ROLE
    // =====================================

    public AdminUserResponseDTO changeUserRole(
            Long userId,
            ChangeRoleRequest request
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found with ID: "
                                                + userId
                                )
                        );

        if (request.getRole() == null ||
                request.getRole().isBlank()) {

            throw new IllegalArgumentException(
                    "Role cannot be empty"
            );
        }

        user.setRole(
                request.getRole()
        );

        User updatedUser =
                userRepository.save(user);

        return mapToDTO(updatedUser);
    }

    // =====================================
    // DELETE USER
    // =====================================

    public void deleteUser(
            Long userId,
            String currentAdminEmail
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "User not found with ID: "
                                                + userId
                                )
                        );

        // =====================================
        // PREVENT SELF DELETE
        // =====================================

        if (user.getEmail()
                .equalsIgnoreCase(
                        currentAdminEmail
                )) {

            throw new IllegalArgumentException(
                    "You cannot delete your own admin account"
            );
        }

        // =====================================
        // CHECK TEAM OWNERSHIP
        // =====================================

        List<Team> ownedTeams =
                teamRepository.findByOwner_Id(
                        userId
                );

        if (!ownedTeams.isEmpty()) {

            throw new IllegalArgumentException(
                    "Cannot delete this user because they own "
                            + ownedTeams.size()
                            + " team(s). Transfer or delete the team first."
            );
        }

        // =====================================
        // CHECK CREATED TASKS
        // =====================================

        List<Task> createdTasks =
                taskRepository.findByCreatedBy_Id(
                        userId
                );

        if (!createdTasks.isEmpty()) {

            throw new IllegalArgumentException(
                    "Cannot delete this user because they created "
                            + createdTasks.size()
                            + " task(s)."
            );
        }

        // =====================================
        // UNASSIGN TASKS
        // =====================================

        List<Task> assignedTasks =
                taskRepository.findByAssignedTo_Id(
                        userId
                );

        for (Task task : assignedTasks) {

            task.setAssignedTo(null);

            taskRepository.save(task);
        }

        // =====================================
        // REMOVE TEAM MEMBERSHIP
        // =====================================

        List<Team> memberTeams =
                teamRepository.findDistinctByMembers_Id(
                        userId
                );

        for (Team team : memberTeams) {

            team.getMembers()
                    .removeIf(member ->
                            member.getId()
                                    .equals(userId)
                    );

            teamRepository.save(team);
        }

        // =====================================
        // DELETE USER COMMENTS
        // =====================================

        taskCommentRepository
                .deleteByUser_Id(userId);

        // =====================================
        // DELETE USER NOTIFICATIONS
        // =====================================

        notificationRepository
                .deleteByRecipient_Id(userId);

        // =====================================
        // DELETE USER ACTIVITIES
        // =====================================

        activityRepository
                .deleteByUser_Id(userId);

        // =====================================
        // DELETE USER
        // =====================================

        userRepository.delete(user);
    }

    // =====================================
    // USER -> DTO
    // =====================================

    private AdminUserResponseDTO mapToDTO(
            User user
    ) {

        AdminUserResponseDTO dto =
                new AdminUserResponseDTO();

        dto.setId(
                user.getId()
        );

        dto.setFullName(
                user.getFullName()
        );

        dto.setEmail(
                user.getEmail()
        );

        dto.setRole(
                user.getRole()
        );

        dto.setCreatedAt(
                user.getCreatedAt()
        );

        return dto;
    }
}