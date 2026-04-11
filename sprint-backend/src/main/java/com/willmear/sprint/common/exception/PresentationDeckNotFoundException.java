package com.willmear.sprint.common.exception;

import java.util.UUID;

public class PresentationDeckNotFoundException extends NotFoundException {

    public PresentationDeckNotFoundException(UUID workspaceId, UUID deckId) {
        super("Presentation deck " + deckId + " was not found for workspace " + workspaceId + ".");
    }

    public PresentationDeckNotFoundException(UUID workspaceId, String sprintReferenceId) {
        super("Presentation deck was not found for workspace " + workspaceId + " and sprint " + sprintReferenceId + ".");
    }
}
