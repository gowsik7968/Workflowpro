package com.workflowpro.backend.activity.repository;

import com.workflowpro.backend.activity.entity.Activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository
        extends JpaRepository<Activity, Long> {

    List<Activity> findAllByOrderByCreatedAtDesc();

    List<Activity> findByUser_IdOrderByCreatedAtDesc(
            Long userId
    );

    // Delete activity records belonging to a user
    void deleteByUser_Id(Long userId);
}