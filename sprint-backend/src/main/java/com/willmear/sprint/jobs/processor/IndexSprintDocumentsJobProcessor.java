package com.willmear.sprint.jobs.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.jobs.domain.Job;
import com.willmear.sprint.jobs.domain.JobExecutionResult;
import com.willmear.sprint.jobs.domain.JobType;
import com.willmear.sprint.retrieval.application.IndexSprintDocumentsJobPayload;
import com.willmear.sprint.retrieval.application.IndexSprintDocumentsUseCase;
import org.springframework.stereotype.Component;

@Component
public class IndexSprintDocumentsJobProcessor implements JobProcessor {

    private final IndexSprintDocumentsUseCase indexSprintDocumentsUseCase;
    private final ObjectMapper objectMapper;

    public IndexSprintDocumentsJobProcessor(
            IndexSprintDocumentsUseCase indexSprintDocumentsUseCase,
            ObjectMapper objectMapper
    ) {
        this.indexSprintDocumentsUseCase = indexSprintDocumentsUseCase;
        this.objectMapper = objectMapper;
    }

    @Override
    public JobType supports() {
        return JobType.INDEX_SPRINT_DOCUMENTS;
    }

    @Override
    public JobExecutionResult process(Job job) {
        IndexSprintDocumentsJobPayload payload = objectMapper.convertValue(job.payload(), IndexSprintDocumentsJobPayload.class);
        indexSprintDocumentsUseCase.index(
                payload.workspaceId(),
                payload.externalSprintId(),
                payload.includeComments(),
                payload.includeSprintSummary(),
                payload.forceReindex()
        );
        return JobExecutionResult.success("INDEX_SPRINT_DOCUMENTS job completed.");
    }
}
