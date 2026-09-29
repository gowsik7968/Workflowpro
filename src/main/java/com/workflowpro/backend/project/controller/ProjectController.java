package com.workflowpro.backend.project.controller;


import com.workflowpro.backend.project.service.ProjectService;

import com.workflowpro.backend.project.entity.Project;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // CREATE PROJECT
    @PostMapping
    public ResponseEntity<Project> createProject(
            @Valid @RequestBody Project project) {

        Project createdProject =
                projectService.createProject(project);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProject);
    }

    // GET ALL PROJECTS
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {

        return ResponseEntity.ok(
                projectService.getAllProjects()
        );
    }

    // GET PROJECT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                projectService.getProjectById(id)
        );
    }

    // UPDATE PROJECT
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody Project project) {

        return ResponseEntity.ok(
                projectService.updateProject(id, project)
        );
    }

    // DELETE PROJECT
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id) {

        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }
}