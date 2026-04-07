package com.willmear.sprint.jira.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraChangelogDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraCommentDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueFieldsDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraUserDto;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AtlassianJiraClient implements JiraClientPort, JiraRestClient {

    @Override
    public JiraAccountSummary getCurrentAccount(JiraConnection connection) {
        String displayName = connection.externalAccountDisplayName() != null
                ? connection.externalAccountDisplayName()
                : "Connected Jira User";
        String accountId = connection.externalAccountId() != null
                ? connection.externalAccountId()
                : "placeholder-account-id";
        String email = connection.clientEmailOrUsername() != null
                ? connection.clientEmailOrUsername()
                : "jira-user@example.com";
        return new JiraAccountSummary(accountId, displayName, email);
    }

    @Override
    public JiraConnectionTestResult testConnection(JiraConnection connection) {
        JiraAccountSummary accountSummary = getCurrentAccount(connection);
        boolean hasToken = connection.encryptedAccessToken() != null && !connection.encryptedAccessToken().isBlank();
        String message = hasToken
                ? "Placeholder Jira connection test succeeded."
                : "Placeholder Jira connection test requires an access token.";
        return new JiraConnectionTestResult(hasToken, message, accountSummary);
    }

    @Override
    public ExternalJiraBoardDto fetchBoard(JiraConnection connection, Long boardId) {
        return new ExternalJiraBoardDto(boardId, "Sprint Board " + boardId, "scrum", "SPR");
    }

    @Override
    public ExternalJiraSprintDto fetchSprint(JiraConnection connection, Long sprintId) {
        Instant now = Instant.now();
        return new ExternalJiraSprintDto(
                sprintId,
                1001L,
                "Sprint " + sprintId,
                "Placeholder sprint sync goal",
                "active",
                now.minus(7, ChronoUnit.DAYS),
                now.plus(7, ChronoUnit.DAYS),
                null
        );
    }

    @Override
    public List<ExternalJiraIssueDto> fetchSprintIssues(JiraConnection connection, Long sprintId) {
        Instant now = Instant.now();
        return List.of(
                new ExternalJiraIssueDto(
                        "10001",
                        "SPR-" + sprintId + "-1",
                        new ExternalJiraIssueFieldsDto(
                                "Initial sync scaffolding",
                                "Placeholder description for sprint sync scaffolding.",
                                "Story",
                                "In Progress",
                                "High",
                                new ExternalJiraUserDto("assignee-1", "Engineer One", "engineer.one@example.com"),
                                new ExternalJiraUserDto("reporter-1", "Product Owner", "po@example.com"),
                                5,
                                now.minus(10, ChronoUnit.DAYS),
                                now.minus(1, ChronoUnit.DAYS)
                        )
                ),
                new ExternalJiraIssueDto(
                        "10002",
                        "SPR-" + sprintId + "-2",
                        new ExternalJiraIssueFieldsDto(
                                "Persist raw payloads",
                                "Placeholder task for raw payload storage.",
                                "Task",
                                "Done",
                                "Medium",
                                new ExternalJiraUserDto("assignee-2", "Engineer Two", "engineer.two@example.com"),
                                new ExternalJiraUserDto("reporter-1", "Product Owner", "po@example.com"),
                                3,
                                now.minus(8, ChronoUnit.DAYS),
                                now.minus(2, ChronoUnit.DAYS)
                        )
                )
        );
    }

    @Override
    public List<ExternalJiraCommentDto> fetchIssueComments(JiraConnection connection, String issueKey) {
        Instant now = Instant.now();
        return List.of(
                new ExternalJiraCommentDto(
                        issueKey + "-comment-1",
                        issueKey,
                        new ExternalJiraUserDto("commenter-1", "Team Member", "team.member@example.com"),
                        "Placeholder comment fetched from Jira for " + issueKey,
                        now.minus(3, ChronoUnit.DAYS),
                        now.minus(3, ChronoUnit.DAYS)
                )
        );
    }

    @Override
    public List<ExternalJiraChangelogDto> fetchIssueChangelog(JiraConnection connection, String issueKey) {
        Instant now = Instant.now();
        return List.of(
                new ExternalJiraChangelogDto(
                        issueKey + "-history-1",
                        issueKey,
                        "status",
                        "To Do",
                        "In Progress",
                        now.minus(4, ChronoUnit.DAYS),
                        new ExternalJiraUserDto("changer-1", "Scrum Master", "scrum.master@example.com")
                )
        );
    }

    @Override
    public JsonNode get(String path) {
        // TODO: Execute authenticated Jira REST calls and capture raw HTTP responses.
        return null;
    }
}
