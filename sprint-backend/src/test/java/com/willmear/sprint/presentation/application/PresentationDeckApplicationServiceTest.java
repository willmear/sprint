package com.willmear.sprint.presentation.application;

import com.willmear.sprint.TestSprintReviewFactory;
import com.willmear.sprint.artifact.api.ArtifactService;
import com.willmear.sprint.artifact.domain.ArtifactType;
import com.willmear.sprint.artifact.mapper.SprintReviewArtifactMapper;
import com.willmear.sprint.config.OpenAiProperties;
import com.willmear.sprint.config.PresentationAiProperties;
import com.willmear.sprint.export.mapper.ArtifactToSprintReviewMapper;
import com.willmear.sprint.presentation.mapper.PresentationDeckMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideElementMapper;
import com.willmear.sprint.presentation.mapper.PresentationSlideMapper;
import com.willmear.sprint.presentation.mapper.SprintReviewToPresentationDeckMapper;
import com.willmear.sprint.presentation.template.DeckLayoutEngine;
import com.willmear.sprint.presentation.template.SlideTemplateRegistry;
import com.willmear.sprint.presentation.template.SlideTemplateType;
import com.willmear.sprint.presentation.theme.application.PresentationThemeApplicationService;
import com.willmear.sprint.presentation.theme.registry.ThemeRegistry;
import com.willmear.sprint.presentationplan.application.CreatePresentationPlanFromSprintReviewUseCase;
import com.willmear.sprint.presentationplan.application.GeneratePresentationPlanUseCase;
import com.willmear.sprint.presentationplan.application.PresentationPlanApplicationService;
import com.willmear.sprint.presentationplan.mapper.PresentationPlanToPresentationDeckMapper;
import com.willmear.sprint.presentationplan.mapper.SprintReviewToPresentationPlanMapper;
import com.willmear.sprint.presentation.repository.PresentationDeckRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PresentationDeckApplicationServiceTest {

    private final PresentationDeckRepository presentationDeckRepository = mock(PresentationDeckRepository.class);
    private final ArtifactService artifactService = mock(ArtifactService.class);
    private final PresentationSlideElementMapper presentationSlideElementMapper = new PresentationSlideElementMapper();
    private final PresentationSlideMapper presentationSlideMapper = new PresentationSlideMapper(presentationSlideElementMapper);
    private final ThemeRegistry themeRegistry = new ThemeRegistry("corporate-clean");
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
    private final PresentationPlanToPresentationDeckMapper presentationPlanToPresentationDeckMapper =
            new PresentationPlanToPresentationDeckMapper(
                    new DeckLayoutEngine(new SlideTemplateRegistry(), new PresentationThemeApplicationService(themeRegistry))
            );
    private final SprintReviewArtifactMapper sprintReviewArtifactMapper = new SprintReviewArtifactMapper(
            new com.fasterxml.jackson.databind.ObjectMapper().findAndRegisterModules(),
            new com.willmear.sprint.artifact.application.support.SprintReviewMarkdownRenderer()
    );
    private final ArtifactToSprintReviewMapper artifactToSprintReviewMapper = new ArtifactToSprintReviewMapper(sprintReviewArtifactMapper);
    private final CreateDeckFromSprintReviewUseCase createDeckFromSprintReviewUseCase = new CreateDeckFromSprintReviewUseCase(
            presentationDeckRepository,
            presentationDeckMapper,
            artifactService,
            artifactToSprintReviewMapper,
            new SprintReviewToPresentationDeckMapper(
                    presentationPlanApplicationService,
                    presentationPlanToPresentationDeckMapper
            )
    );

    @Test
    void shouldReturnExistingDeckWhenPresent() {
        UUID workspaceId = UUID.randomUUID();
        when(presentationDeckRepository.findFirstByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByUpdatedAtDesc(
                workspaceId,
                SprintReviewToPresentationDeckMapper.SPRINT_REFERENCE_TYPE,
                "42"
        )).thenReturn(Optional.of(presentationDeckMapper.toEntity(new SprintReviewToPresentationDeckMapper(
                presentationPlanApplicationService,
                presentationPlanToPresentationDeckMapper
        ).toDeck(TestSprintReviewFactory.artifact(), TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT")))));

        var deck = createDeckFromSprintReviewUseCase.createOrGet(workspaceId, 42L);

        assertThat(deck.referenceId()).isEqualTo("42");
        assertThat(deck.slides()).isNotEmpty();
    }

    @Test
    void shouldCreateDeckFromLatestSprintReviewArtifact() {
        UUID workspaceId = UUID.randomUUID();
        var artifact = sprintReviewArtifactMapper.toArtifact(TestSprintReviewFactory.review(workspaceId, 42L, "DIRECT"));
        when(presentationDeckRepository.findFirstByWorkspaceIdAndReferenceTypeAndReferenceIdOrderByUpdatedAtDesc(
                workspaceId,
                SprintReviewToPresentationDeckMapper.SPRINT_REFERENCE_TYPE,
                "42"
        )).thenReturn(Optional.empty());
        when(artifactService.getLatest(
                eq(workspaceId),
                eq(ArtifactType.SPRINT_REVIEW),
                eq(SprintReviewArtifactMapper.SPRINT_REFERENCE_TYPE),
                eq("42")
        )).thenReturn(artifact);
        when(presentationDeckRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        var deck = createDeckFromSprintReviewUseCase.createOrGet(workspaceId, 42L);

        assertThat(deck.title()).contains("Sprint Review Deck");
        assertThat(deck.slides()).hasSize(9);
        assertThat(deck.slides().stream().filter(slide -> slide.templateType() == SlideTemplateType.SECTION_DIVIDER)).hasSize(3);
    }
}
