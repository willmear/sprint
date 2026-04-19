# sprint

Monorepo for syncing Jira sprint data, generating sprint reviews, exporting review artifacts, building editable presentation decks, exporting `.pptx`, shipping backend/frontend containers.

## Repo Layout

- `sprint-backend/`: Spring Boot backend, Maven build, Dockerfile, compose file, DB migrations
- `sprint-ui/`: Next.js frontend
- `.github/workflows/ci-cd.yml`: CI/CD workflow. Build test backend, publish backend + frontend images to GHCR on non-PR runs

## Stack

- Java 25
- Spring Boot 4.0.3
- Maven
- PostgreSQL
- Flyway
- Next.js 15
- React 19
- Tailwind CSS
- TanStack Query
- Apache POI

## Module Overview

- `common`: shared primitives only
- `config`: framework and bean wiring
- `api`: REST entrypoints, DTOs, mappers
- `auth`: Atlassian/Jira-backed app login, session cookie auth, current-user resolution
- `workspace`: workspace boundary
- `jira`: Jira OAuth, connection mgmt, sprint sync, local Jira snapshot persistence
- `sprintreview`: sprint review domain + orchestration
- `ai`: LLM client, prompts, parsing, validation
- `retrieval`: pgvector retrieval + sprint-context enrichment
- `artifact`: generated artifact persistence
- `presentationplan`: sprint review -> structured presentation plan
- `presentation`: editable deck, slides, slide elements, themes, layout
- `export`: markdown, outline, speaker-notes, PowerPoint export
- `jobs`: async job framework
- `persistence`: cross-cutting persistence support
- `observability`: correlation IDs, logs, metrics, tracing scaffolding
- `support`: local/dev support

## Current Product State

- Jira OAuth now acts as app login.
- Workspace pages no longer treat Jira connection as login. App login happens first, then workspace-specific Jira authorization can be added later per workspace.
- Frontend bootstraps auth state through `/api/auth/me`.
- Backend requires authenticated user session for workspace and workspace-bound APIs.
- Workspaces are owned by authenticated app user and scoped per user.
- Logout supported through session invalidation + cookie clear.
- Sprint review generation now has per-user daily credits, default `3/day`, using UTC day boundaries.
- Jira OAuth remains wired end-to-end for workspace-scoped Jira connections.
- Sprint sync still synchronous.
- Sprint review generation supports direct endpoint and job entrypoint.
- Review artifacts persist and feed export + presentation flows.
- Slide editor exists. Users can create/open deck from latest sprint review, edit slides, reorder, duplicate, delete, add text/shape elements, save, export `.pptx`.
- CI/CD workflow now publishes 2 GHCR images:
  - backend: `ghcr.io/<owner>/<repo>`
  - frontend: `ghcr.io/<owner>/<repo>-frontend`

## Persistence

- PostgreSQL target DB.
- Flyway migrations live under `sprint-backend/src/main/resources/db/migration`.
- Schema includes:
  - `app_user`
  - `app_session`
  - `auth_login_state`
  - `workspace`
  - Jira OAuth + synced Jira snapshot tables
  - `job`
  - persisted generated artifacts
  - `presentation_deck`
  - `presentation_slide`
  - `presentation_slide_element`
- Local datasource props override with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

## Auth And Workspace Flow

- App auth endpoints:
  - `GET /api/auth/jira/login`
  - `GET /api/auth/jira/callback`
  - `GET /api/auth/me`
  - `POST /api/auth/logout`
- Jira OAuth login creates HTTP-only app session cookie.
- User must log in before accessing user-specific workspaces.
- Frontend redirects unauthenticated users to `/login` before workspace access.
- UI terminology now separates app auth from workspace Jira integration:
  - `Log in with Jira` = app login
  - `Authorize Jira access` / `Connect Jira site` = workspace integration step
- Workspace endpoints return only current user-owned workspaces:
  - `POST /api/workspaces`
  - `GET /api/workspaces/{workspaceId}`
  - `GET /api/workspaces`
- Workspace-scoped Jira OAuth endpoints remain:
  - `POST /api/workspaces/{workspaceId}/jira/connections/oauth/start`
  - `GET /api/jira/oauth/callback`
- Jira connection management under `/api/workspaces/{workspaceId}/jira/connections`
- Jira OAuth resolves actual Jira site URL after Atlassian auth, stores account summary on connection

## Sprint Sync Flow

- Sync endpoint: `POST /api/workspaces/{workspaceId}/jira/connections/{connectionId}/sprints/{sprintId}/sync`
- Available-sprints endpoint: `GET /api/workspaces/{workspaceId}/jira/connections/{connectionId}/available-sprints`
- Local read endpoints:
  - `GET /api/workspaces/{workspaceId}/sprints`
  - `GET /api/workspaces/{workspaceId}/sprints/{sprintId}`
  - `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/issues`
- Sync stores local sprint, issues, comments, changelog, raw payloads
- Comments + changelog enabled by default
- Sprint sync not moved to jobs yet

## Jobs Framework

