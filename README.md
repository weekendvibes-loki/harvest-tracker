# Harvest Tracker

A modular harvest management platform for mango farms, designed to evolve into a generic multi-fruit harvest management system.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Frontend | Next.js 16, TypeScript, Tailwind CSS, shadcn/ui |
| Backend | Spring Boot 3, Java 21, Spring Security |
| Database | PostgreSQL 16 (Flyway migrations) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Containerization | Docker, Docker Compose |
| CI/CD | GitHub Actions |

---

## Project Structure

```
harvest-tracker/
│
├── .github/
│   └── workflows/
│       ├── backend.yml         CI workflow for Spring Boot backend
│       └── frontend.yml        CI workflow for Next.js frontend
│
├── backend/                    Spring Boot 3 + Java 21 API
│   └── src/main/java/com/harvesttracker/
│       ├── HarvestTrackerApplication.java
│       ├── common/             Shared cross-cutting code
│       │   ├── config/         Spring configuration beans
│       │   ├── constants/      Application-wide constants
│       │   ├── dto/            Shared response wrappers
│       │   ├── exception/      Global exception handling
│       │   ├── health/         Health check endpoint
│       │   ├── security/       Security configuration
│       │   └── util/           Shared utility classes
│       └── features/           Feature First modules
│           ├── auth/           Authentication and authorization
│           ├── farm/           Farm management
│           ├── harvest/        Harvest tracking
│           ├── worker/         Worker and attendance
│           ├── sales/          Orders, sales, and payments
│           ├── dashboard/      Analytics and summaries
│           └── reports/        Operational reports
│               └── {controller,service,repository,entity,dto,mapper,validation}/
│
├── frontend/                   Next.js 16 + TypeScript UI
│   ├── app/                    Next.js App Router (pages and layouts)
│   ├── components/
│   │   ├── layout/             Shell, header, sidebar, footer
│   │   ├── ui/                 Shared UI primitives (shadcn/ui)
│   │   └── charts/             Chart components
│   ├── features/               Feature First modules
│   │   ├── auth/
│   │   ├── farm/
│   │   ├── harvest/
│   │   ├── worker/
│   │   ├── sales/
│   │   ├── dashboard/
│   │   └── reports/
│   │       └── {api,components,hooks,pages,types,utils}/
│   ├── lib/                    Shared utilities and env config
│   ├── services/               API client setup
│   └── types/                  Global TypeScript types
│
├── database/                   Database management
│   ├── migrations/             Flyway migration scripts (V1__*.sql)
│   ├── schema/                 Reference schema exports
│   ├── seeds/                  Development seed data
│   ├── diagrams/               ERD and visual diagrams
│   └── backups/                Local backups (gitignored)
│
├── docker/                     Service-specific Docker config
│   ├── postgres/               PostgreSQL Dockerfile / init scripts
│   ├── backend/                Backend Dockerfile
│   └── frontend/               Frontend Dockerfile
│
├── docs/                       Project documentation
│   ├── architecture/           System diagrams and ADRs
│   ├── api/                    API design reference
│   ├── database/               Database design reference
│   ├── prompts/                AI implementation prompts
│   ├── decisions/              Architecture Decision Records
│   └── *.md                    Phase and reference documents
│
├── scripts/                    Development automation scripts
│   ├── setup.{sh,ps1}          Install dependencies and check software
│   ├── start-dev.{sh,ps1}      Start all development services
│   ├── stop-dev.{sh,ps1}       Stop all development services
│   ├── reset-db.{sh,ps1}       Wipe and restart database
│   └── clean.{sh,ps1}          Remove build artifacts
│
├── docker-compose.yml          Local development service orchestration
├── .env.example                Environment variable template
└── .gitignore
```

---

## Development Workflow

```
main ──────────────────────────────────────────────────────► stable
  └── develop ──────────────────────────────────────────────► integration
        └── feature/phase-01-setup ──► (PR) ──► develop
        └── feature/phase-02-auth   ──► (PR) ──► develop
```

1. Branch from `develop` using naming: `feature/{phase}-{description}`
2. Implement the phase according to the matching document in `docs/`
3. Run linting, tests, and manual checks
4. Open a pull request with a clear summary
5. Merge only after review and all CI checks pass

---

## Development Commands

### First-Time Setup

```bash
# Linux / macOS
bash scripts/setup.sh

# Windows (PowerShell)
.\scripts\setup.ps1
```

### Start Development Environment

```bash
# Linux / macOS
bash scripts/start-dev.sh

# Windows (PowerShell)
.\scripts\start-dev.ps1
```

Services will start at:
| Service | URL |
|---|---|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| PostgreSQL | localhost:5432 |

### Stop Development Environment

```bash
# Linux / macOS
bash scripts/stop-dev.sh

# Windows (PowerShell)
.\scripts\stop-dev.ps1
```

### Reset Database

```bash
# Linux / macOS
bash scripts/reset-db.sh

# Windows (PowerShell)
.\scripts\reset-db.ps1
```

### Clean Build Artifacts

```bash
# Linux / macOS
bash scripts/clean.sh

# Windows (PowerShell)
.\scripts\clean.ps1
```

### Manual Commands

```bash
# Frontend only
cd frontend
npm install
npm run dev

# Backend only
cd backend
mvn spring-boot:run

# Database only
docker compose up -d
```

---

## Folder Explanation

| Folder | Purpose |
|---|---|
| `backend/src/.../common/` | Cross-cutting infrastructure code shared across all features |
| `backend/src/.../features/` | Business domain modules — one folder per feature |
| `frontend/features/` | Feature-isolated UI modules — colocates API, hooks, and components |
| `frontend/components/ui/` | Shared design system components (shadcn/ui primitives) |
| `database/migrations/` | Flyway versioned SQL migrations (`V1__*.sql`) |
| `database/seeds/` | Development seed data scripts |
| `docker/` | Service-specific Dockerfiles and init scripts |
| `docs/architecture/` | System diagrams and Architecture Decision Records (ADRs) |
| `docs/decisions/` | Structured ADR files explaining key technical decisions |
| `scripts/` | Lightweight automation for common developer tasks |

---

## Contribution Guide

1. **Read the docs first.** Start with `docs/README.md` and the relevant phase document before writing code.
2. **Follow Feature First.** All new business code lives in `features/{module}/`. Do not add business logic to `common/`.
3. **Keep common clean.** `common/` is for infrastructure, not features. If unsure, ask.
4. **Never skip tests.** All business logic must have unit tests. All APIs must have integration tests.
5. **Update docs with changes.** If the scope changes, update the relevant phase document.
6. **One feature per branch.** Do not mix multiple features in one pull request.
7. **Follow naming conventions.** See `docs/07-coding-standards.md` for frontend and backend rules.
8. **Never commit secrets.** Use `.env` (gitignored). Never hardcode credentials.

---

## Notes

This project is built in phases. The scaffold is intentionally minimal.

No business logic or CRUD has been implemented yet.

See `docs/README.md` for the full documentation map and recommended reading order.
