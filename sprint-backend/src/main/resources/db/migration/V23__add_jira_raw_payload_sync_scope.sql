ALTER TABLE jira_raw_payload
    ADD COLUMN sync_scope_type VARCHAR(100) NULL,
    ADD COLUMN sync_scope_reference VARCHAR(255) NULL;

COMMENT ON COLUMN jira_raw_payload.sync_scope_type IS 'Groups raw payloads by sync scope such as SPRINT for safe re-sync replacement.';
COMMENT ON COLUMN jira_raw_payload.sync_scope_reference IS 'External identifier of the sync scope, such as the external sprint id.';
