package com.workflowpro.backend.activity.dto;

import java.time.LocalDateTime;

public class ActivityResponseDTO {

    private Long id;

    private Long userId;

    private String userName;

    private String action;

    private String description;

    private String entityType;

    private Long entityId;

    private LocalDateTime createdAt;

    public ActivityResponseDTO() {
    }

    public ActivityResponseDTO(
            Long id,
            Long userId,
            String userName,
            String action,
            String description,
            String entityType,
            Long entityId,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}