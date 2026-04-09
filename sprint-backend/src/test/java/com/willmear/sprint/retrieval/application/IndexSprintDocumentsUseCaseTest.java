package com.willmear.sprint.retrieval.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.jira.infrastructure.entity.JiraCommentEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraConnectionEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.entity.JiraSprintEntity;
import com.willmear.sprint.observability.logging.LoggingContextHelper;
import com.willmear.sprint.observability.metrics.WorkflowMetricsRecorder;
import com.willmear.sprint.observability.tracing.TraceContextHelper;
import com.willmear.sprint.retrieval.application.support.SprintRetrievalSourceBundle;
import com.willmear.sprint.retrieval.domain.model.IndexingResult;
import com.willmear.sprint.retrieval.domain.port.EmbeddingGeneratorPort;
import com.willmear.sprint.retrieval.domain.port.EmbeddingStorePort;
import com.willmear.sprint.retrieval.domain.port.SprintRetrievalSourcePort;
import com.willmear.sprint.retrieval.domain.service.ChunkingService;
import com.willmear.sprint.workspace.entity.WorkspaceEntity;
import com.willmear.sprint.config.RetrievalProperties;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndexSprintDocumentsUseCaseTest {

    private final SprintRetrievalSourcePort sprintRetrievalSourcePort = mock(SprintRetrievalSourcePort.class);
    private final EmbeddingGeneratorPort embeddingGeneratorPort = mock(EmbeddingGeneratorPort.class);
    private final EmbeddingStorePort embeddingStorePort = mock(EmbeddingStorePort.class);
    private final IndexSprintDocumentsUseCase useCase = new IndexSprintDocumentsUseCase(
            sprintRetrievalSourcePort,
            new ChunkingService(new RetrievalProperties(true, true, true, 5, 250, 25, 1536, true)),
            embeddingGeneratorPort,
            embeddingStorePort,
            new ObjectMapper(),
            new LoggingContextHelper(),
            new WorkflowMetricsRecorder(new SimpleMeterRegistry()),
            new TraceContextHelper()
    );

    @Test
    void shouldIndexSprintIssueAndCommentDocuments() {
        UUID workspaceId = UUID.randomUUID();
        when(sprintRetrievalSourcePort.load(workspaceId, 77L, true, true)).thenReturn(bundle());
        when(embeddingGeneratorPort.generateEmbeddings(anyList())).thenAnswer(invocation -> {
            List<String> texts = invocation.getArgument(0);
            return texts.stream().map(text -> List.of(0.1, 0.2, 0.3)).toList();
        });
        when(embeddingStorePort.replaceSprintDocuments(org.mockito.ArgumentMatchers.eq(workspaceId), org.mockito.ArgumentMatchers.eq(77L), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(2);

        IndexingResult result = useCase.index(workspaceId, 77L, true, true, true);

        assertThat(result.indexedDocuments()).isEqualTo(3);
        assertThat(result.deletedDocuments()).isEqualTo(2);
        assertThat(result.message()).contains("reindexed");
        verify(embeddingStorePort).replaceSprintDocuments(org.mockito.ArgumentMatchers.eq(workspaceId), org.mockito.ArgumentMatchers.eq(77L), org.mockito.ArgumentMatchers.argThat(documents ->
                documents.size() == 3
                        && documents.stream().anyMatch(document -> "SPRINT".equals(document.sourceType()))
                        && documents.stream().anyMatch(document -> "ISSUE".equals(document.sourceType()))
                        && documents.stream().anyMatch(document -> "COMMENT".equals(document.sourceType()))
        ));
    }

    private SprintRetrievalSourceBundle bundle() {
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setId(UUID.randomUUID());
        JiraConnectionEntity connection = new JiraConnectionEntity();
        connection.setId(UUID.randomUUID());
        connection.setWorkspace(workspace);

        JiraSprintEntity sprint = new JiraSprintEntity();
        sprint.setId(UUID.randomUUID());
        sprint.setWorkspace(workspace);
        sprint.setJiraConnection(connection);
        sprint.setExternalSprintId(77L);
        sprint.setName("Sprint 77");
        sprint.setGoal("Ship things");
        sprint.setState("ACTIVE");
        sprint.setSyncedAt(Instant.now());

        JiraIssueEntity issue = new JiraIssueEntity();
        issue.setId(UUID.randomUUID());
        issue.setWorkspace(workspace);
        issue.setJiraConnection(connection);
        issue.setJiraSprint(sprint);
        issue.setExternalSprintId(77L);
        issue.setIssueKey("SPR-1");
        issue.setExternalIssueId("100");
        issue.setSummary("Add retrieval");
        issue.setDescription("Introduce semantic retrieval");
        issue.setIssueType("Story");
        issue.setStatus("Done");
        issue.setAssigneeDisplayName("A Developer");

        JiraCommentEntity comment = new JiraCommentEntity();
        comment.setId(UUID.randomUUID());
        comment.setWorkspace(workspace);
        comment.setJiraIssue(issue);
        comment.setExternalCommentId("200");
        comment.setIssueKey("SPR-1");
        comment.setAuthorDisplayName("Reviewer");
        comment.setBody("Blocked briefly by dependency, then resolved.");

        return new SprintRetrievalSourceBundle(sprint, List.of(issue), Map.of("SPR-1", List.of(comment)));
    }
}
