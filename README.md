# sprint

Backend-first modular monolith scaffold for ingesting Jira sprint data, generating sprint review artifacts, and preparing future AI and retrieval integrations.

## Repo Layout

- `sprint-backend/`: Spring Boot backend, Maven build, Dockerfile, and local Docker Compose stack
- `sprint-ui/`: Next.js frontend

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
- Flyway migrations live under `sprint-backend/src/main/resources/db/migration`.
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

- Sprint review orchestration now builds a `SprintContext` from synced local Jira data, including issue comments used during generation.
- Direct generation endpoint: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate`
- Context endpoint: `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/review/context`
- Optional job entrypoint: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate-job`
- Sprint review generation now uses the real OpenAI API when enabled, grounding the generated review in synced sprint tickets, statuses, and comments.
- Placeholder generation remains available as an explicit fallback when AI is disabled or the AI path fails.

## Artifact Persistence

- Generated sprint reviews are now persisted as artifacts with structured JSON and rendered markdown.
- Latest sprint review retrieval is now backed by durable artifact storage.
- Jobs-based sprint review generation persists the same durable output path as direct generation.
- Persisted sprint reviews can now be exported on demand as markdown, presentation outlines, and copy-ready speaker notes.
- Editable presentation decks can now be created from persisted sprint review artifacts and saved before future PowerPoint export.
- Retrieval/vector search remains a future step.

## AI Module

- The AI module owns the OpenAI client, sprint review prompt building, structured parsing, and validation.
- Set `APP_OPENAI_API_KEY` to enable real OpenAI-backed sprint review generation.
- `app.openai.mock-mode=true` keeps deterministic local development responses without live API calls.
- Successful AI generation persists the structured sprint review through the existing artifact flow, and `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/review` returns the persisted artifact-backed review.

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

- `sprint-backend/src/main/resources/docker-compose.yml` runs PostgreSQL with pgvector, the Spring Boot application, and the Next.js UI together for local development.
- `sprint-ui/` is a separate Next.js + React + TypeScript frontend app in the same repo for workspace, Jira OAuth, sprint sync, sprint detail, sprint review, and jobs flows.
- `sprint-backend/Dockerfile` builds the application jar in a Maven builder stage and runs it on Java 25.
- The application reads datasource and Flyway settings from environment variables, with local defaults in `application.yml`.
- Jira OAuth resolves the actual Jira site URL from Atlassian after login, so local runtime only needs `APP_JIRA_OAUTH_*`; older names like `APP_JIRA_BASE_URL`, `APP_JIRA_USERNAME`, and `APP_JIRA_API_TOKEN` are not used by the application.
- The backend allows the local frontend origin via `APP_UI_ALLOWED_ORIGIN`, which defaults to `http://localhost:3000`.

Run the full local stack from the repo root:

```bash
docker compose -f sprint-backend/src/main/resources/docker-compose.yml up --build
```

Run the backend tests from the backend directory:

```bash
cd sprint-backend
mvn test
```

Run the backend app locally from the backend directory:

```bash
cd sprint-backend
mvn spring-boot:run
```

Run the frontend locally from the frontend directory:

```bash
cd sprint-ui
npm install
npm run dev
```

Build the backend Docker image from the backend directory:

```bash
cd sprint-backend
docker build -t sprint-backend .
```

This gives you:
- backend API on `http://localhost:8080`
- frontend UI on `http://localhost:3000`

## Frontend Notes

- The UI uses Next.js App Router, React, TypeScript, Tailwind CSS, and TanStack Query.
- Frontend API calls read `NEXT_PUBLIC_API_BASE_URL`, with a local default of `http://localhost:8080` in `sprint-ui/.env.local`.
- Review generation and display are grounded in the existing Spring Boot sprint review endpoints and render the structured summary, themes, highlights, blockers, and speaker notes returned by the backend.
- The review page now supports one-click export and copy flows for markdown, presentation outlines, and speaker notes.
- A new slide editor flow lets users open a deck derived from the latest sprint review, edit slides and speaker notes, reorder/add/delete slides, and save the deck for later export. PowerPoint export is planned to use these edited decks in a later step.
