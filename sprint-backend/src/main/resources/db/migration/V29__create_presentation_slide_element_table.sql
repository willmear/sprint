create table presentation_slide_element (
    id uuid primary key,
    slide_id uuid not null references presentation_slide(id) on delete cascade,
    element_order integer not null,
    element_type varchar(50) not null,
    element_role varchar(50) not null,
    text_content text not null,
    position_x double precision not null,
    position_y double precision not null,
    width_px double precision not null,
    height_px double precision not null,
    font_family varchar(120) not null,
    font_size integer not null,
    is_bold boolean not null default false,
    is_italic boolean not null default false,
    text_alignment varchar(20) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);
