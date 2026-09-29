package com.workflowpro.backend.notification.controller;

import com.workflowpro.backend.notification.dto.NotificationResponseDTO;
import com.workflowpro.backend.notification.service.NotificationService;
import com.workflowpro.backend.user.entity.User;

import com.workflowpro.backend.user.repository.UserReopository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserReopository userRepository;

    public NotificationController(
            NotificationService notificationService,
            UserReopository userRepository
    ) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications(
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        return ResponseEntity.ok(
                notificationService.getUserNotifications(user)
        );
    }

    // Get unread notification count
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        return ResponseEntity.ok(
                notificationService.getUnreadCount(user)
        );
    }

    // Mark one notification as read
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(
            @PathVariable Long id,
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        notificationService.markAsRead(id, user);

        return ResponseEntity.ok(
                "Notification marked as read"
        );
    }

    // Mark all notifications as read
    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        notificationService.markAllAsRead(user);

        return ResponseEntity.ok(
                "All notifications marked as read"
        );
    }

    // Delete notification
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id,
            Principal principal
    ) {

        User user = getLoggedInUser(principal);

        notificationService.deleteNotification(id, user);

        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }

    // Get currently logged-in user
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