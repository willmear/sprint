create index idx_presentation_slide_element_slide_order
    on presentation_slide_element(slide_id, element_order);

alter table presentation_slide_element
    add constraint uk_presentation_slide_element_slide_order unique (slide_id, element_order);
