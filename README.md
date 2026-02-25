# chainvault

Lightweight orchestration service for document migration and processing used by Gryphus Lab.

## Overview

`chainvault` is a Spring Boot-based migration/orchestration application that coordinates document extraction, transformation, signing, merging and secure transfer. It exposes BPMN workflows (see the process definition), integrates with SFTP targets, and records migration audit/events via database migrations.

Key responsibilities:
- Extract and hash documents
- Transform metadata and prepare files
- Merge PDFs and sign documents
- Upload artifacts to SFTP targets
- Record audit and event logs via Flyway migrations

## Repo layout
- `src/main/java` — application source, controllers, delegates and services
- `src/main/resources/application.yml` — Spring Boot configuration
- `src/main/resources/mapping-config.yml` — mapping configuration
- `src/main/resources/processes/chainvault.bpmn` — BPMN workflow definition
- `src/main/resources/db/migration` — Flyway migration scripts (V1..V8)
- `test/` — unit and integration tests

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

## Configuration
- Main Spring configuration: `src/main/resources/application.yml`.
- Mapping and process definitions: `mapping-config.yml`, `processes/chainvault.bpmn`.
- Database migrations are located in `src/main/resources/db/migration` and are applied via Flyway on startup.

## Database
The repository contains Flyway SQL migrations under `src/main/resources/db/migration`. A database must be configured in `application.yml` (datasource settings). See `DataSourceConfig.java` for programmatic datasource configuration.

## Testing
Run tests with:

```bash
./mvnw test
```

## Useful files
- Application entry: `src/main/java/ch/gryphus/chainvault/MigrationApplication.java`
- BPMN process: `src/main/resources/processes/chainvault.bpmn`
- Flyway migrations: `src/main/resources/db/migration`

## Contributing
PRs and issues are welcome. Follow the existing code style and add tests for significant changes.

## Maintainers
- gryphus-lab / gryphus-lab@users.noreply.github.com

---
_Generated README — edit with project-specific operational notes as needed._