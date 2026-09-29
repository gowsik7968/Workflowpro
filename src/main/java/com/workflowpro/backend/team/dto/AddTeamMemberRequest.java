package com.workflowpro.backend.team.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AddTeamMemberRequest {

    @NotBlank(message = "Member email is required")
    @Email(message = "Enter a valid email")
    private String email;

    public AddTeamMemberRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}