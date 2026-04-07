package com.willmear.sprint.jira.infrastructure.repository;

import com.willmear.sprint.jira.infrastructure.entity.JiraOAuthStateEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JiraOAuthStateRepository extends JpaRepository<JiraOAuthStateEntity, UUID> {

    @Query("""
            select jiraOAuthState
            from JiraOAuthStateEntity jiraOAuthState
            where jiraOAuthState.state = :state
              and jiraOAuthState.consumed = false
              and jiraOAuthState.expiresAt > :now
            """)
    Optional<JiraOAuthStateEntity> findActiveByState(@Param("state") String state, @Param("now") Instant now);
}
