package com.workflowpro.backend.activity.controller;

import com.workflowpro.backend.activity.dto.ActivityResponseDTO;
import com.workflowpro.backend.activity.service.ActivityService;
import com.workflowpro.backend.user.entity.User;
import com.workflowpro.backend.user.repository.UserReopository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final UserReopository userRepository;

    public ActivityController(
            ActivityService activityService,
            UserReopository userRepository
    ) {
        this.activityService = activityService;
        this.userRepository = userRepository;
    }

    // =====================================
    // GET ALL ACTIVITIES
    // =====================================

    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> getAllActivities() {

        return ResponseEntity.ok(
                activityService.getAllActivities()
        );
    }

    // =====================================
    // GET MY ACTIVITIES
    // =====================================

    @GetMapping("/my")
    public ResponseEntity<List<ActivityResponseDTO>> getMyActivities(
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        return ResponseEntity.ok(
                activityService.getUserActivities(user)
        );
    }

    // =====================================
    // GET LOGGED-IN USER
    // =====================================

    private User getLoggedInUser(Principal principal) {

        return userRepository
                .findByEmail(principal.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        )
                );
    }
}