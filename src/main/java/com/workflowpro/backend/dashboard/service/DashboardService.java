package com.workflowpro.backend.dashboard.service;

import com.workflowpro.backend.dashboard.dto.DashboardStatsDTO;
import com.workflowpro.backend.project.service.ProjectService;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.team.repository.TeamRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final ProjectService projectService;
    private final TaskRepository taskRepository;
    private final TeamRepository teamRepository;

    public DashboardService(
            ProjectService projectService,
            TaskRepository taskRepository,
            TeamRepository teamRepository
    ) {
        this.projectService = projectService;
        this.taskRepository = taskRepository;
        this.teamRepository = teamRepository;
    }

    // =====================================
    // GET DASHBOARD STATISTICS
    // =====================================

    public DashboardStatsDTO getDashboardStats() {

        // Total projects
        long totalProjects =
                projectService.getAllProjects().size();

        // Total teams
        long totalTeams =
                teamRepository.count();

        // Get all tasks
        List<Task> tasks =
                taskRepository.findAll();

        long totalTasks =
                tasks.size();

        // =====================================
        // TASK STATUS COUNTS
        // =====================================

        long todoTasks =
                tasks.stream()
                        .filter(task ->
                                "TODO".equalsIgnoreCase(
                                        task.getStatus()
                                ))
                        .count();

        long inProgressTasks =
                tasks.stream()
                        .filter(task ->
                                "IN_PROGRESS".equalsIgnoreCase(
                                        task.getStatus()
                                ))
                        .count();

        long completedTasks =
                tasks.stream()
                        .filter(task ->
                                "DONE".equalsIgnoreCase(
                                        task.getStatus()
                                ))
                        .count();

        // =====================================
        // RETURN DASHBOARD DATA
        // =====================================

        return new DashboardStatsDTO(
                totalProjects,
                totalTasks,
                todoTasks,
                inProgressTasks,
                completedTasks,
                totalTeams
        );
    }
}