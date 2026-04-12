alter table presentation_slide
    add column if not exists template_type varchar(50);
