# chainvault

Lightweight orchestration service for document migration and processing used by Gryphus Lab.

## Overview

`chainvault` is a Spring Boot-based migration/orchestration application that coordinates document extraction, transformation, signing, merging and secure transfer. It exposes BPMN workflows (see the process definition), integrates with SFTP targets, and records migration audit/events via database migrations.

Key responsibilities:
- Extract and hash documents
- Transform metadata and prepare files
- Merge PDFs and sign documents
- Upload artifacts to SFTP targets
 - Record audit and event logs via Liquibase migrations

## Repo layout
- `src/main/java` — application source, controllers, delegates and services
- `src/main/resources/application.yml` — Spring Boot configuration
- `src/main/resources/mapping-config.yml` — mapping configuration
- `src/main/resources/processes/chainvault.bpmn` — BPMN workflow definition
- `src/main/resources/db/migration` — SQL migration scripts (V1..V8) applied via Liquibase
- `test/` — unit and integration tests
- `docker-compose.yml` — main app stack (chainvault, postgres, sftp-test, fake-source-api)
- `docker-compose-monitoring.yml` — observability stack (Prometheus, Loki, Alloy, Grafana)
- `env/prometheus.yml` — Prometheus scrape config
- `run_chainvault_docker.sh` — script to build and run app + monitoring with Docker Compose

## Prerequisites
- Java 17+ (or the JDK version required by your environment)
- Git
- Docker & Docker Compose (optional, for containerized runs)

The project includes the Maven wrapper (`mvnw`), so a local Maven install is not required.

## Build
From the repository root:

```bash
./mvnw clean package -DskipTests
```

This produces a runnable JAR in `target/`.

## Run

Run the packaged JAR:

```bash
java -jar target/*-SNAPSHOT.jar
```

Or use Docker Compose to start services as defined in `docker-compose.yml`:

```bash
docker-compose up --build
```

To build and run the app stack together with the monitoring stack (Prometheus, Loki, Alloy, Grafana):

```bash
./run_chainvault_docker.sh
```

This runs `docker-compose -f docker-compose-monitoring.yml -f docker-compose.yml up -d` after a clean build.

## Configuration
- Main Spring configuration: `src/main/resources/application.yml`.
- Mapping and process definitions: `mapping-config.yml`, `processes/chainvault.bpmn`.
- Database migrations are in `src/main/resources/db/migration` and are applied via Liquibase on startup (see `src/main/resources/db/changelog/db.changelog-master.yaml`).
- OpenAPI (springdoc) is configured in `application.yml`; Swagger UI is at `/swagger-ui.html` when the app is running.

## Database
The repository contains SQL migration scripts under `src/main/resources/db/migration`. These are applied by Liquibase using the master changelog at `src/main/resources/db/changelog/db.changelog-master.yaml`. A database must be configured in `application.yml` (spring.datasource settings). Spring Boot's auto‑configuration creates a `DataSource` bean automatically, so no custom config class is required.

## Testing
Run tests with:

```bash
./mvnw test
```

Docker integration tests (Testcontainers) validate the full stack and individual services; see `src/test/java/ch/gryphus/chainvault/docker/README.md`. Run them with:

```bash
./mvnw test -Dtest=Docker*
```

## Observability
- **Prometheus**: Metrics are exposed at `/prometheus` (Micrometer). Use `docker-compose-monitoring.yml` to run Prometheus, Loki, Alloy, and Grafana; scrape config is in `env/prometheus.yml`.
- **OpenAPI**: REST API docs and Swagger UI at `/swagger-ui.html` (springdoc).

## Useful files
- Application entry: `src/main/java/ch/gryphus/chainvault/MigrationApplication.java`
- BPMN process: `src/main/resources/processes/chainvault.bpmn`
- Liquibase changelog: `src/main/resources/db/changelog/db.changelog-master.yaml` (includes `src/main/resources/db/migration`)
- OpenAPI config: `src/main/java/ch/gryphus/chainvault/config/OpenApiConfig.java`

## Contributing
PRs and issues are welcome. Follow the existing code style and add tests for significant changes.

## Maintainers
- gryphus-lab / gryphus-lab@users.noreply.github.com

---
_Generated README — edit with project-specific operational notes as needed._