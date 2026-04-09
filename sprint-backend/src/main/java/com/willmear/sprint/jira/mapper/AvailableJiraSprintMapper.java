package com.willmear.sprint.jira.mapper;

import com.willmear.sprint.api.response.AvailableJiraSprintResponse;
import com.willmear.sprint.jira.domain.model.AvailableJiraSprint;
import org.springframework.stereotype.Component;

@Component
public class AvailableJiraSprintMapper {

    public AvailableJiraSprintResponse toResponse(AvailableJiraSprint sprint) {
        return new AvailableJiraSprintResponse(
                sprint.sprintId(),
                sprint.sprintName(),
                sprint.state(),
                sprint.boardId(),
                sprint.boardName(),
                sprint.startDate(),
                sprint.endDate(),
                sprint.completeDate()
        );
    }
}
