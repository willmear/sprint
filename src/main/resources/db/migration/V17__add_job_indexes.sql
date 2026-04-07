CREATE INDEX idx_job_status
    ON job (status);

CREATE INDEX idx_job_available_at
    ON job (available_at);

CREATE INDEX idx_job_job_type
    ON job (job_type);

CREATE INDEX idx_job_workspace_id
    ON job (workspace_id);

CREATE INDEX idx_job_runnable
    ON job (status, available_at);

CREATE INDEX idx_job_polling
    ON job (status, available_at, created_at);
