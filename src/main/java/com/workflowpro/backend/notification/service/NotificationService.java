package com.workflowpro.backend.notification.service;

import com.workflowpro.backend.notification.dto.NotificationResponseDTO;
import com.workflowpro.backend.notification.entity.Notification;
import com.workflowpro.backend.notification.repository.NotificationRepository;
import com.workflowpro.backend.task.entity.Task;
import com.workflowpro.backend.user.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(
            NotificationRepository notificationRepository
    ) {
        this.notificationRepository = notificationRepository;
    }

    // =========================================================
    // CREATE TASK ASSIGNED NOTIFICATION
    // =========================================================

    @Transactional
    public void createTaskAssignedNotification(
            User recipient,
            Task task
    ) {

        Notification notification = new Notification();

        notification.setRecipient(recipient);
        notification.setTask(task);

        notification.setType("TASK_ASSIGNED");

        notification.setMessage(
                "You have been assigned a new task: "
                        + task.getTitle()
        );

        notification.setReadStatus(false);

        notificationRepository.save(notification);
    }


    // =========================================================
    // GET ALL NOTIFICATIONS FOR LOGGED-IN USER
    // =========================================================

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(
            User user
    ) {

        List<Notification> notifications =
                notificationRepository
                        .findByRecipient_IdOrderByCreatedAtDesc(
                                user.getId()
                        );

        return notifications.stream()
                .map(this::convertToDTO)
                .toList();
    }


    // =========================================================
    // GET UNREAD NOTIFICATION COUNT
    // =========================================================

    @Transactional(readOnly = true)
    public long getUnreadCount(
            User user
    ) {

        return notificationRepository
                .countByRecipient_IdAndReadStatusFalse(
                        user.getId()
                );
    }


    // =========================================================
    // MARK ONE NOTIFICATION AS READ
    // =========================================================

    @Transactional
    public void markAsRead(
            Long notificationId,
            User user
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndRecipient_Id(
                                notificationId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notification.setReadStatus(true);

        notificationRepository.save(notification);
    }


    // =========================================================
    // MARK ALL NOTIFICATIONS AS READ
    // =========================================================

    @Transactional
    public void markAllAsRead(
            User user
    ) {

        List<Notification> notifications =
                notificationRepository
                        .findByRecipient_IdOrderByCreatedAtDesc(
                                user.getId()
                        );

        for (Notification notification : notifications) {

            notification.setReadStatus(true);
        }

        notificationRepository.saveAll(notifications);
    }


    // =========================================================
    // DELETE NOTIFICATION
    // =========================================================

    @Transactional
    public void deleteNotification(
            Long notificationId,
            User user
    ) {

        Notification notification =
                notificationRepository
                        .findByIdAndRecipient_Id(
                                notificationId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        notificationRepository.delete(notification);
    }


    // =========================================================
    // CONVERT ENTITY TO DTO
    // =========================================================

    private NotificationResponseDTO convertToDTO(
            Notification notification
    ) {

        Long taskId = null;

        String taskTitle = null;


        // Check whether notification has a related task

        if (notification.getTask() != null) {

            taskId =
                    notification.getTask().getId();

            taskTitle =
                    notification.getTask().getTitle();
        }


        return new NotificationResponseDTO(

                notification.getId(),

                notification.getMessage(),

                notification.getType(),

                notification.isReadStatus(),

                notification.getCreatedAt(),

                taskId,

                taskTitle
        );
    }
}