CREATE TABLE jira_comment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    jira_issue_id UUID NOT NULL,
    external_comment_id VARCHAR(100) NOT NULL,
    issue_key VARCHAR(100) NOT NULL,
    author_display_name VARCHAR(255) NULL,
    body TEXT NULL,
    created_at_external TIMESTAMPTZ NULL,
    updated_at_external TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_comment_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_comment_issue
        FOREIGN KEY (jira_issue_id)
        REFERENCES jira_issue (id)
);
