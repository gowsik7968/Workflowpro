package com.workflowpro.backend.team.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TeamResponse {

    private Long id;
    private String name;
    private String description;
    private String ownerEmail;
    private List<String> memberEmails;
    private List<TeamMemberDTO> members;
    private LocalDateTime createdAt;

    public TeamResponse() {
    }

    public TeamResponse(
            Long id,
            String name,
            String description,
            String ownerEmail,
            List<String> memberEmails,
            List<TeamMemberDTO> members,
            LocalDateTime createdAt) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerEmail = ownerEmail;
        this.memberEmails = memberEmails;
        this.members = members;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public List<String> getMemberEmails() {
        return memberEmails;
    }

    public List<TeamMemberDTO> getMembers() {
        return members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}