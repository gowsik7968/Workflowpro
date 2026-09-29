package com.workflowpro.backend.notification.repository;

import com.workflowpro.backend.notification.entity.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(
            Long recipientId
    );

    long countByRecipient_IdAndReadStatusFalse(
            Long recipientId
    );

    Optional<Notification> findByIdAndRecipient_Id(
            Long id,
            Long recipientId
    );

    // Delete notifications connected to a task
    void deleteByTask_Id(Long taskId);

    // Delete notifications received by a user
    void deleteByRecipient_Id(Long recipientId);
}