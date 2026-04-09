CREATE TABLE jira_changelog_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    jira_issue_id UUID NOT NULL,
    external_history_id VARCHAR(100) NOT NULL,
    issue_key VARCHAR(100) NOT NULL,
    field_name VARCHAR(255) NOT NULL,
    from_value TEXT NULL,
    to_value TEXT NULL,
    changed_at TIMESTAMPTZ NULL,
    author_display_name VARCHAR(255) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_changelog_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_changelog_issue
        FOREIGN KEY (jira_issue_id)
        REFERENCES jira_issue (id)
);
