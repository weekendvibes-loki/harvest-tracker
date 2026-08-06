# 09. API Guidelines

## Project Goal

Define how frontend and backend will communicate for Harvest Tracker.

## Purpose

This document ensures that APIs remain consistent, versioned, and easy to consume.

## Business Context

The product will have multiple modules and role-based actions. The API shape must be predictable and secure.

## Functional Requirements

- Provide REST endpoints for farms, harvests, workers, customers, orders, payments, and reports.
- Support authentication and authorization.
- Return structured JSON responses with clear errors.

## Non Functional Requirements

- Security and validation.
- Consistent response formats.
- Clear pagination and filtering.

## Business Rules

- All state-changing requests must require authentication.
- Validation failures must return structured error payloads.
- Financial operations must be logged and auditable.

## Acceptance Criteria

- The frontend can retrieve and submit data through stable endpoints.
- Errors are explicit and actionable.
- API responses are consistent across modules.

## Database Tables

- Not applicable

## API Endpoints

### Authentication
- POST /api/auth/login
- POST /api/auth/refresh
- POST /api/auth/logout

### Farms
- GET /api/farms
- POST /api/farms
- GET /api/farms/{id}
- PUT /api/farms/{id}

### Harvests
- GET /api/harvests
- POST /api/harvests
- GET /api/harvests/{id}
- PUT /api/harvests/{id}

### Workers
- GET /api/workers
- POST /api/workers
- GET /api/workers/{id}

### Sales
- GET /api/customers
- GET /api/orders
- POST /api/orders
- GET /api/payments

## UI Screens

- API-driven list and detail pages
- Form submission screens

## Implementation Strategy

Use RESTful resource-oriented endpoints with consistent JSON payloads. Put validation in the backend and return standard error responses. Add pagination and filtering to list endpoints.

### Response Format
```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully"
}
```

### Error Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Harvest quantity must be greater than zero"
  }
}
```

## Manual Tasks

- Define the initial versioning strategy.
- Decide on shared DTO structures for forms and list responses.
- Document common pagination query parameters.

## Monkey Code Prompt

Objective: Implement the initial API layer for authentication, farms, harvests, workers, and sales.

Current Project Context: The frontend requires a stable backend API for the initial release.

Files that already exist: docs/08-ui-guidelines.md

Files to create: controllers, DTOs, services, and API route definitions.

Business Rules: Enforce authentication, authorization, and structured validation.

Validation Rules: Reject invalid input with clear errors and status codes.

Coding Standards: Use service classes, DTOs, and consistent response shapes.

Expected Deliverables: Backend API endpoints for the initial modules.

## Testing Checklist

- Endpoints handle valid and invalid input correctly.
- Auth-protected routes reject unauthorized access.
- Response payloads are consistent.

## Git Commit Message

feat: add initial REST api for core modules

## Definition of Done

The API layer is consistent, secure, and ready for UI integration.

## Next Phase

Use the API design to set up the development workflow and implementation phases.
