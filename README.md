# chainvault

Lightweight orchestration and document migration engine for secure processing, auditability, and delivery.

[![Java 25](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot 4.1](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![React](https://img.shields.io/badge/React-19.2-2496ED?logo=react&logoColor=white)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Liquibase](https://img.shields.io/badge/Liquibase-managed-2962FF)](https://www.liquibase.org/)
[![Flowable](https://img.shields.io/badge/orchestrated%20with-Flowable%208-0072C6)](https://www.flowable.com/)
[![mise](https://img.shields.io/badge/managed%20with-mise-6f42c1)](https://mise.jdx.dev/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gryphus-lab_chainvault&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gryphus-lab_chainvault)
[![GitHub Actions CI](https://github.com/gryphus-lab/chainvault/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/gryphus-lab/chainvault/actions/workflows/ci.yml)

## At a glance

|      Aspect       |                     Stack                     |
|-------------------|-----------------------------------------------|
| Language          | Java 25, TypeScript 5.9                       |
| Framework         | Spring Boot 4.1.1, React 19.2                 |
| Orchestration     | Flowable 8.0.0 (BPMN 2.0)                     |
| Database          | PostgreSQL 18                                 |
| Schema migrations | Liquibase (Maven plugin 5.0.4)                |
| Build             | Maven 3.9 (multi-module), Vite 8              |
| Local tooling     | mise                                          |
| Observability     | Prometheus, Loki, Grafana, OpenTelemetry 1.66 |
| CI / quality      | GitHub Actions, SonarCloud                    |
| Testing           | JUnit 5, Testcontainers 2.0, Vitest 5         |
| API docs          | springdoc OpenAPI 3.1 / Swagger UI            |

## Overview

Chainvault is a Java-first document orchestration service built to process incoming archives through a BPMN workflow. It coordinates document extraction, hashing, metadata transformation, OCR, PDF merging, signing, and secure SFTP delivery while preserving a full audit trail for each migration.

The service is designed for operational transparency and traceability:

- Extracts and validates incoming ZIP archives
- Computes content hashes and stores chain-of-custody metadata
- Runs OCR over document pages using Tesseract
- Transforms and prepares source files for downstream processing
- Merges PDF output and signs documents before handoff
- Uploads final artifacts to SFTP endpoints
- Records timeline events and exposes them through the REST API and live dashboard

## Features

- Java 25 support with modern JVM tuning and virtual-thread-friendly runtime defaults
- Spring Boot 4.1.1 baseline with Jakarta EE 11 compatibility
- Multi-module Maven build with dedicated migration, orchestration, UI, and coverage modules
- Flowable 8.0.0 BPMN 2.0 engine for process orchestration and delegate-based execution
- PostgreSQL-backed persistence with Liquibase-managed schema evolution
- OCR via Tesseract/Tess4J for TIFF and page-based document extraction
- Document transformation, merge, and signing pipeline for migration artifacts
- Secure SFTP upload integration for downstream delivery
- Real-time migration status stream via Server-Sent Events (SSE)
- React 19.2 dashboard (TypeScript 5.9, Vite 8) with live metrics, filtering, and per-migration detail views
- Spring Boot SPA hosting for frontend delivery without a separate web server
- Aggregated JaCoCo coverage for CI and SonarCloud quality gates
- Docker-ready local development and compose-based integration workflows

## Module structure

```text
.
├── chainvault-migration/          # Business logic: extraction, hashing, OCR, signing, PDF merge, SFTP
├── chainvault-orchestration/      # Spring Boot app, REST API, Flowable engine, entities, delegates
│   └── src/main/java/
│       ├── controller/
│       │   ├── MigrationController.java     # /api/migrations endpoints
│       │   └── SpaController.java           # forwards SPA routes to index.html
│       ├── model/
│       │   ├── Migration.java               # basic migration record
│       │   ├── MigrationDetail.java         # detail payload with timeline + downloads
│       │   └── MigrationStats.java          # aggregated metrics
│       └── workflow/
│           ├── delegate/                    # BPMN delegate implementations
│           └── service/
│               ├── AuditEventService.java   # migration stats/detail queries
│               └── SseEmitterService.java   # SSE push for dashboard updates
├── chainvault-admin-ui/           # React 19 admin dashboard; bundled into the Spring Boot JAR
│   └── src/
│       ├── hooks/useMigrationEvents.ts
│       └── views/pages/migration/
│           ├── Overview.tsx
│           └── MigrationDetailPage.tsx
├── chainvault-report-aggregate/   # JaCoCo report aggregation
├── docker-compose.yml             # app + postgres + fake source + SFTP stack
├── docker-compose-lgtm.yml        # observability stack (Prometheus, Loki, Alloy, Grafana)
├── env/
│   └── prometheus.yml             # Prometheus scrape config
├── mise.toml                      # toolchain + developer tasks
├── Dockerfile                     # container image definition
├── pom.xml                        # parent Maven build configuration
├── start_test.sh                  # smoke/load test orchestration
├── .github/workflows/ci.yml       # project CI
└── README.md
```

## Architecture

### BPMN workflow

The process defined in `chainvault-orchestration/src/main/resources/processes/chainvault.bpmn` runs in order:

```text
AsyncInitVariables → ExtractAndHash → TransformMetadata → PrepareFiles →
PerformOcr → MergePdf → SignDocument → SftpUpload → [End]
                                                          ↓
                                                    HandleError → [End Failed]
```

Each step is implemented as a Flowable delegate, and boundary error events route failures into `HandleError` so problems are captured in the audit timeline.

### REST API

The orchestration service exposes the main migration endpoints:

| Method |             Path              |                         Description                         |
|--------|-------------------------------|-------------------------------------------------------------|
| `GET`  | `/api/migrations?limit={n}`   | List recent migrations                                      |
| `GET`  | `/api/migrations/stats`       | Aggregated migration metrics                                |
| `GET`  | `/api/migrations/{id}/detail` | Detail payload with events, OCR preview, and artifact links |
| `GET`  | `/api/migrations/events`      | SSE stream for live dashboard updates                       |

The detail response includes `events`, `ocrTextPreview`, and artifact URLs such as `chainZipUrl` and `pdfUrl`.

### Dashboard and events

The React UI consumes `/api/migrations/events` and updates the dashboard in real time. The `useMigrationEvents` hook automatically reconnects if the stream drops and merges live updates into the migration table.

### Database and migrations

- Database: PostgreSQL 18
- Schema management: Liquibase YAML changelogs under `chainvault-orchestration/src/main/resources/db/changelog/`
- Start-up behavior: local profile applies the schema automatically
- Core entities: migration audit records and migration event timeline rows

## Prerequisites

- Docker and Docker Compose v2+
- [mise](https://mise.jdx.dev/) to manage the toolchain (Java, Maven, Node, Yarn, Python) and project tasks
- Git
- Tesseract OCR runtime for local OCR: `brew install tesseract tesseract-lang`
- Access to the configured SFTP and source API endpoints for your local profile

### Toolchain versions

`mise` pins the full toolchain in `mise.toml`, so you do not need these installed globally - `mise install` provisions them:

|         Tool          |  Version   |                                          Notes                                          |
|-----------------------|------------|-----------------------------------------------------------------------------------------|
| Java                  | temurin 25 | Build + runtime JDK (`-XX:+UseZGC`)                                                     |
| Maven                 | 3.9        | Multi-module reactor build                                                              |
| Node                  | 25.9.0     | Dev shell (`mise`); the Maven UI build pins Node 26.0.0 via frontend-maven-plugin 2.0.2 |
| Yarn                  | 4.13.0     | Admin-UI package manager                                                                |
| Python                | 3.14       | Tooling/scripts (`uv`-managed `.venv`)                                                  |
| jq / trivy / hadolint | latest     | CI helpers                                                                              |

The container image is multi-stage: the build stage uses a `maven:3-eclipse-temurin-*` image (tracking the latest JDK via Dependabot) and the runtime stage uses `eclipse-temurin:25-jre-noble` (JRE 25).

One-time environment setup:

```bash
curl https://mise.run | sh
mise doctor
```

`mise` is configured to set `SPRING_PROFILES_ACTIVE=local` and `TESSDATA_PREFIX=/opt/homebrew/share/tessdata` by default for macOS installs.

## Quick start

```bash
git clone https://github.com/gryphus-lab/chainvault.git
cd chainvault

mise install
mise trust
mise dev
```

After startup, the app is available at:

- Health: <http://localhost:8085/actuator/health>
- Swagger UI: <http://localhost:8085/swagger-ui.html>
- Dashboard: <http://localhost:8085/>

## Local development

### Common commands

```bash
mise build                    # mvn clean install -DskipTests
mise test                     # mvn integration-test
mise test-docker              # Docker-based integration tests only
mise verify                   # mvn clean verify -Pcoverage
mise package                  # mvn clean package -DskipTests -am
mise dev                      # start local profile with Postgres + app
mise compose-up               # docker compose with app + observability stack
mise compose-down             # stop all compose services
mise compose-down-full        # stop all services and volumes
mise docker-build             # build local Docker image
mise docker-build-versioned   # build versioned Docker image from POM
mise smoke-test               # run smoke test script
mise load-test                # run load test script (1000 iterations)
mise github-build             # CI build with JaCoCo coverage
mise check                    # yarn lint + prettier + Spotless check
mise format                   # yarn lint-fix + prettier + Spotless apply
```

### Local profile configuration

The project uses the local Spring profile by default via `mise.toml`:

```text
SPRING_PROFILES_ACTIVE=local
TESSDATA_PREFIX=/opt/homebrew/share/tessdata
```

The corresponding `application-local.yml` is used for local service wiring such as PostgreSQL, SFTP, and the fake source API.

## Observability and local stack

The repository includes a full observability stack for local monitoring and troubleshooting.

```bash
docker compose -f docker-compose-lgtm.yml up -d
```

Access points:

- Grafana: <http://localhost:3000> (admin/admin)
- Prometheus: <http://localhost:9090>
- Loki: <http://localhost:3100>
- App metrics: <http://localhost:8085/actuator/prometheus>
- OpenTelemetry exporter: `localhost:4317`

## Docker and compose

```bash
# build image
mise docker-build

# build tagged image using the project version
mise docker-build-versioned

# start the full stack
mise compose-up

# stop services
mise compose-down

# stop services and volumes
mise compose-down-full
```

## Testing and coverage

```bash
# unit + integration tests
mise test

# Docker integration tests only
mise test-docker

# build + verify + JaCoCo aggregate coverage
mise verify
```

Specific examples:

```bash
mvn -pl chainvault-orchestration test -Dtest=MigrationControllerTest
mvn -pl chainvault-orchestration failsafe:integration-test -Dtest=DockerServicesIT
```

Coverage report:

```text
chainvault-report-aggregate/target/site/jacoco-aggregate/index.html
```

## Continuous integration

GitHub Actions runs the CI workflow for pushes to `main` and pull requests targeting `main`. It builds and tests the project with `mise run github-build` (`mvn clean install -Pcoverage`), publishes JUnit test reports, and runs a SonarCloud analysis.

## Configuration and secrets

Sensitive configuration should be provided through environment variables or mounted secrets instead of being checked into source control.

This includes:

- SFTP credentials
- signing keys
- API tokens
- environment-specific service endpoints

## Contributing

Contributions are welcome. Please keep changes aligned with the current architecture, run the relevant tests, and follow the repo’s formatting and lint conventions.

## Maintainers

- gryphus-lab / <gryphus-lab@users.noreply.github.com>

---

Chainvault is a secure, observable, and workflow-driven document processing system for enterprise migration pipelines.
