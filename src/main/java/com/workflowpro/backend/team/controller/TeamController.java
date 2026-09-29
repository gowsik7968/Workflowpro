package com.workflowpro.backend.team.controller;

import com.workflowpro.backend.team.dto.*;
import com.workflowpro.backend.team.service.TeamService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    // CREATE TEAM
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            Authentication authentication) {

        TeamResponse response = teamService.createTeam(
                request,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET MY TEAMS
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getMyTeams(
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.getMyTeams(authentication.getName())
        );
    }

    // GET TEAM DETAILS
    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.getTeamById(id, authentication.getName())
        );
    }

    // ADD MEMBER
    @PostMapping("/{id}/members")
    public ResponseEntity<TeamResponse> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddTeamMemberRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.addMember(
                        id, request, authentication.getName())
        );
    }

    // REMOVE MEMBER
    @DeleteMapping("/{id}/members")
    public ResponseEntity<TeamResponse> removeMember(
            @PathVariable Long id,
            @RequestParam String email,
            Authentication authentication) {

        return ResponseEntity.ok(
                teamService.removeMember(
                        id, email, authentication.getName())
        );
    }

    // DELETE TEAM
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long id,
            Authentication authentication) {

        teamService.deleteTeam(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}