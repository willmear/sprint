package com.willmear.sprint.jira.domain.port;

import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraChangelogDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraCommentDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import java.util.List;

public interface JiraClientPort {

    JiraAccountSummary getCurrentAccount(JiraConnection connection);

    JiraConnectionTestResult testConnection(JiraConnection connection);

    ExternalJiraBoardDto fetchBoard(JiraConnection connection, Long boardId);

    ExternalJiraSprintDto fetchSprint(JiraConnection connection, Long sprintId);

    List<ExternalJiraIssueDto> fetchSprintIssues(JiraConnection connection, Long sprintId);

    List<ExternalJiraCommentDto> fetchIssueComments(JiraConnection connection, String issueKey);

    List<ExternalJiraChangelogDto> fetchIssueChangelog(JiraConnection connection, String issueKey);
}
