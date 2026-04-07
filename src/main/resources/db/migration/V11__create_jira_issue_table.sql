CREATE TABLE jira_issue (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    jira_connection_id UUID NOT NULL,
    jira_sprint_id UUID NOT NULL,
    external_sprint_id BIGINT NOT NULL,
    issue_key VARCHAR(100) NOT NULL,
    external_issue_id VARCHAR(100) NOT NULL,
    summary VARCHAR(500) NOT NULL,
    description TEXT NULL,
    issue_type VARCHAR(100) NULL,
    status VARCHAR(100) NULL,
    priority VARCHAR(100) NULL,
    assignee_display_name VARCHAR(255) NULL,
    reporter_display_name VARCHAR(255) NULL,
    story_points INTEGER NULL,
    created_at_external TIMESTAMPTZ NULL,
    updated_at_external TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_issue_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_issue_connection
        FOREIGN KEY (jira_connection_id)
        REFERENCES jira_connection (id),
    CONSTRAINT fk_jira_issue_sprint
        FOREIGN KEY (jira_sprint_id)
        REFERENCES jira_sprint (id),
    CONSTRAINT uk_jira_issue_workspace_sprint_issue_key
        UNIQUE (workspace_id, external_sprint_id, issue_key)
);
