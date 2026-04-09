package com.willmear.sprint.jira.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface JiraRestClient {

    JsonNode get(String path);
}

