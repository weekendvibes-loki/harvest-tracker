# 12. Phase 01 — Project Setup

## Project Goal

Create the initial project foundation for Harvest Tracker so the later phases can be implemented safely.

## Purpose

This phase establishes the repository structure, tooling, environment setup, and the baseline for all future work.

## Business Context

A weak project foundation causes delays and inconsistent implementation. This phase ensures that the team starts from a reliable baseline.

## Functional Requirements

- Initialize the frontend and backend projects.
- Add base configuration for environment variables and package management.
- Create the initial docs and test folders.

## Non Functional Requirements

- The setup must be reproducible.
- The initial structure must be easy to extend.
- The setup must support both local development and later deployment.

## Business Rules

- The frontend and backend must be separate modules.
- Shared configuration must be explicit and documented.
- Secrets must not be committed into the repository.

## Acceptance Criteria

- The repository contains a clear frontend and backend structure.
- The environment can be started locally with documented commands.
- The initial folder structure matches the architecture plan.

## Database Tables

- Not applicable

## API Endpoints

- Not applicable

## UI Screens

- Initial app shell placeholder

## Implementation Strategy

Set up the project skeleton first. Create the frontend app, backend module, initial config files, environment example files, and placeholder screens. Do not implement full business features yet.

## Manual Tasks

- Install required local tools.
- Create environment variable examples.
- Confirm the repo can be cloned and started cleanly.

## Monkey Code Prompt

Objective: Initialize the frontend and backend project skeleton for Harvest Tracker.

Current Project Context: The repository currently only has a README and the documentation set. The task is to prepare the project for subsequent feature implementation.

Files that already exist: README.md and docs folder.

Files to create: frontend project files, backend starter files, environment examples, and basic app shell pages.

Business Rules: Keep the structure modular, secure, and future-proof.

Validation Rules: Application should start with default placeholders and no broken paths.

Coding Standards: Use Next.js 16, TypeScript, Tailwind CSS, Spring Boot, Java 21, and PostgreSQL-compatible configuration patterns.

UI Requirements: Provide a basic shell with navigation placeholders and a clean layout.

Backend Requirements: Provide a simple health endpoint and base configuration.

API Requirements: Add a basic /api/health endpoint.

Database Rules: No production data is required yet; use configuration placeholders only.

Acceptance Criteria: The project can be started locally without missing dependencies or broken imports.

What NOT to modify: Do not implement business modules yet. Do not alter the architecture documents.

Expected Deliverables: Scaffolding for frontend, backend, environment setup, and initial health endpoint.

Testing Requirements: Verify the app starts successfully and the health endpoint returns a response.

Stopping Conditions: Stop when the project can run locally and the base structure is verified.

## Testing Checklist

- Frontend starts successfully.
- Backend health endpoint works.
- Environment examples are complete.

## Git Commit Message

chore: scaffold project foundation and health endpoint

## Definition of Done

The repository has a working local baseline for all subsequent phases.

## Next Phase

Proceed to authentication and user roles.
