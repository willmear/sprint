create index idx_presentation_deck_workspace_reference
    on presentation_deck(workspace_id, reference_type, reference_id);

create index idx_presentation_slide_deck_order
    on presentation_slide(deck_id, slide_order);

alter table presentation_slide
    add constraint uk_presentation_slide_deck_order unique (deck_id, slide_order);
