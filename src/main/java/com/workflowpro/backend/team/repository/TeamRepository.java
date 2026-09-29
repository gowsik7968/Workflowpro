package com.workflowpro.backend.team.repository;

import com.workflowpro.backend.team.entity.Team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository
        extends JpaRepository<Team, Long> {

    List<Team> findDistinctByOwner_EmailOrMembers_Email(
            String ownerEmail,
            String memberEmail
    );

    // Find teams owned by a user
    List<Team> findByOwner_Id(Long ownerId);

    // Find teams where user is a member
    List<Team> findDistinctByMembers_Id(Long userId);
}