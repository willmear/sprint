# sprint

Backend-first modular monolith scaffold for ingesting Jira sprint data, generating sprint review artifacts, and preparing future AI and retrieval integrations.

## Stack

- Java 25
- Spring Boot 4.0.3
- Maven
- PostgreSQL
- Flyway

## Module Overview

- `common`: small shared primitives only
- `config`: framework and bean wiring
- `api`: REST entrypoints, DTOs, and API mappers
- `auth`: authentication boundary
- `workspace`: workspace and tenant boundary
- `jira`: Jira integration boundary
- `sprintreview`: sprint review core domain
- `ai`: LLM and prompt orchestration boundary
- `retrieval`: retrieval and future pgvector boundary
- `artifact`: generated artifact persistence boundary
- `jobs`: asynchronous job processing boundary
- `persistence`: cross-cutting persistence support
- `observability`: logging, metrics, and tracing scaffolding
- `support`: development fixtures and local support placeholders

## Persistence

- PostgreSQL is the target database.
- Flyway migrations live under `src/main/resources/db/migration`.
- The current schema includes `workspace`, `jira_connection`, `jira_oauth_state`, and synced Jira snapshot tables for boards, sprints, issues, comments, changelog events, and raw payloads.
- Local datasource properties can be overridden with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

## Workspace And Jira Connection Flow

- Workspace endpoints: `POST /api/workspaces`, `GET /api/workspaces/{workspaceId}`, `GET /api/workspaces`
- Jira OAuth endpoints: `POST /api/workspaces/{workspaceId}/jira/connections/oauth/start` and `GET /api/jira/oauth/callback`
- Jira connection management endpoints: list/get/test/disconnect under `/api/workspaces/{workspaceId}/jira/connections`
- OAuth is scaffolded with placeholder-backed URL generation and token exchange for now.

## Sprint Sync Flow

- Sprint sync endpoint: `POST /api/workspaces/{workspaceId}/jira/connections/{connectionId}/sprints/{sprintId}/sync`
- Local read endpoints: `GET /api/workspaces/{workspaceId}/sprints`, `GET /api/workspaces/{workspaceId}/sprints/{sprintId}`, and `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/issues`
- Synced Jira data is stored locally for later sprint review generation.
- Comments and changelog ingestion are scaffolded and enabled by default.
- Raw Jira payloads are stored for future reprocessing and debugging.
- A future step will move sprint sync orchestration onto the jobs boundary.

## Jobs Framework

- A database-backed jobs framework now exists using the `job` table.
- The scheduler polls pending jobs, claims them, and dispatches by `JobType`.
- Current processor implementations are placeholders intended to prove dispatch and lifecycle handling.
- Future steps will move sprint sync, sprint review generation, embeddings, and export flows onto this framework.

## Sprint Review Orchestration

- Sprint review orchestration now builds a `SprintContext` from synced local Jira data.
- Direct generation endpoint: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate`
- Context endpoint: `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/review/context`
- Optional job entrypoint: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate-job`
- Sprint review generation now routes through prompt builders and an AI generation layer, with deterministic fallback when AI is disabled or unavailable.

## Artifact Persistence

- Generated sprint reviews are now persisted as artifacts with structured JSON and rendered markdown.
- Latest sprint review retrieval is now backed by durable artifact storage.
- Jobs-based sprint review generation persists the same durable output path as direct generation.
- Retrieval/vector search remains a future step.

## AI Module

- The AI module now owns prompt builders, structured parsers, workflow-specific generation use cases, and an OpenAI-oriented client abstraction.
- `app.openai.mock-mode=true` enables deterministic local development responses without live API calls.
- Sprint review generation can use the AI-backed path while still falling back safely to the placeholder generator.
- Artifact persistence and retrieval/vector integration are still future steps.

## Retrieval

- The retrieval module now indexes synced sprint data into PostgreSQL using pgvector.
- Sprint issues, comments, and optional sprint summary content can be embedded and searched semantically.
- Retrieval can enrich sprint review context through a lightweight enrichment hook.
- No separate vector database is used.

## Observability

- API requests now propagate a correlation ID via `X-Correlation-Id`, which is also returned in error responses.
- Actuator health and metrics endpoints are exposed for lightweight local operational visibility.
- Jobs, Jira sync, sprint review generation, AI, and retrieval flows now emit focused logs and Micrometer metrics.
- Observability remains intentionally lightweight and app-local; future tracing/export integrations can build on this foundation.

## Local Runtime

- `src/main/resources/docker-compose.yml` runs PostgreSQL and the Spring Boot application together for local development.
- `Dockerfile` builds the application jar in a Maven builder stage and runs it on Java 25.
- The application reads datasource and Flyway settings from environment variables, with local defaults in `application.yml`.

Run locally with:

```bash
docker compose -f src/main/resources/docker-compose.yml up --build
```
