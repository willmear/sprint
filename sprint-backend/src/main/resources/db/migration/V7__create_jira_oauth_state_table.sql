CREATE TABLE jira_oauth_state (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    connection_id UUID NULL,
    state VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(1024) NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    consumed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_oauth_state_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_oauth_state_connection
        FOREIGN KEY (connection_id)
        REFERENCES jira_connection (id),
    CONSTRAINT uk_jira_oauth_state_state
        UNIQUE (state)
);
