package com.workflowpro.backend.user.dto;

public record AuthResponse(
        Long id,
        String fullName,
        String email,
        String role,
        String message,
        String token
) { }
