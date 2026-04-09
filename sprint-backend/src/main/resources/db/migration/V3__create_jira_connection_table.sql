CREATE TABLE jira_connection (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    base_url VARCHAR(512) NOT NULL,
    client_email_or_username VARCHAR(255) NULL,
    encrypted_api_token TEXT NULL,
    auth_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_tested_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_connection_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id)
);

COMMENT ON COLUMN jira_connection.encrypted_api_token IS 'TODO: add encryption for Jira credentials before storing production secrets.';

