package com.workflowpro.backend.config;

import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createInitialAdmin(
            UserReopository userRepository,
            PasswordEncoder passwordEncoder,

            @Value("${app.admin.name}")
            String adminName,

            @Value("${app.admin.email}")
            String adminEmail,

            @Value("${app.admin.password}")
            String adminPassword
    ) {

        return args -> {

            String email = adminEmail
                    .trim()
                    .toLowerCase();

            // Check whether admin already exists
            if (userRepository.existsByEmail(email)) {

                System.out.println(
                        "Initial admin already exists: " + email
                );

                return;
            }

            // Create admin user
            User admin = new User();

            admin.setFullName(adminName);
            admin.setEmail(email);

            // Encrypt password using BCrypt
            admin.setPassword(
                    passwordEncoder.encode(adminPassword)
            );

            // Explicitly make this user ADMIN
            admin.setRole("ADMIN");

            // Save admin
            userRepository.save(admin);

            System.out.println(
                    "=========================================="
            );

            System.out.println(
                    "Initial ADMIN account created"
            );

            System.out.println(
                    "Email: " + email
            );

            System.out.println(
                    "=========================================="
            );
        };
    }
}