CREATE INDEX idx_jira_connection_workspace_id
    ON jira_connection (workspace_id);

CREATE INDEX idx_jira_connection_status
    ON jira_connection (status);
