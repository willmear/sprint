package com.willmear.sprint.presentation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentation.api.request.UpdateDeckRequest;
import com.willmear.sprint.presentation.domain.TextAlignment;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideElementMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.template.DeckLayoutEngine;
import com.willmear.sprint.presentation.template.SlideTemplateRegistry;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import com.willmear.sprint.presentationplan.application.CreatePresentationPlanFromSprintReviewUseCase;
import com.willmear.sprint.presentationplan.application.GeneratePresentationPlanUseCase;
import com.willmear.sprint.presentationplan.application.PresentationPlanApplicationService;
import com.willmear.sprint.presentationplan.mapper.PresentationPlanToPresentationDeckMapper;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class SaveDeckUseCaseTest {

    private final ThemeRegistry themeRegistry = new ThemeRegistry("corporate-clean");
    private final PresentationPlanApplicationService presentationPlanApplicationService =
            new PresentationPlanApplicationService(
                    new CreatePresentationPlanFromSprintReviewUseCase(
                            new SprintReviewToPresentationPlanMapper(),
                            mock(GeneratePresentationPlanUseCase.class),
                            new OpenAiProperties(false, false, "", "", "", "", "gpt-test", "", Duration.ofSeconds(30), 1200, 0.2),
                            new PresentationAiProperties(false, true)
                    )
            );

    private final SprintReviewToPresentationDeckMapper sprintReviewToPresentationDeckMapper =
            new SprintReviewToPresentationDeckMapper(
                    presentationPlanApplicationService,
                    new PresentationPlanToPresentationDeckMapper(
                            new DeckLayoutEngine(new SlideTemplateRegistry(), new PresentationThemeApplicationService(themeRegistry))
                    )
            );

    private final PresentationDeckRepository presentationDeckRepository = mock(PresentationDeckRepository.class);
    private final PresentationSlideElementMapper presentationSlideElementMapper = new PresentationSlideElementMapper();
    private final PresentationSlideMapper presentationSlideMapper = new PresentationSlideMapper(presentationSlideElementMapper);
    private final PresentationDeckMapper presentationDeckMapper = new PresentationDeckMapper(presentationSlideMapper, themeRegistry);
    private final SaveDeckUseCase saveDeckUseCase = new SaveDeckUseCase(presentationDeckRepository, presentationDeckMapper);

    @Test
    void shouldPersistElementPositionsAndFormattingWhenSavingDeck() {
        UUID workspaceId = UUID.randomUUID();
        var existingDeck = sprintReviewToPresentationDeckMapper.toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var existingEntity = presentationDeckMapper.toEntity(existingDeck);
        var firstSlide = existingDeck.slides().getFirst();
        var firstElement = firstSlide.elements().getFirst();

        UpdateDeckRequest request = new UpdateDeckRequest(
                "Edited deck",
                "Sprint 42",
                "modern-minimal",
                existingDeck.status(),
                List.of(new UpdateDeckRequest.UpdateDeckSlideRequest(
                        firstSlide.id(),
                        firstSlide.slideType(),
                        "Edited title slide",
                        firstSlide.bulletPoints(),
                        firstSlide.bodyText(),
                        "Presenter note",
                        firstSlide.sectionLabel(),
                        firstSlide.backgroundColor(),
                        firstSlide.backgroundStyleType(),
                        firstSlide.showGrid(),
                        firstSlide.layoutType(),
                        firstSlide.templateType(),
                        List.of(new UpdateDeckRequest.UpdateDeckSlideElementRequest(
                                firstElement.id(),
                                firstElement.elementType(),
                                firstElement.role(),
                                "Updated text box",
                                180.0,
                                96.0,
                                520.0,
                                144.0,
                                firstElement.zIndex(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                "Georgia",
                                30,
                                true,
                                true,
                                false,
                                TextAlignment.CENTER,
                                null,
                                false
                        )),
                        false
                ))
        );

        when(presentationDeckRepository.findByIdAndWorkspaceId(existingDeck.id(), workspaceId)).thenReturn(Optional.of(existingEntity));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var savedDeck = saveDeckUseCase.save(workspaceId, existingDeck.id(), request);

        assertThat(savedDeck.title()).isEqualTo("Edited deck");
        assertThat(savedDeck.themeId()).isEqualTo("modern-minimal");
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

    @Test
    void shouldReplaceSlideElementsWithoutMergingDetachedDeletedChildren() {
        UUID workspaceId = UUID.randomUUID();
        var existingDeck = sprintReviewToPresentationDeckMapper.toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var existingEntity = presentationDeckMapper.toEntity(existingDeck);
        var firstSlide = existingDeck.slides().getFirst();
        UUID replacementElementId = UUID.randomUUID();

        UpdateDeckRequest request = new UpdateDeckRequest(
                existingDeck.title(),
                existingDeck.subtitle(),
                existingDeck.themeId(),
                existingDeck.status(),
                List.of(new UpdateDeckRequest.UpdateDeckSlideRequest(
                        firstSlide.id(),
                        firstSlide.slideType(),
                        firstSlide.title(),
                        firstSlide.bulletPoints(),
                        firstSlide.bodyText(),
                        firstSlide.speakerNotes(),
                        firstSlide.sectionLabel(),
                        firstSlide.backgroundColor(),
                        firstSlide.backgroundStyleType(),
                        firstSlide.showGrid(),
                        firstSlide.layoutType(),
                        firstSlide.templateType(),
                        List.of(new UpdateDeckRequest.UpdateDeckSlideElementRequest(
                                replacementElementId,
                                firstSlide.elements().getFirst().elementType(),
                                firstSlide.elements().getFirst().role(),
                                "Replacement text box",
                                240.0,
                                140.0,
                                480.0,
                                128.0,
                                firstSlide.elements().getFirst().zIndex(),
                                null,
                                null,
                                null,
                                null,
                                null,
                                "Aptos",
                                26,
                                false,
                                false,
                                false,
                                TextAlignment.LEFT,
                                null,
                                false
                        )),
                        false
                ))
        );

        when(presentationDeckRepository.findByIdAndWorkspaceId(existingDeck.id(), workspaceId)).thenReturn(Optional.of(existingEntity));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var savedDeck = saveDeckUseCase.save(workspaceId, existingDeck.id(), request);

        assertThat(savedDeck.slides()).hasSize(1);
        assertThat(savedDeck.slides().getFirst().elements()).singleElement().satisfies(element -> {
            assertThat(element.id()).isEqualTo(replacementElementId);
            assertThat(element.textContent()).isEqualTo("Replacement text box");
            assertThat(element.x()).isEqualTo(240.0);
            assertThat(element.y()).isEqualTo(140.0);
        });
    }

    @Test
    void shouldNormalizeElementZOrderWhenSavingDeck() {
        UUID workspaceId = UUID.randomUUID();
        var existingDeck = sprintReviewToPresentationDeckMapper.toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var existingEntity = presentationDeckMapper.toEntity(existingDeck);
        var firstSlide = existingDeck.slides().getFirst();
        var firstElement = firstSlide.elements().getFirst();
        UUID secondElementId = UUID.randomUUID();

        UpdateDeckRequest request = new UpdateDeckRequest(
                existingDeck.title(),
                existingDeck.subtitle(),
                existingDeck.themeId(),
                existingDeck.status(),
                List.of(new UpdateDeckRequest.UpdateDeckSlideRequest(
                        firstSlide.id(),
                        firstSlide.slideType(),
                        firstSlide.title(),
                        firstSlide.bulletPoints(),
                        firstSlide.bodyText(),
                        firstSlide.speakerNotes(),
                        firstSlide.sectionLabel(),
                        firstSlide.backgroundColor(),
                        firstSlide.backgroundStyleType(),
                        firstSlide.showGrid(),
                        firstSlide.layoutType(),
                        firstSlide.templateType(),
                        List.of(
                                new UpdateDeckRequest.UpdateDeckSlideElementRequest(
                                        firstElement.id(),
                                        firstElement.elementType(),
                                        firstElement.role(),
                                        firstElement.textContent(),
                                        firstElement.x(),
                                        firstElement.y(),
                                        firstElement.width(),
                                        firstElement.height(),
                                        5,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        firstElement.fontFamily(),
                                        firstElement.fontSize(),
                                        firstElement.bold(),
                                        firstElement.italic(),
                                        firstElement.underline(),
                                        firstElement.textAlignment(),
                                        firstElement.shapeType(),
                                        firstElement.hidden()
                                ),
                                new UpdateDeckRequest.UpdateDeckSlideElementRequest(
                                        secondElementId,
                                        firstElement.elementType(),
                                        firstElement.role(),
                                        "Second layer",
                                        firstElement.x() + 40,
                                        firstElement.y() + 40,
                                        firstElement.width(),
                                        firstElement.height(),
                                        1,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        firstElement.fontFamily(),
                                        firstElement.fontSize(),
                                        false,
                                        false,
                                        false,
                                        firstElement.textAlignment(),
                                        null,
                                        false
                                )
                        ),
                        false
                ))
        );

        when(presentationDeckRepository.findByIdAndWorkspaceId(existingDeck.id(), workspaceId)).thenReturn(Optional.of(existingEntity));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var savedDeck = saveDeckUseCase.save(workspaceId, existingDeck.id(), request);

        assertThat(savedDeck.slides()).hasSize(1);
        assertThat(savedDeck.slides().getFirst().elements())
                .extracting(element -> element.textContent(), element -> element.zIndex(), element -> element.elementOrder())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("Second layer", 0, 0),
                        org.assertj.core.groups.Tuple.tuple(firstElement.textContent(), 1, 1)
                );
    }
}
