CREATE TABLE jira_board (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL,
    external_board_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    board_type VARCHAR(100) NULL,
    project_key VARCHAR(100) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_jira_board_workspace
        FOREIGN KEY (workspace_id)
        REFERENCES workspace (id)
);
