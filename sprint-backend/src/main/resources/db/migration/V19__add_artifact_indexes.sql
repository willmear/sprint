CREATE INDEX idx_artifact_workspace_id ON artifact (workspace_id);
CREATE INDEX idx_artifact_type ON artifact (artifact_type);
CREATE INDEX idx_artifact_reference_type ON artifact (reference_type);
CREATE INDEX idx_artifact_reference_id ON artifact (reference_id);
CREATE INDEX idx_artifact_generated_at ON artifact (generated_at DESC);
CREATE INDEX idx_artifact_latest_lookup
    ON artifact (workspace_id, artifact_type, reference_type, reference_id, generated_at DESC);
CREATE INDEX idx_artifact_reference_lookup
    ON artifact (workspace_id, reference_type, reference_id);
