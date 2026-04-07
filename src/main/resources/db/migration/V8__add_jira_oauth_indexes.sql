CREATE INDEX idx_jira_oauth_state_workspace_id
    ON jira_oauth_state (workspace_id);

CREATE INDEX idx_jira_oauth_state_connection_id
    ON jira_oauth_state (connection_id);

CREATE INDEX idx_jira_oauth_state_expires_at
    ON jira_oauth_state (expires_at);

CREATE INDEX idx_jira_connection_workspace_status
    ON jira_connection (workspace_id, status);
