create table presentation_deck (
    id uuid primary key,
    workspace_id uuid not null references workspace(id),
    reference_type varchar(100) not null,
    reference_id varchar(255) not null,
    title varchar(255) not null,
    subtitle varchar(255),
    status varchar(50) not null,
    source_artifact_id uuid references artifact(id),
    created_at timestamptz not null,
    updated_at timestamptz not null
);
