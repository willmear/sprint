CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_account_id VARCHAR(255) NOT NULL,
    email VARCHAR(320) NULL,
    display_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(1024) NULL,
    auth_provider VARCHAR(50) NOT NULL,
    last_login_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_app_user_provider_external_account UNIQUE (auth_provider, external_account_id)
);

CREATE TABLE app_session (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    session_token_hash VARCHAR(128) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    invalidated_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_session_user
        FOREIGN KEY (user_id)
        REFERENCES app_user (id)
        ON DELETE CASCADE,
    CONSTRAINT uk_app_session_token_hash UNIQUE (session_token_hash)
);

CREATE INDEX idx_app_session_user_id ON app_session (user_id);
CREATE INDEX idx_app_session_expires_at ON app_session (expires_at);

CREATE TABLE auth_login_state (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state VARCHAR(255) NOT NULL,
    post_login_redirect_uri VARCHAR(1024) NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    consumed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_auth_login_state_state UNIQUE (state)
);

CREATE INDEX idx_auth_login_state_expires_at ON auth_login_state (expires_at);

ALTER TABLE workspace
    ADD COLUMN owner_user_id UUID NULL;

ALTER TABLE workspace
    ADD CONSTRAINT fk_workspace_owner_user
        FOREIGN KEY (owner_user_id)
        REFERENCES app_user (id);

CREATE INDEX idx_workspace_owner_user_id ON workspace (owner_user_id);
