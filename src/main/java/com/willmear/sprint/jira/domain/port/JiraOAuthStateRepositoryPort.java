package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraOAuthState;
import java.time.Instant;
import java.util.Optional;

public interface JiraOAuthStateRepositoryPort {

    JiraOAuthState save(JiraOAuthState jiraOAuthState);

    Optional<JiraOAuthState> findActiveByState(String state, Instant now);
}
