CREATE TABLE artifact (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    workspace_id UUID NOT NULL REFERENCES workspace (id),
    artifact_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    reference_type VARCHAR(100) NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    structured_content TEXT NULL,
    rendered_markdown TEXT NULL,
    title VARCHAR(255) NULL,
    summary TEXT NULL,
    generator_type VARCHAR(100) NULL,
    generator_version VARCHAR(100) NULL,
    generated_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
