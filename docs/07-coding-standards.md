# 07. Coding Standards

## Project Goal

Define the coding conventions that will keep the implementation consistent and maintainable.

## Purpose

This document helps another AI agent or developer write code that fits the project without introducing style drift or architectural inconsistency.

## Business Context

The platform will be built in phases. Good standards reduce rework, simplify reviews, and help the system evolve into a larger multi-fruit product.

## Functional Requirements

- Standardized naming and structure.
- Consistent validation and error handling.
- Clear separation of UI and backend responsibilities.

## Non Functional Requirements

- Readability and maintainability.
- Predictable code organization.
- Safe changes as modules grow.

## Business Rules

- All business rules must be implemented on the backend.
- Frontend code should not bypass validation.
- Sensitive data must never be exposed in the UI.

## Acceptance Criteria

- New code follows naming and structure guidelines.
- Validation and error messages are consistent.
- The project remains easy to extend.

## Database Tables

- Not applicable

## API Endpoints

- Not applicable

## UI Screens

- Not applicable

## Implementation Strategy

Adopt a clear structure and conventions for both frontend and backend code.

### Frontend Standards
- Use Next.js App Router where possible.
- Use TypeScript for all components and utilities.
- Prefer functional components and hooks.
- Use Tailwind CSS for styling.
- Use shadcn/ui for common UI primitives.

### Backend Standards
- Use Java 21 and Spring Boot.
- Keep controllers thin and move logic to services.
- Use DTOs for request and response payloads.
- Use repository interfaces for persistence access.
- Use meaningful exception handling and validation.

### General Standards
- Use descriptive names.
- Avoid large files with mixed responsibility.
- Add comments only when the purpose is not obvious.
- Write tests for business logic and critical API behavior.

## Manual Tasks

- Set up linting and formatting rules.
- Agree on import ordering and naming.
- Document common error codes.

## Monkey Code Prompt

Objective: Create the initial coding standards and project configuration files for frontend and backend.

Current Project Context: The project is a multi-phase web application intended for long-term maintenance.

Files that already exist: docs/06-database-design.md

Files to create: ESLint and Prettier configuration, Java style guidance, and example service/controller patterns.

Business Rules: Enforce validation and service-layer rules.

Validation Rules: Enforce type safety, required fields, and no silent failures.

Coding Standards: Follow TypeScript, Java, and REST API conventions.

Expected Deliverables: Linting config and coding conventions document.

## Testing Checklist

- Formatting and linting rules are applied.
- Code reviews can follow the standards easily.
- The implementation remains readable and consistent.

## Git Commit Message

chore: add coding standards and linting setup guidance

## Definition of Done

The codebase has a consistent baseline for future implementation.

## Next Phase

Use these standards during the setup and authentication phase.
