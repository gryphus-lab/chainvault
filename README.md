# chainvault

Lightweight orchestration service for document migration and processing used by Gryphus Lab.

## Overview

`chainvault` is a Spring Boot-based migration/orchestration application that coordinates document extraction,
transformation, signing, merging and secure transfer. It exposes BPMN workflows (see the process definition), integrates
with SFTP targets, and records migration audit/events via database migrations.

Key responsibilities:

- Extract and hash documents
- Transform metadata and prepare files
- Merge PDFs and sign documents
- Upload artifacts to SFTP targets
- Record audit and event logs via Liquibase migrations

[![Java 25](https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.0+-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Liquibase](https://img.shields.io/badge/Liquibase-managed-2962FF)](https://www.liquibase.org/)
[![mise](https://img.shields.io/badge/managed%20with-mise-6f42c1)](https://mise.jdx.dev/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=gryphus-lab_chainvault&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=gryphus-lab_chainvault)
[![GitHub Actions CI](https://github.com/gryphus-lab/chainvault/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/gryphus-lab/chainvault/actions/workflows/ci.yml)

**Clean, modern, production-grade multi-module Spring Boot application** built with **Java 25**, strict modularization, real PostgreSQL for local development, Liquibase for schema versioning, **mise** for frictionless developer environments, and a full quality & deployment pipeline.

## Key Highlights

- Java 25 first-class support (virtual threads, scoped values, ZGC improvements, faster startup)
- Spring Boot 4.x (Jakarta EE 11, modular JARs, observability enhancements, better native support)
- Multi-module Maven structure (separation of concerns: domain, services, API)
- **No H2 in development** – real PostgreSQL via Docker Compose
- Liquibase YAML changelogs (repeatable, rollback-capable, team-safe)
- **mise** as single source of truth for tools, versions, tasks & environment
- GitHub Actions CI with:
  - Consistent tool setup via mise
  - Full test suite + JaCoCo coverage
  - SonarCloud analysis + **enforced quality gate on new code**
  - Multi-stage Docker build → GHCR push (main branch only)
- Developer-first local experience: one command starts everything

## Project Structure

- `chainvault-migration/` — migration service module: domain, extraction/transform/merge/sign/upload logic, SFTP and
  REST client config
- `chainvault-orchestration/` — Flowable BPMN orchestration module: process definition, delegates, REST API, main
  application
- `chainvault-report-aggregate/` — JaCoCo aggregate module combining coverage from `chainvault-migration` and
  `chainvault-orchestration` (unit + integration/Docker tests), used for CI/Sonar
- `docker-compose.yml` — main app stack (chainvault, postgres, sftp-test, fake-source-api)
- `docker-compose-monitoring.yml` — observability stack (Prometheus, Loki, Alloy, Grafana)
- `env/prometheus.yml` — Prometheus scrape config## Prerequisites

## Configuration

- Main Spring configuration: `chainvault-orchestration/src/main/resources/application.yml`.
- Mapping and process definitions: `mapping-config.yml`, `processes/chainvault.bpmn` (in orchestration module).
- Database migrations are in `chainvault-orchestration/src/main/resources/db/migration` and are applied via Liquibase on
  startup (see `db/changelog/db.changelog-master.yaml`).
- OpenAPI (springdoc) is configured in `application.yml`; Swagger UI is at `/swagger-ui.html` when the app is running.

## Database

The repository contains SQL migration scripts under `src/main/resources/db/migration`. These are applied by Liquibase
using the master changelog at `src/main/resources/db/changelog/db.changelog-master.yaml`. A database must be configured
in `application.yml` (spring.datasource settings). Spring Boot's auto‑configuration creates a `DataSource` bean
automatically, so no custom config class is required.

## Prerequisites

- Docker & Docker Compose (v2+)
- [mise](https://mise.jdx.dev/) – the modern replacement for asdf + direnv + nvm + pyenv + ...
- Git
- IDE of choice (IntelliJ IDEA Ultimate recommended for Spring Boot)

Install mise (one time per machine):

```bash
curl https://mise.run | sh
# Follow shell setup instructions (usually one line for .zshrc / .bashrc / config.fish)
mise doctor     # should confirm Java 25 + Maven are ready
```

## Quick Start

```bash
git clone https://github.com/gryphus-lab/chainvault.git
cd chainvault

mise install
mise trust

mise dev
```

## Testing

Run unit and integration tests with:

```bash
mise test
```

Docker integration tests (Testcontainers) validate the full stack and individual services; see
`chainvault-orchestration/src/test/java/ch/gryphus/chainvault/docker/README.md`. Run them with:

```bash
mise test-docker
```

### Test coverage (JaCoCo, multi-module)

To generate JaCoCo coverage reports (including integration/Docker tests) across all modules, use the `coverage` profile
from the project root:

```bash
mise verify
```

This will:

- Attach JaCoCo agents for both unit and integration tests in each module
- Generate per-module reports, and
- Produce an aggregated report in the `chainvault-report-aggregate` module (HTML + XML) suitable for CI/Sonar
  consumption.

## Docker Usage

```bash
mise docker-build
```

## Observability

- **Prometheus**: Metrics are exposed at `/prometheus` (Micrometer). Use `docker-compose-monitoring.yml` to run
  Prometheus, Loki, Alloy, and Grafana; scrape config is in `env/prometheus.yml`.
- **OpenAPI**: REST API docs and Swagger UI at `/swagger-ui.html` (springdoc).

## Useful files

- Application entry: `chainvault-orchestration/src/main/java/ch/gryphus/chainvault/MigrationApplication.java`
- BPMN process: `chainvault-orchestration/src/main/resources/processes/chainvault.bpmn`
- Liquibase changelog: `chainvault-orchestration/src/main/resources/db/changelog/db.changelog-master.yaml`
- OpenAPI config: `chainvault-orchestration/src/main/java/ch/gryphus/chainvault/config/OpenApiConfig.java`
- Migration service: `chainvault-migration/src/main/java/ch/gryphus/chainvault/service/MigrationService.java`

## Contributing

PRs and issues are welcome. Follow the existing code style and add tests for significant changes.

## Maintainers

- gryphus-lab / <gryphus-lab@users.noreply.github.com>

---

_Generated README — edit with project-specific operational notes as needed._
