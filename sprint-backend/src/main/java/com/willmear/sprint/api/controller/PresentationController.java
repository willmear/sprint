package com.willmear.sprint.api.controller;

import com.willmear.sprint.presentation.api.PresentationDeckService;
import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.api.request.UpdateSlideRequest;
import com.willmear.sprint.presentation.api.response.PresentationDeckResponse;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}")
public class PresentationController {

    private final PresentationDeckService presentationDeckService;
    private final PresentationDeckMapper presentationDeckMapper;

    public PresentationController(PresentationDeckService presentationDeckService, PresentationDeckMapper presentationDeckMapper) {
        this.presentationDeckService = presentationDeckService;
        this.presentationDeckMapper = presentationDeckMapper;
    }

    @PostMapping("/sprints/{sprintId}/slides/deck")
    public ResponseEntity<PresentationDeckResponse> createOrGetDeck(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                presentationDeckMapper.toResponse(presentationDeckService.createOrGetDeck(workspaceId, sprintId))
        );
    }

    @GetMapping("/sprints/{sprintId}/slides/deck")
    public ResponseEntity<PresentationDeckResponse> getLatestDeck(@PathVariable UUID workspaceId, @PathVariable Long sprintId) {
        return ResponseEntity.ok(presentationDeckMapper.toResponse(
                presentationDeckService.getLatestDeckForSprint(workspaceId, sprintId)
        ));
    }

    @GetMapping("/slides/decks/{deckId}")
    public ResponseEntity<PresentationDeckResponse> getDeck(@PathVariable UUID workspaceId, @PathVariable UUID deckId) {
        return ResponseEntity.ok(presentationDeckMapper.toResponse(presentationDeckService.getDeck(workspaceId, deckId)));
    }

    @PutMapping("/slides/decks/{deckId}")
    public ResponseEntity<PresentationDeckResponse> saveDeck(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @Valid @RequestBody UpdateDeckRequest request
    ) {
        return ResponseEntity.ok(presentationDeckMapper.toResponse(
                presentationDeckService.saveDeck(workspaceId, deckId, request)
        ));
    }

    @PutMapping("/slides/decks/{deckId}/slides/{slideId}")
    public ResponseEntity<PresentationDeckResponse> updateSlide(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @PathVariable UUID slideId,
            @Valid @RequestBody UpdateSlideRequest request
    ) {
        return ResponseEntity.ok(presentationDeckMapper.toResponse(
                presentationDeckService.updateSlide(workspaceId, deckId, slideId, request)
        ));
    }

    @PutMapping("/slides/decks/{deckId}/slides/reorder")
    public ResponseEntity<PresentationDeckResponse> reorderSlides(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @Valid @RequestBody ReorderSlidesRequest request
    ) {
        return ResponseEntity.ok(presentationDeckMapper.toResponse(
                presentationDeckService.reorderSlides(workspaceId, deckId, request)
        ));
    }

    @PostMapping("/slides/decks/{deckId}/slides")
    public ResponseEntity<PresentationDeckResponse> addSlide(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @Valid @RequestBody AddSlideRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                presentationDeckMapper.toResponse(presentationDeckService.addSlide(workspaceId, deckId, request))
        );
    }

    @PostMapping("/slides/decks/{deckId}/slides/{slideId}/duplicate")
    public ResponseEntity<PresentationDeckResponse> duplicateSlide(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @PathVariable UUID slideId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                presentationDeckMapper.toResponse(presentationDeckService.duplicateSlide(workspaceId, deckId, slideId))
        );
    }

    @DeleteMapping("/slides/decks/{deckId}/slides/{slideId}")
    public ResponseEntity<Void> deleteSlide(
            @PathVariable UUID workspaceId,
            @PathVariable UUID deckId,
            @PathVariable UUID slideId
    ) {
        presentationDeckService.deleteSlide(workspaceId, deckId, slideId);
        return ResponseEntity.noContent().build();
    }
}
