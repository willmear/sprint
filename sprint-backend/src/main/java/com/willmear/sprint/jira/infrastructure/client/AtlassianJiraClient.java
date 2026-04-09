package com.willmear.sprint.jira.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.willmear.sprint.common.exception.JiraOAuthException;
import com.willmear.sprint.jira.domain.model.JiraAccessibleResource;
import com.willmear.sprint.jira.domain.model.JiraAccountSummary;
import com.willmear.sprint.jira.domain.model.JiraConnectionStatus;
import com.willmear.sprint.jira.domain.model.JiraConnection;
import com.willmear.sprint.jira.domain.model.JiraOAuthTokenResponse;
import com.willmear.sprint.jira.domain.model.JiraConnectionTestResult;
import com.willmear.sprint.jira.domain.port.JiraClientPort;
import com.willmear.sprint.jira.domain.port.JiraConnectionRepositoryPort;
import com.willmear.sprint.jira.domain.port.JiraOAuthClientPort;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraBoardDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraChangelogDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraCommentDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraIssueFieldsDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraSprintDto;
import com.willmear.sprint.jira.infrastructure.client.dto.ExternalJiraUserDto;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class AtlassianJiraClient implements JiraClientPort, JiraRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtlassianJiraClient.class);
    private static final DateTimeFormatter ATLASSIAN_OFFSET_DATE_TIME = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .optionalStart()
            .appendPattern(".SSS")
            .optionalEnd()
            .appendOffset("+HHMM", "Z")
            .toFormatter();

    private final JiraOAuthClientPort jiraOAuthClientPort;
    private final JiraConnectionRepositoryPort jiraConnectionRepositoryPort;
    private final ObjectMapper objectMapper;

    public AtlassianJiraClient(
            JiraOAuthClientPort jiraOAuthClientPort,
            JiraConnectionRepositoryPort jiraConnectionRepositoryPort,
            ObjectMapper objectMapper
    ) {
        this.jiraOAuthClientPort = jiraOAuthClientPort;
        this.jiraConnectionRepositoryPort = jiraConnectionRepositoryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public JiraAccountSummary getCurrentAccount(JiraConnection connection) {
        ResourceContext resource = resolveResource(connection);
        JsonNode response = getJson(resource, "/rest/api/3/myself");
        return new JiraAccountSummary(
                text(response, "accountId"),
                text(response, "displayName"),
                text(response, "emailAddress")
        );
    }

    @Override
    public JiraConnectionTestResult testConnection(JiraConnection connection) {
        try {
            JiraAccountSummary accountSummary = getCurrentAccount(connection);
            return new JiraConnectionTestResult(true, "Jira connection test succeeded.", accountSummary);
        } catch (RuntimeException exception) {
            LOGGER.warn("jira.connection.test.failed baseUrl={}", connection.baseUrl(), exception);
            return new JiraConnectionTestResult(false, "Jira connection test failed: " + exception.getMessage(), null);
        }
    }

    @Override
    public List<ExternalJiraBoardDto> fetchBoards(JiraConnection connection) {
        ResourceContext resource = resolveResource(connection);
        List<ExternalJiraBoardDto> boards = new ArrayList<>();
        int startAt = 0;

        while (true) {
            JsonNode response = getJson(resource, "/rest/agile/1.0/board?startAt=" + startAt + "&maxResults=50");
            JsonNode boardNodes = response.path("values");
            boardNodes.forEach(boardNode -> boards.add(new ExternalJiraBoardDto(
                    longValue(boardNode, "id"),
                    text(boardNode, "name"),
                    text(boardNode, "type"),
                    text(boardNode.path("location"), "projectKey")
            )));
            startAt += response.path("maxResults").asInt(boardNodes.size());
            if (boardNodes.isEmpty() || startAt >= response.path("total").asInt(startAt)) {
                break;
            }
        }

        return boards;
    }

    @Override
    public ExternalJiraBoardDto fetchBoard(JiraConnection connection, Long boardId) {
        ResourceContext resource = resolveResource(connection);
        JsonNode response = getJson(resource, "/rest/agile/1.0/board/" + boardId);
        return new ExternalJiraBoardDto(
                longValue(response, "id"),
                text(response, "name"),
                text(response, "type"),
                text(response.path("location"), "projectKey")
        );
    }

    @Override
    public List<ExternalJiraSprintDto> fetchBoardSprints(JiraConnection connection, Long boardId) {
        ResourceContext resource = resolveResource(connection);
        List<ExternalJiraSprintDto> sprints = new ArrayList<>();
        int startAt = 0;

        while (true) {
            JsonNode response = getJson(
                    resource,
                    "/rest/agile/1.0/board/" + boardId + "/sprint?startAt=" + startAt + "&maxResults=50&state=active,future,closed"
            );
            JsonNode sprintNodes = response.path("values");
            sprintNodes.forEach(sprintNode -> sprints.add(new ExternalJiraSprintDto(
                    longValue(sprintNode, "id"),
                    longValue(sprintNode, "originBoardId"),
                    text(sprintNode, "name"),
                    text(sprintNode, "goal"),
                    text(sprintNode, "state"),
                    instantValue(sprintNode, "startDate"),
                    instantValue(sprintNode, "endDate"),
                    instantValue(sprintNode, "completeDate")
            )));
            startAt += response.path("maxResults").asInt(sprintNodes.size());
            if (sprintNodes.isEmpty() || startAt >= response.path("total").asInt(startAt)) {
                break;
            }
        }

        return sprints;
    }

    @Override
    public ExternalJiraSprintDto fetchSprint(JiraConnection connection, Long sprintId) {
        ResourceContext resource = resolveResource(connection);
        JsonNode response = getJson(resource, "/rest/agile/1.0/sprint/" + sprintId);
        return new ExternalJiraSprintDto(
                longValue(response, "id"),
                longValue(response, "originBoardId"),
                text(response, "name"),
                text(response, "goal"),
                text(response, "state"),
                instantValue(response, "startDate"),
                instantValue(response, "endDate"),
                instantValue(response, "completeDate")
        );
    }

    @Override
    public List<ExternalJiraIssueDto> fetchSprintIssues(JiraConnection connection, Long sprintId) {
        ResourceContext resource = resolveResource(connection);
        List<ExternalJiraIssueDto> issues = new ArrayList<>();
        int startAt = 0;

        while (true) {
            JsonNode response = getJson(
                    resource,
                    "/rest/agile/1.0/sprint/" + sprintId
                            + "/issue?startAt=" + startAt
                            + "&maxResults=50"
                            + "&fields=summary,description,issuetype,status,priority,assignee,reporter,created,updated"
            );
            JsonNode issueNodes = response.path("issues");
            issueNodes.forEach(issueNode -> issues.add(mapIssue(issueNode)));
            startAt += response.path("maxResults").asInt(issueNodes.size());
            if (issueNodes.isEmpty() || startAt >= response.path("total").asInt(startAt)) {
                break;
            }
        }

        return issues;
    }

    @Override
    public List<ExternalJiraCommentDto> fetchIssueComments(JiraConnection connection, String issueKey) {
        ResourceContext resource = resolveResource(connection);
        List<ExternalJiraCommentDto> comments = new ArrayList<>();
        int startAt = 0;

        while (true) {
            JsonNode response = getJson(
                    resource,
                    "/rest/api/3/issue/" + issueKey + "/comment?startAt=" + startAt + "&maxResults=100"
            );
            JsonNode commentNodes = response.path("comments");
            commentNodes.forEach(commentNode -> comments.add(new ExternalJiraCommentDto(
                    text(commentNode, "id"),
                    issueKey,
                    mapUser(commentNode.path("author")),
                    flattenRichText(commentNode.path("body")),
                    instantValue(commentNode, "created"),
                    instantValue(commentNode, "updated")
            )));
            startAt += response.path("maxResults").asInt(commentNodes.size());
            if (commentNodes.isEmpty() || startAt >= response.path("total").asInt(startAt)) {
                break;
            }
        }

        return comments;
    }

    @Override
    public List<ExternalJiraChangelogDto> fetchIssueChangelog(JiraConnection connection, String issueKey) {
        ResourceContext resource = resolveResource(connection);
        List<ExternalJiraChangelogDto> events = new ArrayList<>();
        int startAt = 0;

        while (true) {
            JsonNode response = getJson(
                    resource,
                    "/rest/api/3/issue/" + issueKey + "/changelog?startAt=" + startAt + "&maxResults=100"
            );
            JsonNode historyNodes = response.path("values");
            historyNodes.forEach(historyNode -> {
                JsonNode items = historyNode.path("items");
                items.forEach(itemNode -> events.add(new ExternalJiraChangelogDto(
                        text(historyNode, "id"),
                        issueKey,
                        text(itemNode, "field"),
                        text(itemNode, "fromString"),
                        text(itemNode, "toString"),
                        instantValue(historyNode, "created"),
                        mapUser(historyNode.path("author"))
                )));
            });
            startAt += response.path("maxResults").asInt(historyNodes.size());
            if (historyNodes.isEmpty() || startAt >= response.path("total").asInt(startAt)) {
                break;
            }
        }

        return events;
    }

    @Override
    public JsonNode get(String path) {
        throw new UnsupportedOperationException("Use JiraClientPort methods with a JiraConnection.");
    }

    private ExternalJiraIssueDto mapIssue(JsonNode issueNode) {
        JsonNode fields = issueNode.path("fields");
        return new ExternalJiraIssueDto(
                text(issueNode, "id"),
                text(issueNode, "key"),
                new ExternalJiraIssueFieldsDto(
                        text(fields, "summary"),
                        flattenRichText(fields.path("description")),
                        text(fields.path("issuetype"), "name"),
                        text(fields.path("status"), "name"),
                        text(fields.path("priority"), "name"),
                        mapUser(fields.path("assignee")),
                        mapUser(fields.path("reporter")),
                        null,
                        instantValue(fields, "created"),
                        instantValue(fields, "updated")
                )
        );
    }

    private ExternalJiraUserDto mapUser(JsonNode userNode) {
        if (userNode == null || userNode.isMissingNode() || userNode.isNull()) {
            return null;
        }
        return new ExternalJiraUserDto(
                text(userNode, "accountId"),
                text(userNode, "displayName"),
                text(userNode, "emailAddress")
        );
    }

    private ResourceContext resolveResource(JiraConnection connection) {
        JiraConnection effectiveConnection = refreshConnectionIfExpiring(connection);
        String accessToken = effectiveConnection.encryptedAccessToken();
        if (!StringUtils.hasText(accessToken)) {
            throw new JiraOAuthException("Jira connection does not have an access token.");
        }
        try {
            List<JiraAccessibleResource> resources = jiraOAuthClientPort.getAccessibleResources(accessToken);
            if (resources.isEmpty()) {
                throw new JiraOAuthException("No Jira sites are accessible for the authorized Atlassian account.");
            }

            String normalizedBaseUrl = normalizeUrl(effectiveConnection.baseUrl());
            if (StringUtils.hasText(normalizedBaseUrl)) {
                for (JiraAccessibleResource resource : resources) {
                    if (Objects.equals(normalizeUrl(resource.url()), normalizedBaseUrl)) {
                        return new ResourceContext(resource.cloudId(), resource.url(), accessToken);
                    }
                }
            }

            JiraAccessibleResource selected = resources.stream()
                    .min(Comparator.comparing(resource -> normalizeUrl(resource.url()) == null ? "" : normalizeUrl(resource.url())))
                    .orElseThrow(() -> new JiraOAuthException("No Jira sites are accessible for the authorized Atlassian account."));
            return new ResourceContext(selected.cloudId(), selected.url(), accessToken);
        } catch (RuntimeException exception) {
            if (isUnauthorized(exception) && StringUtils.hasText(effectiveConnection.encryptedRefreshToken())) {
                JiraConnection refreshedConnection = refreshConnection(effectiveConnection);
                return resolveResourceAfterRefresh(refreshedConnection);
            }
            throw exception instanceof JiraOAuthException ? (JiraOAuthException) exception
                    : new JiraOAuthException("Failed to resolve accessible Jira resources.", exception);
        }
    }

    private ResourceContext resolveResourceAfterRefresh(JiraConnection connection) {
        List<JiraAccessibleResource> resources = jiraOAuthClientPort.getAccessibleResources(connection.encryptedAccessToken());
        if (resources.isEmpty()) {
            throw new JiraOAuthException("No Jira sites are accessible for the authorized Atlassian account.");
        }

        String normalizedBaseUrl = normalizeUrl(connection.baseUrl());
        if (StringUtils.hasText(normalizedBaseUrl)) {
            for (JiraAccessibleResource resource : resources) {
                if (Objects.equals(normalizeUrl(resource.url()), normalizedBaseUrl)) {
                    return new ResourceContext(resource.cloudId(), resource.url(), connection.encryptedAccessToken());
                }
            }
        }

        JiraAccessibleResource selected = resources.stream()
                .min(Comparator.comparing(resource -> normalizeUrl(resource.url()) == null ? "" : normalizeUrl(resource.url())))
                .orElseThrow(() -> new JiraOAuthException("No Jira sites are accessible for the authorized Atlassian account."));
        return new ResourceContext(selected.cloudId(), selected.url(), connection.encryptedAccessToken());
    }

    private JiraConnection refreshConnectionIfExpiring(JiraConnection connection) {
        if (connection.tokenExpiresAt() == null || connection.tokenExpiresAt().isAfter(Instant.now().plusSeconds(60))) {
            return connection;
        }
        if (!StringUtils.hasText(connection.encryptedRefreshToken())) {
            return connection;
        }
        return refreshConnection(connection);
    }

    private JiraConnection refreshConnection(JiraConnection connection) {
        JiraOAuthTokenResponse refreshedTokens = jiraOAuthClientPort.refreshAccessToken(connection.encryptedRefreshToken());
        return jiraConnectionRepositoryPort.save(new JiraConnection(
                connection.id(),
                connection.workspaceId(),
                connection.baseUrl(),
                connection.authType(),
                connection.status() == JiraConnectionStatus.REVOKED ? JiraConnectionStatus.ACTIVE : connection.status(),
                connection.clientEmailOrUsername(),
                refreshedTokens.accessToken(),
                refreshedTokens.refreshToken(),
                refreshedTokens.expiresAt(),
                connection.lastTestedAt(),
                connection.externalAccountId(),
                connection.externalAccountDisplayName(),
                connection.createdAt(),
                Instant.now()
        ));
    }

    private boolean isUnauthorized(RuntimeException exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof HttpClientErrorException.Unauthorized) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private JsonNode getJson(ResourceContext resource, String apiPath) {
        try {
            String responseBody = RestClient.create().get()
                    .uri("https://api.atlassian.com/ex/jira/" + resource.cloudId() + apiPath)
                    .header("Authorization", "Bearer " + resource.accessToken())
                    .retrieve()
                    .body(String.class);
            if (!StringUtils.hasText(responseBody)) {
                throw new JiraOAuthException("Jira API returned an empty response body for path " + apiPath + ".");
            }
            return objectMapper.readTree(responseBody);
        } catch (RuntimeException exception) {
            throw new JiraOAuthException("Jira API request failed for path " + apiPath + ".", exception);
        } catch (Exception exception) {
            throw new JiraOAuthException("Failed to parse Jira API response for path " + apiPath + ".", exception);
        }
    }

    private String flattenRichText(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        List<String> texts = new ArrayList<>();
        collectText(node, texts);
        String value = String.join(" ", texts).replaceAll("\\s+", " ").trim();
        return value.isBlank() ? null : value;
    }

    private void collectText(JsonNode node, List<String> texts) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return;
        }
        if (node.isTextual()) {
            texts.add(node.asText());
            return;
        }
        JsonNode textNode = node.get("text");
        if (textNode != null && textNode.isTextual()) {
            texts.add(textNode.asText());
        }
        if (node.isArray()) {
            node.forEach(child -> collectText(child, texts));
            return;
        }
        node.elements().forEachRemaining(child -> collectText(child, texts));
    }

    private Instant instantValue(JsonNode node, String fieldName) {
        String value = text(node, fieldName);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (RuntimeException ignored) {
            return OffsetDateTime.parse(value, ATLASSIAN_OFFSET_DATE_TIME).toInstant();
        }
    }

    private Long longValue(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value == null || value.isNull() ? null : value.asLong();
    }

    private String text(JsonNode node, String fieldName) {
        JsonNode value = node.get(fieldName);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String normalizeUrl(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        String normalized = url.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private record ResourceContext(String cloudId, String baseUrl, String accessToken) {
    }
}
