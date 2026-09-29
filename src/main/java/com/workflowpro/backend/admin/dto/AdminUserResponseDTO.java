package com.workflowpro.backend.admin.dto;

import java.time.LocalDateTime;

public class AdminUserResponseDTO {

    private Long id;

    private String fullName;

    private String email;

    private String role;

    private LocalDateTime createdAt;

    public AdminUserResponseDTO() {
    }

    public AdminUserResponseDTO(
            Long id,
            String fullName,
            String email,
            String role,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    // =====================================
    // GETTERS
    // =====================================

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // =====================================
    // SETTERS
    // =====================================

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}