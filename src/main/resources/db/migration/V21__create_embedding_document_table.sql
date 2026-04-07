CREATE TABLE embedding_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspace (id),
    jira_connection_id UUID NULL REFERENCES jira_connection (id),
    external_sprint_id BIGINT NULL,
    source_type VARCHAR(100) NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    source_key VARCHAR(255) NULL,
    title VARCHAR(255) NULL,
    content TEXT NOT NULL,
    chunk_text TEXT NOT NULL,
    chunk_index INTEGER NOT NULL,
    token_count_estimate INTEGER NULL,
    metadata JSONB NULL,
    embedding VECTOR(1536) NOT NULL,
    indexed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
