package com.willmear.sprint.jira.application;

import com.willmear.sprint.jira.domain.model.AvailableJiraSprint;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ListAvailableJiraSprintsUseCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListAvailableJiraSprintsUseCase.class);

    private final GetJiraConnectionUseCase getJiraConnectionUseCase;
    private final JiraClientPort jiraClientPort;

    public ListAvailableJiraSprintsUseCase(
            GetJiraConnectionUseCase getJiraConnectionUseCase,
            JiraClientPort jiraClientPort
    ) {
        this.getJiraConnectionUseCase = getJiraConnectionUseCase;
        this.jiraClientPort = jiraClientPort;
    }

    public List<AvailableJiraSprint> list(UUID workspaceId, UUID connectionId) {
        JiraConnection connection = getJiraConnectionUseCase.get(workspaceId, connectionId);
        List<ExternalJiraBoardDto> boards = List.of();
        try {
            boards = jiraClientPort.fetchBoards(connection);
        } catch (RuntimeException exception) {
            LOGGER.warn("jira.available-sprints.board-discovery.failed workspaceId={} connectionId={}", workspaceId, connectionId, exception);
        }
        Map<Long, AvailableJiraSprint> deduplicated = new LinkedHashMap<>();

        for (ExternalJiraBoardDto board : boards) {
            if (board.type() == null || !board.type().equalsIgnoreCase("scrum")) {
                continue;
            }
            List<ExternalJiraSprintDto> sprints = jiraClientPort.fetchBoardSprints(connection, board.id());
            for (ExternalJiraSprintDto sprint : sprints) {
                deduplicated.putIfAbsent(sprint.id(), new AvailableJiraSprint(
                        sprint.id(),
                        sprint.name(),
                        sprint.state(),
                        sprint.boardId() != null ? sprint.boardId() : board.id(),
                        board.name(),
                        sprint.startDate(),
                        sprint.endDate(),
                        sprint.completeDate()
                ));
            }
        }

        if (deduplicated.isEmpty()) {
            Map<Long, String> boardNamesById = boards.stream()
                    .collect(java.util.stream.Collectors.toMap(
                            ExternalJiraBoardDto::id,
                            ExternalJiraBoardDto::name,
                            (left, right) -> left
                    ));
            for (ExternalJiraSprintDto sprint : jiraClientPort.fetchRecentSprints(connection)) {
                deduplicated.putIfAbsent(sprint.id(), new AvailableJiraSprint(
                        sprint.id(),
                        sprint.name(),
                        sprint.state(),
                        sprint.boardId(),
                        sprint.boardId() != null ? boardNamesById.get(sprint.boardId()) : null,
                        sprint.startDate(),
                        sprint.endDate(),
                        sprint.completeDate()
                ));
            }
        }

        return deduplicated.values().stream()
                .sorted(Comparator
                        .comparingInt((AvailableJiraSprint sprint) -> stateRank(sprint.state()))
                        .thenComparing((AvailableJiraSprint sprint) -> mostRelevantDate(sprint), Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AvailableJiraSprint::sprintName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private int stateRank(String state) {
        if (state == null) {
            return 3;
        }
        return switch (state.toUpperCase()) {
            case "ACTIVE" -> 0;
            case "FUTURE" -> 1;
            case "CLOSED" -> 2;
            default -> 3;
        };
    }

    private Instant mostRelevantDate(AvailableJiraSprint sprint) {
        if (sprint.endDate() != null) {
            return sprint.endDate();
        }
        if (sprint.completeDate() != null) {
            return sprint.completeDate();
        }
        return sprint.startDate();
    }
}
