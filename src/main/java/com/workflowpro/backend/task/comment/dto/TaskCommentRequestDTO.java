
package com.workflowpro.backend.task.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCommentRequestDTO {

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    private String content;

    public TaskCommentRequestDTO() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}