# Harvest Tracker

## Overview

Harvest Tracker is a modular platform for farm harvest operations. This repository currently contains the project scaffold for the frontend, backend, database configuration, and documentation foundation.

## Structure

- frontend/ — Next.js 16 + TypeScript UI scaffold
- backend/ — Spring Boot 3 + Java 21 API scaffold
- db/ — migration folder placeholder
- docs/ — implementation and architecture documentation

## Getting Started

### Frontend

```bash
cd frontend
npm install
npm run build
```

### Backend

```bash
cd backend
mvn clean verify
```

### Database

```bash
docker compose up -d
```

## Notes

No business logic or CRUD implementation has been added. The scaffold is intentionally minimal and ready for the next implementation phase.
