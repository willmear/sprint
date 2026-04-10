package com.willmear.sprint.jira.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.jira.domain.model.AvailableJiraSprint;
import com.willmear.sprint.jira.domain.model.JiraAuthType;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListAvailableJiraSprintsUseCaseTest {

    @Mock
    private GetJiraConnectionUseCase getJiraConnectionUseCase;
    @Mock
    private JiraClientPort jiraClientPort;
    @InjectMocks
    private ListAvailableJiraSprintsUseCase useCase;

    @Test
    void shouldListAvailableSprintsSortedAndDeduplicated() {
        JiraConnection connection = connection();
        ExternalJiraBoardDto boardOne = new ExternalJiraBoardDto(10L, "Alpha Board", "scrum", "ALPHA");
        ExternalJiraBoardDto boardTwo = new ExternalJiraBoardDto(20L, "Beta Board", "scrum", "BETA");

        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraClientPort.fetchBoards(connection)).thenReturn(List.of(boardOne, boardTwo));
        when(jiraClientPort.fetchBoardSprints(connection, boardOne.id())).thenReturn(List.of(
                new ExternalJiraSprintDto(1L, boardOne.id(), "Active Sprint", null, "active", Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600), null),
                new ExternalJiraSprintDto(2L, boardOne.id(), "Closed Sprint", null, "closed", Instant.now().minusSeconds(7200), Instant.now().minusSeconds(1800), Instant.now().minusSeconds(1200))
        ));
        when(jiraClientPort.fetchBoardSprints(connection, boardTwo.id())).thenReturn(List.of(
                new ExternalJiraSprintDto(1L, boardOne.id(), "Active Sprint", null, "active", Instant.now().minusSeconds(3600), Instant.now().plusSeconds(3600), null),
                new ExternalJiraSprintDto(3L, boardTwo.id(), "Future Sprint", null, "future", Instant.now().plusSeconds(7200), Instant.now().plusSeconds(10800), null)
        ));

        List<AvailableJiraSprint> result = useCase.list(connection.workspaceId(), connection.id());

        assertThat(result).extracting(AvailableJiraSprint::sprintId).containsExactly(1L, 3L, 2L);
        assertThat(result).extracting(AvailableJiraSprint::boardName).containsExactly("Alpha Board", "Beta Board", "Alpha Board");
    }

    @Test
    void shouldFallBackToIssueBackedSprintDiscoveryWhenBoardDiscoveryReturnsNothing() {
        JiraConnection connection = connection();

        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        when(jiraClientPort.fetchBoards(connection)).thenReturn(List.of());
        when(jiraClientPort.fetchRecentSprints(connection)).thenReturn(List.of(
                new ExternalJiraSprintDto(11L, 91L, "Platform Sprint", null, "active", Instant.now(), Instant.now().plusSeconds(3600), null)
        ));

        List<AvailableJiraSprint> result = useCase.list(connection.workspaceId(), connection.id());

        assertThat(result).singleElement().satisfies(sprint -> {
            assertThat(sprint.sprintId()).isEqualTo(11L);
            assertThat(sprint.sprintName()).isEqualTo("Platform Sprint");
            assertThat(sprint.boardName()).isNull();
        });
    }

    @Test
    void shouldFallBackToIssueBackedSprintDiscoveryWhenBoardDiscoveryFails() {
        JiraConnection connection = connection();

        when(getJiraConnectionUseCase.get(connection.workspaceId(), connection.id())).thenReturn(connection);
        doThrow(new JiraOAuthException("board access failed")).when(jiraClientPort).fetchBoards(connection);
        when(jiraClientPort.fetchRecentSprints(connection)).thenReturn(List.of(
                new ExternalJiraSprintDto(21L, null, "Fallback Sprint", null, "future", Instant.now(), Instant.now().plusSeconds(3600), null)
        ));

        List<AvailableJiraSprint> result = useCase.list(connection.workspaceId(), connection.id());

        assertThat(result).extracting(AvailableJiraSprint::sprintId).containsExactly(21L);
    }

    private JiraConnection connection() {
        return new JiraConnection(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "https://example.atlassian.net",
                JiraAuthType.OAUTH,
                JiraConnectionStatus.ACTIVE,
                "user@example.com",
                "access",
                "refresh",
                Instant.now().plusSeconds(3600),
                Instant.now(),
                "acct",
                "User",
                "https://avatar.example/user.png",
                Instant.now(),
                Instant.now()
        );
    }
}
