CREATE INDEX idx_jira_raw_payload_sync_scope
    ON jira_raw_payload (workspace_id, jira_connection_id, sync_scope_type, sync_scope_reference);
