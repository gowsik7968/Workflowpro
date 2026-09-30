package com.workflowpro.backend.team.service;

import com.workflowpro.backend.activity.service.ActivityService;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.task.repository.TaskRepository;
import com.workflowpro.backend.team.dto.*;
import com.workflowpro.backend.team.entity.Team;
import com.workflowpro.backend.team.repository.TeamRepository;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserReopository userRepository;
    private final ActivityService activityService;
    private final TaskRepository taskRepository;

    public TeamService(
            TeamRepository teamRepository,
            UserReopository userRepository,
            ActivityService activityService,
            TaskRepository taskRepository
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
        this.taskRepository = taskRepository;
    }

    // GET CURRENT USER

    private User getCurrentUser(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }

    // GET TEAM

    private Team getTeam(Long id) {

        return teamRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Team not found with ID: " + id
                        )
                );
    }

    // CHECK ROLE

    private boolean hasRole(
            User user,
            String role
    ) {

        return role.equalsIgnoreCase(
                user.getRole()
        );
    }

    // CHECK TEAM MANAGEMENT PERMISSION

    private void checkTeamManagementPermission(
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
                "Only TEAM_LEAD, MANAGER or ADMIN can manage teams"
        );
    }

    // CHECK TEAM DELETE PERMISSION

    private void checkDeletePermission(
            Team team,
            User currentUser
    ) {

        if (hasRole(currentUser, "ADMIN")) {
            return;
        }

        if (hasRole(currentUser, "TEAM_LEAD")
                && team.getOwner() != null
                && team.getOwner()
                .getId()
                .equals(currentUser.getId())) {

            return;
        }

        throw new AccessDeniedException(
                "Only the TEAM_LEAD owner or ADMIN can delete this team"
        );
    }

    // CHECK TEAM ACCESS

    private void checkTeamAccess(
            Team team,
            String email
    ) {

        boolean isOwner =
                team.getOwner() != null
                        && team.getOwner()
                        .getEmail()
                        .equalsIgnoreCase(email);

        boolean isMember =
                team.getMembers() != null
                        && team.getMembers()
                        .stream()
                        .anyMatch(user ->
                                user.getEmail() != null
                                        && user.getEmail()
                                        .equalsIgnoreCase(email)
                        );

        if (!isOwner && !isMember) {

            User currentUser =
                    getCurrentUser(email);

            if (hasRole(currentUser, "MANAGER")
                    || hasRole(currentUser, "ADMIN")) {

                return;
            }

            throw new AccessDeniedException(
                    "You do not have access to this team"
            );
        }
    }

    // TEAM TO RESPONSE

    private TeamResponse toResponse(
            Team team
    ) {

        System.out.println(
                "TEAM RESPONSE: "
                        + team.getId()
                        + " - "
                        + team.getName()
        );

        List<String> memberEmails =
                new ArrayList<>();

        List<TeamMemberDTO> members =
                new ArrayList<>();

        User owner =
                team.getOwner();

        if (owner != null) {

            members.add(
                    new TeamMemberDTO(
                            owner.getId(),
                            owner.getFullName(),
                            owner.getEmail()
                    )
            );
        }

        if (team.getMembers() != null) {

            System.out.println(
                    "TEAM MEMBERS COUNT: "
                            + team.getMembers().size()
            );

            for (User user : team.getMembers()) {

                System.out.println(
                        "TEAM MEMBER: "
                                + user.getId()
                                + " - "
                                + user.getEmail()
                );

                if (user.getEmail() != null) {

                    memberEmails.add(
                            user.getEmail()
                    );
                }

                members.add(
                        new TeamMemberDTO(
                                user.getId(),
                                user.getFullName(),
                                user.getEmail()
                        )
                );
            }
        }

        memberEmails.sort(
                String::compareToIgnoreCase
        );

        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.getDescription(),
                owner != null
                        ? owner.getEmail()
                        : null,
                memberEmails,
                members,
                team.getCreatedAt()
        );
    }

    // CREATE TEAM

    public TeamResponse createTeam(
            CreateTeamRequest request,
            String currentEmail
    ) {

        User owner =
                getCurrentUser(currentEmail);

        checkTeamManagementPermission(
                owner
        );

        Team team =
                new Team();

        team.setName(
                request.getName().trim()
        );

        team.setDescription(
                request.getDescription()
        );

        team.setOwner(owner);

        Team savedTeam =
                teamRepository.save(team);

        activityService.createActivity(
                owner,
                "CREATE",
                owner.getFullName()
                        + " created team \""
                        + savedTeam.getName()
                        + "\"",
                "TEAM",
                savedTeam.getId()
        );

        return toResponse(savedTeam);
    }

    // GET MY TEAMS

    @Transactional(readOnly = true)
    public List<TeamResponse> getMyTeams(
            String currentEmail
    ) {

        System.out.println(
                "GET MY TEAMS CALLED FOR: "
                        + currentEmail
        );

        User currentUser =
                getCurrentUser(currentEmail);

        if (hasRole(currentUser, "ADMIN")
                || hasRole(currentUser, "MANAGER")) {

            return teamRepository.findAll()
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return teamRepository
                .findDistinctByOwner_EmailOrMembers_Email(
                        currentEmail,
                        currentEmail
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // GET TEAM DETAILS

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(
            Long id,
            String currentEmail
    ) {

        Team team =
                getTeam(id);

        checkTeamAccess(
                team,
                currentEmail
        );

        return toResponse(team);
    }

    // ADD MEMBER

    public TeamResponse addMember(
            Long teamId,
            AddTeamMemberRequest request,
            String currentEmail
    ) {

        Team team =
                getTeam(teamId);

        User currentUser =
                getCurrentUser(currentEmail);

        checkTeamManagementPermission(
                currentUser
        );

        User member =
                userRepository.findByEmail(
                                request.getEmail()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No registered user found with that email"
                                )
                        );

        if (team.getOwner() != null
                && team.getOwner()
                .getId()
                .equals(member.getId())) {

            throw new RuntimeException(
                    "Team owner is already associated with this team"
            );
        }

        boolean alreadyMember =
                team.getMembers() != null
                        && team.getMembers()
                        .stream()
                        .anyMatch(user ->
                                user.getId()
                                        .equals(member.getId())
                        );

        if (alreadyMember) {

            throw new RuntimeException(
                    "User is already a member of this team"
            );
        }

        team.getMembers()
                .add(member);

        Team savedTeam =
                teamRepository.save(team);

        activityService.createActivity(
                currentUser,
                "ADD_MEMBER",
                currentUser.getFullName()
                        + " added "
                        + member.getFullName()
                        + " to team \""
                        + savedTeam.getName()
                        + "\"",
                "TEAM",
                savedTeam.getId()
        );

        return toResponse(savedTeam);
    }

    // REMOVE MEMBER

    public TeamResponse removeMember(
            Long teamId,
            String memberEmail,
            String currentEmail
    ) {

        Team team =
                getTeam(teamId);

        User currentUser =
                getCurrentUser(currentEmail);

        checkTeamManagementPermission(
                currentUser
        );

        User member =
                userRepository.findByEmail(
                                memberEmail
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        boolean removed =
                team.getMembers()
                        .removeIf(user ->
                                user.getId()
                                        .equals(member.getId())
                        );

        if (!removed) {

            throw new RuntimeException(
                    "This user is not a member of the team"
            );
        }

        Team savedTeam =
                teamRepository.save(team);

        activityService.createActivity(
                currentUser,
                "REMOVE_MEMBER",
                currentUser.getFullName()
                        + " removed "
                        + member.getFullName()
                        + " from team \""
                        + savedTeam.getName()
                        + "\"",
                "TEAM",
                savedTeam.getId()
        );

        return toResponse(savedTeam);
    }

    // DELETE TEAM

    public void deleteTeam(
            Long teamId,
            String currentEmail
    ) {

        Team team =
                getTeam(teamId);

        User currentUser =
                getCurrentUser(currentEmail);

        checkDeletePermission(
                team,
                currentUser
        );

        String teamName =
                team.getName();

        Long teamIdValue =
                team.getId();

        // DETACH TASKS

        List<Task> tasks =
                taskRepository
                        .findByTeam_Id(teamId);

        for (Task task : tasks) {

            task.setTeam(null);

            taskRepository.save(task);
        }

        // REMOVE MEMBERS

        if (team.getMembers() != null) {
            team.getMembers().clear();
        }

        teamRepository.save(team);

        // DELETE TEAM

        teamRepository.delete(team);

        // ACTIVITY

        activityService.createActivity(
                currentUser,
                "DELETE",
                currentUser.getFullName()
                        + " deleted team \""
                        + teamName
                        + "\"",
                "TEAM",
                teamIdValue
        );
    }
}

