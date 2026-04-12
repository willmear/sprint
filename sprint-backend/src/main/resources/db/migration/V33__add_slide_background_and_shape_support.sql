alter table presentation_slide
    add column if not exists background_color varchar(20),
    add column if not exists background_style_type varchar(30),
    add column if not exists show_grid boolean not null default false;

alter table presentation_slide_element
    add column if not exists z_index integer,
    add column if not exists rotation_degrees double precision,
    add column if not exists fill_color varchar(20),
    add column if not exists border_color varchar(20),
    add column if not exists border_width integer,
    add column if not exists text_color varchar(20),
    add column if not exists is_underline boolean not null default false,
    add column if not exists shape_type varchar(40),
    add column if not exists hidden boolean not null default false;

update presentation_slide_element
set z_index = element_order
where z_index is null;

alter table presentation_slide_element
    alter column z_index set not null;
