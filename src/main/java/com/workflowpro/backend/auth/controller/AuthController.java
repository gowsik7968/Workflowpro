package com.workflowpro.backend.auth.controller;

import com.workflowpro.backend.user.dto.AuthResponse;
import com.workflowpro.backend.user.dto.LoginRequest;
import com.workflowpro.backend.user.dto.RegistrationRequest;
import com.workflowpro.backend.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse>register(
            @Valid @RequestBody RegistrationRequest request){
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>login(
            @Valid @RequestBody LoginRequest request){
        AuthResponse response= authService.login(request);
        return ResponseEntity.ok(response);
    }

}
