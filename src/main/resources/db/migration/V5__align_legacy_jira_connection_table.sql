DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'jira_connection'
          AND column_name = 'username'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'jira_connection'
          AND column_name = 'client_email_or_username'
    ) THEN
        ALTER TABLE jira_connection
            RENAME COLUMN username TO client_email_or_username;
    END IF;
END $$;

ALTER TABLE jira_connection
    ADD COLUMN IF NOT EXISTS client_email_or_username VARCHAR(255),
    ADD COLUMN IF NOT EXISTS auth_type VARCHAR(50),
    ADD COLUMN IF NOT EXISTS status VARCHAR(50),
    ADD COLUMN IF NOT EXISTS last_tested_at TIMESTAMPTZ;

UPDATE jira_connection
SET auth_type = COALESCE(auth_type, 'API_TOKEN'),
    status = COALESCE(status, 'PENDING_VALIDATION')
WHERE auth_type IS NULL
   OR status IS NULL;

ALTER TABLE jira_connection
    ALTER COLUMN auth_type SET NOT NULL,
    ALTER COLUMN status SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'jira_connection'
          AND constraint_name = 'fk_jira_connection_workspace'
    ) THEN
        ALTER TABLE jira_connection
            ADD CONSTRAINT fk_jira_connection_workspace
            FOREIGN KEY (workspace_id)
            REFERENCES workspace (id);
    END IF;
END $$;
