
package com.workflowpro.backend.task.comment.dto;

import java.time.LocalDateTime;

public class TaskCommentResponseDTO {

    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    public TaskCommentResponseDTO(
            Long id,
            String content,
            String authorName,
            LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}