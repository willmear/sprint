package com.willmear.sprint.common.exception;

import java.util.UUID;

public class PresentationSlideNotFoundException extends NotFoundException {

    public PresentationSlideNotFoundException(UUID deckId, UUID slideId) {
        super("Presentation slide " + slideId + " was not found in deck " + deckId + ".");
    }
}
