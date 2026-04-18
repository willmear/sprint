package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraOAuthStateRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraRawPayloadRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraSprintRepository;
import com.willmear.sprint.retrieval.infrastructure.repository.EmbeddingDocumentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveJiraConnectionUseCase {

    private final GetJiraConnectionUseCase getJiraConnectionUseCase;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final JiraSprintRepository jiraSprintRepository;
    private final JiraIssueRepository jiraIssueRepository;
    private final JiraCommentRepository jiraCommentRepository;
    private final JiraChangelogEventRepository jiraChangelogEventRepository;
    private final JiraRawPayloadRepository jiraRawPayloadRepository;
    private final JiraOAuthStateRepository jiraOAuthStateRepository;
    private final EmbeddingDocumentRepository embeddingDocumentRepository;

    public RemoveJiraConnectionUseCase(
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            JiraSprintRepository jiraSprintRepository,
            JiraIssueRepository jiraIssueRepository,
            JiraCommentRepository jiraCommentRepository,
            JiraChangelogEventRepository jiraChangelogEventRepository,
            JiraRawPayloadRepository jiraRawPayloadRepository,
            JiraOAuthStateRepository jiraOAuthStateRepository,
            EmbeddingDocumentRepository embeddingDocumentRepository
    ) {
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.jiraSprintRepository = jiraSprintRepository;
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraCommentRepository = jiraCommentRepository;
        this.jiraChangelogEventRepository = jiraChangelogEventRepository;
        this.jiraRawPayloadRepository = jiraRawPayloadRepository;
        this.jiraOAuthStateRepository = jiraOAuthStateRepository;
        this.embeddingDocumentRepository = embeddingDocumentRepository;
    }

    @Transactional
    public void remove(UUID workspaceId, UUID connectionId) {
        JiraConnection connection = getJiraConnectionUseCase.get(workspaceId, connectionId);
        if (!isRemovable(connection.status())) {
            throw new BadRequestException("Only revoked or failed Jira connections can be removed.");
        }

        deleteDependentData(connectionId);
        jiraOAuthStateRepository.deleteByConnection_Id(connectionId);
        jiraRawPayloadRepository.deleteByJiraConnection_Id(connectionId);
        jiraConnectionRepositoryPort.deleteByIdAndWorkspaceId(connectionId, workspaceId);
    }

    private boolean isRemovable(JiraConnectionStatus status) {
        return status == JiraConnectionStatus.REVOKED || status == JiraConnectionStatus.FAILED;
    }

    private void deleteDependentData(UUID connectionId) {
        jiraCommentRepository.deleteByJiraIssue_JiraConnection_Id(connectionId);
        jiraChangelogEventRepository.deleteByJiraIssue_JiraConnection_Id(connectionId);
        jiraIssueRepository.deleteByJiraConnection_Id(connectionId);
        jiraSprintRepository.deleteByJiraConnection_Id(connectionId);
        embeddingDocumentRepository.deleteByJiraConnectionId(connectionId);
    }
}
