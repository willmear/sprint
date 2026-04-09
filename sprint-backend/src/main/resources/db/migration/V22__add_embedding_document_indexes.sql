CREATE INDEX idx_embedding_document_workspace_id ON embedding_document (workspace_id);
CREATE INDEX idx_embedding_document_external_sprint_id ON embedding_document (external_sprint_id);
CREATE INDEX idx_embedding_document_source_type ON embedding_document (source_type);
CREATE INDEX idx_embedding_document_source_id ON embedding_document (source_id);
CREATE INDEX idx_embedding_document_source_key ON embedding_document (source_key);
CREATE INDEX idx_embedding_document_workspace_sprint_source
    ON embedding_document (workspace_id, external_sprint_id, source_type);
CREATE UNIQUE INDEX idx_embedding_document_source_chunk
    ON embedding_document (workspace_id, source_type, source_id, chunk_index);
