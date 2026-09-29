package com.workflowpro.backend.admin.controller;

import com.workflowpro.backend.admin.dto.AdminUserResponseDTO;
import com.workflowpro.backend.admin.dto.ChangeRoleRequest;
import com.workflowpro.backend.admin.service.AdminUserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService
    ) {

        this.adminUserService =
                adminUserService;
    }

    // =====================================
    // GET ALL USERS
    // =====================================

    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponseDTO>>
    getAllUsers() {

        return ResponseEntity.ok(
                adminUserService.getAllUsers()
        );
    }

    // =====================================
    // CHANGE USER ROLE
    // =====================================

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserResponseDTO>
    changeUserRole(
            @PathVariable Long userId,
            @RequestBody ChangeRoleRequest request
    ) {

        return ResponseEntity.ok(
                adminUserService.changeUserRole(
                        userId,
                        request
                )
        );
    }

    // =====================================
    // DELETE USER
    // =====================================

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long userId,
            Principal principal
    ) {

        adminUserService.deleteUser(
                userId,
                principal.getName()
        );

        return ResponseEntity.noContent()
                .build();
    }
}