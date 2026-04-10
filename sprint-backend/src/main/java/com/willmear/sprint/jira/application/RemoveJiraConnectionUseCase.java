package com.willmear.sprint.jira.application;

import com.willmear.sprint.common.exception.BadRequestException;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
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
    private final JiraRawPayloadRepository jiraRawPayloadRepository;
    private final JiraOAuthStateRepository jiraOAuthStateRepository;
    private final EmbeddingDocumentRepository embeddingDocumentRepository;

    public RemoveJiraConnectionUseCase(
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            JiraSprintRepository jiraSprintRepository,
            JiraIssueRepository jiraIssueRepository,
            JiraRawPayloadRepository jiraRawPayloadRepository,
            JiraOAuthStateRepository jiraOAuthStateRepository,
            EmbeddingDocumentRepository embeddingDocumentRepository
    ) {
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.jiraSprintRepository = jiraSprintRepository;
        this.jiraIssueRepository = jiraIssueRepository;
        this.jiraRawPayloadRepository = jiraRawPayloadRepository;
        this.jiraOAuthStateRepository = jiraOAuthStateRepository;
        this.embeddingDocumentRepository = embeddingDocumentRepository;
    }

    @Transactional
    public void remove(UUID workspaceId, UUID connectionId) {
        JiraConnection connection = getJiraConnectionUseCase.get(workspaceId, connectionId);
        if (connection.status() != JiraConnectionStatus.REVOKED) {
            throw new BadRequestException("Only revoked Jira connections can be removed.");
        }
        if (hasDependentData(connectionId)) {
            throw new BadRequestException("This Jira connection still has synced sprint data and cannot be removed yet.");
        }

        jiraOAuthStateRepository.deleteByConnection_Id(connectionId);
        jiraRawPayloadRepository.deleteByJiraConnection_Id(connectionId);
        jiraConnectionRepositoryPort.deleteByIdAndWorkspaceId(connectionId, workspaceId);
    }

    private boolean hasDependentData(UUID connectionId) {
        return jiraSprintRepository.existsByJiraConnection_Id(connectionId)
                || jiraIssueRepository.existsByJiraConnection_Id(connectionId)
                || embeddingDocumentRepository.existsByJiraConnectionId(connectionId);
    }
}
