CREATE TABLE jira_raw_payload (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    jira_connection_id UUID NOT NULL,
    payload_type VARCHAR(100) NOT NULL,
    external_reference VARCHAR(255) NULL,
    payload TEXT NOT NULL,
    fetched_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_raw_payload_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id),
    CONSTRAINT fk_jira_raw_payload_connection
        FOREIGN KEY (jira_connection_id)
        REFERENCES jira_connection (id)
);

COMMENT ON COLUMN jira_raw_payload.payload IS 'TODO: refine to JSONB and capture exact raw HTTP payloads later.';
