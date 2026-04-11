create table presentation_slide (
    id uuid primary key,
    deck_id uuid not null references presentation_deck(id) on delete cascade,
    slide_order integer not null,
    slide_type varchar(50) not null,
    title varchar(255) not null,
    bullet_points text not null,
    body_text text,
    speaker_notes text,
    section_label varchar(100),
    layout_type varchar(50) not null,
    hidden boolean not null default false,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
