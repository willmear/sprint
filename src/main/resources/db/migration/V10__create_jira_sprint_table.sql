CREATE TABLE jira_sprint (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    jira_connection_id UUID NOT NULL,
    external_sprint_id BIGINT NOT NULL,
    external_board_id BIGINT NULL,
    name VARCHAR(255) NOT NULL,
    goal TEXT NULL,
    state VARCHAR(100) NOT NULL,
    start_date TIMESTAMPTZ NULL,
    end_date TIMESTAMPTZ NULL,
    complete_date TIMESTAMPTZ NULL,
    synced_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_sprint_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_sprint_connection
        FOREIGN KEY (jira_connection_id)
        REFERENCES jira_connection (id),
    CONSTRAINT uk_jira_sprint_workspace_external_sprint
        UNIQUE (workspace_id, external_sprint_id)
);
