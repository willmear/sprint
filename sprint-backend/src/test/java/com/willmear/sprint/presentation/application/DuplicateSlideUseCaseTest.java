package com.willmear.sprint.presentation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;

class DuplicateSlideUseCaseTest {

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
    private final DuplicateSlideUseCase duplicateSlideUseCase = new DuplicateSlideUseCase(presentationDeckRepository, presentationDeckMapper);

    @Test
    void shouldDuplicateSlideAndPreserveUniqueSlideOrder() {
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
        var sourceSlide = entity.getSlides().get(1);

        when(presentationDeckRepository.findByIdAndWorkspaceId(deck.id(), workspaceId)).thenReturn(Optional.of(entity));
        when(presentationDeckRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var duplicatedDeck = duplicateSlideUseCase.duplicate(workspaceId, deck.id(), sourceSlide.getId());

        assertThat(duplicatedDeck.slides()).hasSize(deck.slides().size() + 1);
        assertThat(duplicatedDeck.slides())
                .extracting(slide -> slide.slideOrder())
                .containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertThat(duplicatedDeck.slides().get(2).title()).isEqualTo(duplicatedDeck.slides().get(1).title() + " copy");
        assertThat(duplicatedDeck.slides().get(2).elements()).hasSize(duplicatedDeck.slides().get(1).elements().size());
    }
}
