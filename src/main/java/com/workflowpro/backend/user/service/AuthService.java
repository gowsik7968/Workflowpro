package com.workflowpro.backend.user.service;

import com.workflowpro.backend.auth.service.JwtService;
import com.workflowpro.backend.user.dto.AuthResponse;
import com.workflowpro.backend.user.dto.LoginRequest;
import com.workflowpro.backend.user.dto.RegistrationRequest;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserReopository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Constructor Injection
    public AuthService(
            UserReopository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // USER REGISTRATION
    // =========================
    public AuthResponse register(RegistrationRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        // Check whether email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException(
                    "Email is already registered"
            );
        }

        // Create new user
        User user = new User();

        user.setFullName(request.fullName());
        user.setEmail(email);

        // Encrypt password using BCrypt
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        user.setRole("USER");

        // Save user into MySQL
        User savedUser = userRepository.save(user);

        // Registration response (JWT not generated here)
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "Registration successful",
                null
        );
    }

    // =========================
    // USER LOGIN
    // =========================
    public AuthResponse login(LoginRequest request) {

        String email = request.email()
                .trim()
                .toLowerCase();

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        )
                );

        // Verify entered password with BCrypt hash
        boolean passwordMatches =
                passwordEncoder.matches(
                        request.password(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        // Generate JWT token after successful login
        String token = jwtService.generateToken(user);

        // Return user details and JWT token
        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                "Login successful",
                token
        );
    }
}