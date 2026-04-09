DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'jira_connection'
          AND column_name = 'encrypted_api_token'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'jira_connection'
          AND column_name = 'encrypted_access_token'
    ) THEN
        ALTER TABLE jira_connection
            RENAME COLUMN encrypted_api_token TO encrypted_access_token;
    END IF;
END $$;

ALTER TABLE jira_connection
    ADD COLUMN IF NOT EXISTS encrypted_access_token TEXT,
    ADD COLUMN IF NOT EXISTS encrypted_refresh_token TEXT,
    ADD COLUMN IF NOT EXISTS token_expires_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS external_account_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS external_account_display_name VARCHAR(255);

UPDATE jira_connection
SET auth_type = CASE auth_type
                    WHEN 'API_TOKEN' THEN 'API_TOKEN'
                    ELSE 'OAUTH'
                END,
    status = CASE status
                 WHEN 'ACTIVE' THEN 'ACTIVE'
                 WHEN 'FAILED' THEN 'FAILED'
                 WHEN 'REVOKED' THEN 'REVOKED'
                 ELSE 'PENDING_AUTHORIZATION'
             END;
