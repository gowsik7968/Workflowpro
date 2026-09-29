package com.workflowpro.backend.activity.service;

import com.workflowpro.backend.activity.dto.ActivityResponseDTO;
import com.workflowpro.backend.activity.entity.Activity;
import com.workflowpro.backend.activity.repository.ActivityRepository;
import com.workflowpro.backend.user.entity.User;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(
            ActivityRepository activityRepository
    ) {
        this.activityRepository = activityRepository;
    }

    // =====================================
    // CREATE ACTIVITY
    // =====================================

    @Transactional
    public void createActivity(
            User user,
            String action,
            String description,
            String entityType,
            Long entityId
    ) {

        Activity activity = new Activity();

        activity.setUser(user);
        activity.setAction(action);
        activity.setDescription(description);
        activity.setEntityType(entityType);
        activity.setEntityId(entityId);

        activityRepository.save(activity);
    }

    // =====================================
    // GET ALL ACTIVITIES
    // =====================================

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getAllActivities() {

        return activityRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =====================================
    // GET USER ACTIVITIES
    // =====================================

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getUserActivities(
            User user
    ) {

        return activityRepository
                .findByUser_IdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // =====================================
    // CONVERT ENTITY TO DTO
    // =====================================

    private ActivityResponseDTO convertToDTO(
            Activity activity
    ) {

        return new ActivityResponseDTO(
                activity.getId(),
                activity.getUser().getId(),
                activity.getUser().getFullName(),
                activity.getAction(),
                activity.getDescription(),
                activity.getEntityType(),
                activity.getEntityId(),
                activity.getCreatedAt()
        );
    }
}