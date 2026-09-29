package com.workflowpro.backend.user.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // ==============================
    // USER ROLE
    // ==============================

    @Column(nullable = false, length = 20)
    private String role;

    // ==============================
    // CREATED DATE
    // ==============================

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ==============================
    // PRE-PERSIST
    // ==============================

    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        // Every newly registered user
        // will be USER by default.
        if (role == null || role.isBlank()) {
            role = "USER";
        }
    }

    // ==============================
    // CONSTRUCTOR
    // ==============================

    public User() {
    }

    // ==============================
    // GETTERS AND SETTERS
    // ==============================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}