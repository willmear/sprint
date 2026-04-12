package com.willmear.sprint.presentation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.presentation.api.request.AddSlideRequest;
import com.willmear.sprint.presentation.domain.SlideLayoutType;
import com.willmear.sprint.presentation.domain.SlideType;
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

class AddSlideUseCaseTest {

    private final ThemeRegistry themeRegistry = new ThemeRegistry("corporate-clean");
    private final PresentationDeckRepository presentationDeckRepository = mock(PresentationDeckRepository.class);
    private final PresentationSlideElementMapper presentationSlideElementMapper = new PresentationSlideElementMapper();
    private final PresentationSlideMapper presentationSlideMapper = new PresentationSlideMapper(presentationSlideElementMapper);
    private final PresentationDeckMapper presentationDeckMapper = new PresentationDeckMapper(presentationSlideMapper, themeRegistry);
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
    private final AddSlideUseCase addSlideUseCase = new AddSlideUseCase(presentationDeckRepository, presentationDeckMapper);

    @Test
    void shouldAssignZIndexForDefaultElementsWhenAddingSlide() {
        UUID workspaceId = UUID.randomUUID();
        var deck = sprintReviewToPresentationDeckMapper.toDeck(
                TestSprintReviewFactory.artifact(),
                TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")
        );
        var entity = presentationDeckMapper.toEntity(deck);
        entity.setId(deck.id());

        when(presentationDeckRepository.findByIdAndWorkspaceId(deck.id(), workspaceId)).thenReturn(Optional.of(entity));
        when(presentationDeckRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var updatedDeck = addSlideUseCase.add(
                workspaceId,
                deck.id(),
                new AddSlideRequest(SlideType.CUSTOM, "New slide", null, SlideLayoutType.TITLE_AND_BULLETS)
        );

        var addedSlide = updatedDeck.slides().getLast();
        assertThat(addedSlide.elements())
                .extracting(element -> element.zIndex(), element -> element.elementOrder())
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple(0, 0),
                        org.assertj.core.groups.Tuple.tuple(1, 1)
                );
    }
}
