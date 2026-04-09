package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.willmear.sprint.jira.domain.model.JiraIssue;
import com.willmear.sprint.jira.infrastructure.entity.JiraIssueEntity;
import com.willmear.sprint.jira.infrastructure.persistence.JpaJiraIssueRepositoryAdapter;
import com.willmear.sprint.jira.infrastructure.repository.JiraChangelogEventRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraCommentRepository;
import com.willmear.sprint.jira.infrastructure.repository.JiraIssueRepository;
import com.willmear.sprint.jira.mapper.JiraIssueMapper;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JpaJiraIssueRepositoryAdapterTest {

    @Mock
    private JiraIssueRepository jiraIssueRepository;
    @Mock
    private JiraCommentRepository jiraCommentRepository;
    @Mock
    private JiraChangelogEventRepository jiraChangelogEventRepository;
    @Mock
    private JiraIssueMapper jiraIssueMapper;
    @InjectMocks
    private JpaJiraIssueRepositoryAdapter adapter;

    @Test
    void shouldFlushDeletesBeforeSavingReplacementIssues() {
        UUID workspaceId = UUID.randomUUID();
        UUID connectionId = UUID.randomUUID();
        UUID sprintEntityId = UUID.randomUUID();
        UUID existingIssueId = UUID.randomUUID();
        JiraIssue issue = issue("SS-1");

        JiraIssueEntity existingEntity = new JiraIssueEntity();
        existingEntity.setId(existingIssueId);

        JiraIssueEntity savedEntity = new JiraIssueEntity();
        savedEntity.setId(UUID.randomUUID());
        savedEntity.setIssueKey("SS-1");

        when(jiraIssueRepository.findByWorkspace_IdAndExternalSprintIdOrderByIssueKeyAsc(workspaceId, 1L))
                .thenReturn(List.of(existingEntity));
        when(jiraIssueMapper.toEntity(workspaceId, connectionId, sprintEntityId, 1L, issue)).thenReturn(new JiraIssueEntity());
        when(jiraIssueRepository.saveAll(any())).thenReturn(List.of(savedEntity));

        Map<String, UUID> result = adapter.replaceForSprint(workspaceId, connectionId, sprintEntityId, 1L, List.of(issue));

        InOrder inOrder = inOrder(jiraCommentRepository, jiraChangelogEventRepository, jiraIssueRepository);
        inOrder.verify(jiraCommentRepository).deleteByJiraIssue_Id(existingIssueId);
        inOrder.verify(jiraChangelogEventRepository).deleteByJiraIssue_Id(existingIssueId);
        inOrder.verify(jiraCommentRepository).flush();
        inOrder.verify(jiraChangelogEventRepository).flush();
        inOrder.verify(jiraIssueRepository).deleteAllInBatch(List.of(existingEntity));
        inOrder.verify(jiraIssueRepository).flush();
        inOrder.verify(jiraIssueRepository).saveAll(any());

        assertThat(result).containsEntry("SS-1", savedEntity.getId());
        verify(jiraIssueMapper).toEntity(workspaceId, connectionId, sprintEntityId, 1L, issue);
    }

    private JiraIssue issue(String issueKey) {
        return new JiraIssue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                issueKey,
                "10001",
                "Summary",
                "Description",
                "Story",
                "In Progress",
                "Medium",
                "Assignee",
                "Reporter",
                3,
                Instant.now(),
                Instant.now()
        );
    }
}
