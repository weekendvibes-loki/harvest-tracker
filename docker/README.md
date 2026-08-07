# Docker

This folder contains service-specific Docker configuration files.

## Structure

```
docker/
    postgres/       PostgreSQL init scripts and custom configuration
    backend/        Backend Dockerfile and build context overrides
    frontend/       Frontend Dockerfile and build context overrides
```

## docker-compose.yml

The root `docker-compose.yml` remains at the project root as required by Docker Compose defaults.
It orchestrates all services together for local development.

To start all services:
```bash
docker compose up -d
```

## Dockerfiles (Phase 1+)

Service-specific Dockerfiles will be placed here as the project matures.

Example paths:
```
docker/backend/Dockerfile
docker/frontend/Dockerfile
docker/postgres/init.sql
```

These files are referenced from `docker-compose.yml` using the `build.context` and `dockerfile` options.

## Notes

- Never commit secrets or credentials to Docker files.
- Use `.env` at the project root for environment variable injection.
- Production Docker configuration will be handled separately during the deployment phase.
  See `docs/21-deployment.md` for deployment architecture.
