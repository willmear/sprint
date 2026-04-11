package com.willmear.sprint.jobs.processor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.willmear.sprint.api.request.GenerateSprintReviewRequest;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobStatus;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.sprintreview.application.GenerateSprintReviewUseCase;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerateSprintReviewJobProcessorTest {

    @Mock
    private GenerateSprintReviewUseCase generateSprintReviewUseCase;

    @Test
    void shouldMapForceRegenerateFromJobPayload() {
        GenerateSprintReviewJobProcessor processor = new GenerateSprintReviewJobProcessor(
                generateSprintReviewUseCase,
                new ObjectMapper()
        );
        UUID workspaceId = UUID.randomUUID();
        ObjectNode payload = new ObjectMapper().createObjectNode();
        payload.put("workspaceId", workspaceId.toString());
        payload.put("externalSprintId", 42L);
        payload.put("includeComments", true);
        payload.put("includeChangelog", false);
        payload.put("forceRegenerate", false);
        payload.put("audience", "leadership");
        payload.put("tone", "concise");

        Job job = new Job(
                UUID.randomUUID(),
                workspaceId,
                JobType.GENERATE_SPRINT_REVIEW,
                JobStatus.PENDING,
                "default",
                payload,
                0,
                3,
                Instant.now(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                Instant.now(),
                Instant.now()
        );

        processor.process(job);

        ArgumentCaptor<GenerateSprintReviewRequest> requestCaptor = ArgumentCaptor.forClass(GenerateSprintReviewRequest.class);
        verify(generateSprintReviewUseCase).generate(eq(workspaceId), eq(42L), requestCaptor.capture(), eq("JOB"));
        assertThat(requestCaptor.getValue().forceRegenerate()).isFalse();
    }
}
