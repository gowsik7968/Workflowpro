package com.workflowpro.backend.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponseDTO {

    private Long id;

    private String message;

    private String type;

    private boolean readStatus;

    private LocalDateTime createdAt;

    private Long taskId;

    private String taskTitle;

    public NotificationResponseDTO() {
    }

    public NotificationResponseDTO(
            Long id,
            String message,
            String type,
            boolean readStatus,
            LocalDateTime createdAt,
            Long taskId,
            String taskTitle
    ) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
}