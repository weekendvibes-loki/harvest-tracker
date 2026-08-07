# Harvest Tracker Backend

Enterprise REST API backend for **Harvest Tracker** — Multi-Fruit Harvest Management Platform. Built with Spring Boot 3.3, Java 21, and PostgreSQL 16.

---

## Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.3.4 (Spring Web, Spring Security, Spring Data JPA, Validation, Actuator)
- **Database**: PostgreSQL 16
- **Migrations**: Flyway 10
- **API Documentation**: Springdoc OpenAPI 3.0 / Swagger UI
- **Logging**: Logback with SLF4J MDC Request Correlation
- **Build System**: Apache Maven 3.9+

---

## Quick Start (Local Development)

### Prerequisites

1. Java 21 JDK installed and configured.
2. Docker & Docker Compose installed.
3. PostgreSQL 16 container running (port 5432).

### Running PostgreSQL

Start the database container from the project root:

```bash
docker compose up -d postgres
```

### Running the Application

Navigate to the `backend/` directory and execute:

```bash
mvn spring-boot:run
```

The application defaults to the `dev` profile on `http://localhost:8080`.

---

## Configuration Profiles

| Profile | Command / Flag | Purpose |
|---|---|---|
| `dev` | Default (`-Dspring.profiles.active=dev`) | Local development with PostgreSQL, debug logging, formatted SQL, Flyway auto-migrate. |
| `test` | `-Dspring.profiles.active=test` | Unit and integration testing setup. |
| `prod` | `-Dspring.profiles.active=prod` | Production environment requiring environment variables (`SPRING_DATASOURCE_URL`, etc.). |

---

## Environment Variables

| Variable | Description | Default (Dev) |
|---|---|---|
| `PORT` | HTTP Server Port | `8080` |
| `SPRING_PROFILES_ACTIVE` | Active Spring Profile | `dev` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC Connection String | `jdbc:postgresql://localhost:5432/harvest_tracker?options=-c%20timezone=UTC` |
| `SPRING_DATASOURCE_USERNAME` | Database Username | `harvest_user` |
| `SPRING_DATASOURCE_PASSWORD` | Database Password | `harvest_password` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS Origins | `http://localhost:3000` |

---

## System Endpoints & Documentation

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3.0 Spec**: `http://localhost:8080/v3/api-docs`
- **API Health Endpoint**: `http://localhost:8080/api/health`
- **Actuator Health**: `http://localhost:8080/actuator/health`
- **Actuator Liveness Probe**: `http://localhost:8080/actuator/health/liveness`
- **Actuator Readiness Probe**: `http://localhost:8080/actuator/health/readiness`

---

## Database Migrations (Flyway)

Migration scripts reside in `src/main/resources/db/migration/`.

- `V1__initial_schema.sql` — Initial relational schema with 31 tables, foreign key constraints, partial unique indexes, trigger functions, and master reference data seeds.

All future schema changes must follow the Flyway naming convention: `V{version}__{description}.sql` (e.g., `V2__add_index.sql`).

---

## Troubleshooting

### Timezone Error (`FATAL: invalid value for parameter "TimeZone": "Asia/Calcutta"`)
- **Cause**: Windows JVM passing obsolete timezone name to PostgreSQL 16.
- **Solution**: The application automatically sets JVM default timezone to `UTC` on startup. Ensure JDBC URL includes `?options=-c%20timezone=UTC`.
