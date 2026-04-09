CREATE UNIQUE INDEX idx_jira_board_workspace_external_board
    ON jira_board (workspace_id, external_board_id);

CREATE INDEX idx_jira_sprint_workspace_external_sprint
    ON jira_sprint (workspace_id, external_sprint_id);

CREATE INDEX idx_jira_issue_workspace_external_sprint
    ON jira_issue (workspace_id, external_sprint_id);

CREATE INDEX idx_jira_issue_issue_key
    ON jira_issue (issue_key);

CREATE INDEX idx_jira_comment_issue_id
    ON jira_comment (jira_issue_id);

CREATE INDEX idx_jira_changelog_issue_id
    ON jira_changelog_event (jira_issue_id);

CREATE INDEX idx_jira_raw_payload_workspace_connection_type
    ON jira_raw_payload (workspace_id, jira_connection_id, payload_type);
