package com.willmear.sprint.presentation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideElementMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SaveDeckUseCaseTest {

    private final PresentationDeckRepository presentationDeckRepository = mock(PresentationDeckRepository.class);
    private final PresentationSlideElementMapper presentationSlideElementMapper = new PresentationSlideElementMapper();
    private final PresentationSlideMapper presentationSlideMapper = new PresentationSlideMapper(presentationSlideElementMapper);
    private final PresentationDeckMapper presentationDeckMapper = new PresentationDeckMapper(presentationSlideMapper);
    private final SaveDeckUseCase saveDeckUseCase = new SaveDeckUseCase(presentationDeckRepository, presentationDeckMapper);

    @Test
    void shouldPersistElementPositionsAndFormattingWhenSavingDeck() {
        UUID workspaceId = UUID.randomUUID();
        var existingDeck = new SprintReviewToPresentationDeckMapper().toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var existingEntity = presentationDeckMapper.toEntity(existingDeck);
        var firstSlide = existingDeck.slides().getFirst();
        var firstElement = firstSlide.elements().getFirst();

        UpdateDeckRequest request = new UpdateDeckRequest(
                "Edited deck",
                "Sprint 42",
                existingDeck.status(),
                List.of(new UpdateDeckRequest.UpdateDeckSlideRequest(
                        firstSlide.id(),
                        firstSlide.slideType(),
                        "Edited title slide",
                        firstSlide.bulletPoints(),
                        firstSlide.bodyText(),
                        "Presenter note",
                        firstSlide.sectionLabel(),
                        firstSlide.layoutType(),
                        List.of(new UpdateDeckRequest.UpdateDeckSlideElementRequest(
                                firstElement.id(),
                                firstElement.elementType(),
                                firstElement.role(),
                                "Updated text box",
                                180.0,
                                96.0,
                                520.0,
                                144.0,
                                "Georgia",
                                30,
                                true,
                                true,
                                TextAlignment.CENTER
                        )),
                        false
                ))
        );

        when(presentationDeckRepository.findByIdAndWorkspaceId(existingDeck.id(), workspaceId)).thenReturn(Optional.of(existingEntity));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var savedDeck = saveDeckUseCase.save(workspaceId, existingDeck.id(), request);

        assertThat(savedDeck.title()).isEqualTo("Edited deck");
        assertThat(savedDeck.slides()).hasSize(1);
        var savedSlide = savedDeck.slides().getFirst();
        assertThat(savedSlide.title()).isEqualTo("Edited title slide");
        assertThat(savedSlide.speakerNotes()).isEqualTo("Presenter note");
        assertThat(savedSlide.elements()).hasSize(1);

        var savedElement = savedSlide.elements().getFirst();
        assertThat(savedElement.textContent()).isEqualTo("Updated text box");
        assertThat(savedElement.x()).isEqualTo(180.0);
        assertThat(savedElement.y()).isEqualTo(96.0);
        assertThat(savedElement.width()).isEqualTo(520.0);
        assertThat(savedElement.height()).isEqualTo(144.0);
        assertThat(savedElement.fontFamily()).isEqualTo("Georgia");
        assertThat(savedElement.fontSize()).isEqualTo(30);
        assertThat(savedElement.bold()).isTrue();
        assertThat(savedElement.italic()).isTrue();
        assertThat(savedElement.textAlignment()).isEqualTo(TextAlignment.CENTER);
    }
}
