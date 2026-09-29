package com.workflowpro.backend.project.service;

import com.workflowpro.backend.activity.service.ActivityService;
import com.workflowpro.backend.notification.repository.NotificationRepository;
import com.workflowpro.backend.project.entity.Project;
import com.workflowpro.backend.project.repository.ProjectRepository;
import com.workflowpro.backend.task.comment.repository.TaskCommentRepository;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserReopository userRepository;
    private final ActivityService activityService;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final NotificationRepository notificationRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            UserReopository userRepository,
            ActivityService activityService,
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository,
            NotificationRepository notificationRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.notificationRepository = notificationRepository;
    }

    // =====================================
    // CREATE PROJECT
    // MANAGER / ADMIN
    // =====================================

    public Project createProject(Project project) {

        User currentUser =
                getCurrentLoggedInUser();

        checkCreatePermission(currentUser);

        Project savedProject =
                projectRepository.save(project);

        activityService.createActivity(
                currentUser,
                "CREATE",
                currentUser.getFullName()
                        + " created project \""
                        + savedProject.getName()
                        + "\"",
                "PROJECT",
                savedProject.getId()
        );

        return savedProject;
    }

    // =====================================
    // GET ALL PROJECTS
    // =====================================

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {

        return projectRepository.findAll();
    }

    // =====================================
    // GET PROJECT
    // =====================================

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Project not found with ID: "
                                        + id
                        )
                );
    }

    // =====================================
    // UPDATE PROJECT
    // MANAGER / ADMIN
    // =====================================

    public Project updateProject(
            Long id,
            Project updatedProject
    ) {

        User currentUser =
                getCurrentLoggedInUser();

        checkUpdatePermission(currentUser);

        Project existingProject =
                getProjectById(id);

        existingProject.setName(
                updatedProject.getName()
        );

        existingProject.setDescription(
                updatedProject.getDescription()
        );

        existingProject.setStatus(
                updatedProject.getStatus()
        );

        existingProject.setStartDate(
                updatedProject.getStartDate()
        );

        existingProject.setDueDate(
                updatedProject.getDueDate()
        );

        Project savedProject =
                projectRepository.save(
                        existingProject
                );

        activityService.createActivity(
                currentUser,
                "UPDATE",
                currentUser.getFullName()
                        + " updated project \""
                        + savedProject.getName()
                        + "\"",
                "PROJECT",
                savedProject.getId()
        );

        return savedProject;
    }

    // =====================================
    // DELETE PROJECT
    // ADMIN ONLY
    // =====================================

    public void deleteProject(Long id) {

        User currentUser =
                getCurrentLoggedInUser();

        checkDeletePermission(currentUser);

        Project project =
                getProjectById(id);

        String projectName =
                project.getName();

        Long projectId =
                project.getId();

        List<Task> tasks =
                taskRepository.findByProject_Id(
                        projectId
                );

        for (Task task : tasks) {

            Long taskId =
                    task.getId();

            taskCommentRepository
                    .deleteByTask_Id(taskId);

            notificationRepository
                    .deleteByTask_Id(taskId);
        }

        if (!tasks.isEmpty()) {

            taskRepository.deleteAll(tasks);
        }

        projectRepository.delete(project);

        activityService.createActivity(
                currentUser,
                "DELETE",
                currentUser.getFullName()
                        + " deleted project \""
                        + projectName
                        + "\"",
                "PROJECT",
                projectId
        );
    }

    // =====================================
    // CREATE PERMISSION
    // =====================================

    private void checkCreatePermission(
            User currentUser
    ) {

        String role =
                currentUser.getRole();

        if ("MANAGER".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            return;
        }

        throw new AccessDeniedException(
                "Only MANAGER or ADMIN can create projects"
        );
    }

    // =====================================
    // UPDATE PERMISSION
    // =====================================

    private void checkUpdatePermission(
            User currentUser
    ) {

        String role =
                currentUser.getRole();

        if ("MANAGER".equalsIgnoreCase(role)
                || "ADMIN".equalsIgnoreCase(role)) {

            return;
        }

        throw new AccessDeniedException(
                "Only MANAGER or ADMIN can update projects"
        );
    }

    // =====================================
    // DELETE PERMISSION
    // =====================================

    private void checkDeletePermission(
            User currentUser
    ) {

        if ("ADMIN".equalsIgnoreCase(
                currentUser.getRole()
        )) {
            return;
        }

        throw new AccessDeniedException(
                "Only ADMIN can delete projects"
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
}