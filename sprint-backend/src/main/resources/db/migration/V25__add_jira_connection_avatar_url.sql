ALTER TABLE jira_connection
    ADD COLUMN IF NOT EXISTS external_account_avatar_url VARCHAR(1024) NULL;
