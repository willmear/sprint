package com.willmear.sprint.presentation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentation.api.request.ReorderSlidesRequest;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideElementMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import com.willmear.sprint.presentation.template.DeckLayoutEngine;
import com.willmear.sprint.presentation.template.SlideTemplateRegistry;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import com.willmear.sprint.presentationplan.application.CreatePresentationPlanFromSprintReviewUseCase;
import com.willmear.sprint.presentationplan.application.GeneratePresentationPlanUseCase;
import com.willmear.sprint.presentationplan.application.PresentationPlanApplicationService;
import com.willmear.sprint.presentationplan.mapper.PresentationPlanToPresentationDeckMapper;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReorderSlidesUseCaseTest {

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
    private final ReorderSlidesUseCase reorderSlidesUseCase = new ReorderSlidesUseCase(presentationDeckRepository, presentationDeckMapper);

    @Test
    void shouldReorderSlidesWithoutViolatingUniqueDeckOrderConstraint() {
        UUID workspaceId = UUID.randomUUID();
        var deck = sprintReviewToPresentationDeckMapper.toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var entity = presentationDeckMapper.toEntity(deck);
        entity.setId(deck.id());
        entity.getSlides().forEach(slide -> {
            slide.setId(UUID.randomUUID());
            slide.getElements().forEach(element -> element.setId(UUID.randomUUID()));
        });

        List<UUID> reorderedIds = List.of(
                entity.getSlides().get(0).getId(),
                entity.getSlides().get(2).getId(),
                entity.getSlides().get(1).getId(),
                entity.getSlides().get(3).getId(),
                entity.getSlides().get(4).getId(),
                entity.getSlides().get(5).getId(),
                entity.getSlides().get(6).getId(),
                entity.getSlides().get(7).getId(),
                entity.getSlides().get(8).getId()
        );

        when(presentationDeckRepository.findByIdAndWorkspaceId(deck.id(), workspaceId)).thenReturn(Optional.of(entity));
        when(presentationDeckRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var reorderedDeck = reorderSlidesUseCase.reorder(workspaceId, deck.id(), new ReorderSlidesRequest(reorderedIds));
        var orderedSlides = reorderedDeck.slides().stream()
                .sorted(java.util.Comparator.comparing(slide -> slide.slideOrder()))
                .toList();

        assertThat(orderedSlides)
                .extracting(slide -> slide.slideOrder())
                .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8);
        assertThat(orderedSlides.get(1).id()).isEqualTo(reorderedIds.get(1));
        assertThat(orderedSlides.get(2).id()).isEqualTo(reorderedIds.get(2));
    }
}