- DB-backed jobs framework exists
- Scheduler polls pending jobs, claims, dispatches by `JobType`
- Real processors currently include:
  - sprint review generation
  - sprint document indexing
- `SYNC_SPRINT` job processor still placeholder
- Frontend has jobs page for listing + retrying failed jobs

## Sprint Review Orchestration

- Sprint review builds `SprintContext` from synced local Jira data
- Endpoints:
  - direct generation: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate`
  - latest persisted review: `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/review`
  - context: `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/review/context`
  - async job entrypoint: `POST /api/workspaces/{workspaceId}/sprints/{sprintId}/review/generate-job`
- AI path uses OpenAI when enabled
- Placeholder path remains fallback when AI disabled/fails
- Generated review persists as artifact, reused by export + presentation features
- Daily sprint review generation allowance defaults to `3` per authenticated user per day
- Local/dev override with:
  - `APP_CREDITS_DAILY_GENERATION_LIMIT=10`
  - `APP_CREDITS_ENABLED=true`

## Artifact Persistence

- Sprint reviews persist as artifacts with structured JSON + rendered markdown
- Latest review retrieval backed by durable artifact storage
- Artifact endpoints support workspace-level + sprint-scoped listing/fetching

## Presentation And Export

- Review exports:
  - markdown
  - presentation outline
  - speaker notes
  - PowerPoint
- Export endpoints:
  - `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/export?format=...`
  - `GET /api/artifacts/{artifactId}/export?format=...`
  - `GET /api/workspaces/{workspaceId}/slides/decks/{deckId}/export/powerpoint`
  - `GET /api/workspaces/{workspaceId}/sprints/{sprintId}/export/powerpoint`
- Presentation deck endpoints support:
  - create/get latest deck for sprint
  - fetch deck by id
  - save deck
  - update slide
  - reorder slides
  - add slide
  - duplicate slide
  - delete slide
- Frontend slide editor route:
  - `workspaces/[workspaceId]/sprints/[sprintId]/slides`
- Current editor support:
  - text-first slides
  - speaker notes
  - freeform text boxes
  - basic shapes
  - theme selection
  - save/reorder/duplicate/delete
  - `.pptx` export

## AI Module

- Owns OpenAI client, prompt building, structured parsing, validation
- Set `APP_OPENAI_API_KEY` for real OpenAI-backed sprint review generation
- `APP_OPENAI_MOCK_MODE=true` keeps deterministic local responses

## Retrieval

- Uses pgvector in PostgreSQL
- Indexes sprint issues, comments, optional sprint summary content
- Can enrich sprint review context through retrieval hook
- No separate vector DB

## Observability

- Correlation ID via `X-Correlation-Id`
- Correlation ID returned in error responses too
- Actuator health + metrics exposed
- Logs + Micrometer metrics around jobs, sync, review generation, AI, retrieval, export

## Local Runtime

- Compose file path:
  - `sprint-backend/src/main/resources/docker-compose.yml`
- Compose starts:
  - PostgreSQL with pgvector
  - Spring Boot backend
  - Next.js UI
- Backend allows local frontend origin with `APP_UI_ALLOWED_ORIGIN`, default `http://localhost:3000`
- Jira OAuth local env uses `APP_JIRA_OAUTH_*`
- Credits local env uses:
  - `APP_CREDITS_DAILY_GENERATION_LIMIT=10`
  - `APP_CREDITS_ENABLED=true`
- Docker Compose backend service forwards both credit env vars, so local override can come from shell env or `.env`

Example `.env` for Compose:

```bash
APP_CREDITS_ENABLED=true
APP_CREDITS_DAILY_GENERATION_LIMIT=20
```

Run full stack from repo root:

```bash
docker compose -f sprint-backend/src/main/resources/docker-compose.yml up --build
```

Run backend tests:

```bash
cd sprint-backend
mvn test
```

Run backend locally:

```bash
cd sprint-backend
mvn spring-boot:run
```

Run frontend locally:

```bash
cd sprint-ui
npm install
npm run dev
```

Build backend image locally:

```bash
cd sprint-backend
docker build -t sprint-backend .
```

Build frontend image locally:

```bash
cd sprint-ui
docker build -t sprint-ui .
```

Local URLs:

- backend API: `http://localhost:8080`
- frontend UI: `http://localhost:3000`

## Frontend Notes

- UI uses Next.js App Router, React, TypeScript, Tailwind CSS, TanStack Query
- Frontend API base from `NEXT_PUBLIC_API_BASE_URL`, local default `http://localhost:8080`
- Current UI covers:
  - workspace creation + selection
  - Jira OAuth login/logout/test
  - sprint discovery + sync
  - sprint detail + issue inventory
  - sprint review generation + display
  - export preview/copy/download
  - jobs monitor
  - slide editor

## CI/CD

- Workflow file: `.github/workflows/ci-cd.yml`
- PRs to `main`: backend tests only
- Push/tag/manual non-PR runs:
  - run backend tests
  - build/push backend image to GHCR
  - build/push frontend image to GHCR
