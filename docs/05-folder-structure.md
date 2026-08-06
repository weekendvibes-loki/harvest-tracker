# 05. Folder Structure

## Project Goal

Provide a clear and scalable folder structure for the implementation team.

## Purpose

This document prevents confusion during implementation and helps maintain separation between frontend, backend, tests, and documentation.

## Business Context

The project will grow across multiple modules and phases. A predictable structure is essential for maintainability.

## Functional Requirements

- Keep feature modules isolated.
- Separate shared utilities from feature-specific code.
- Keep documentation and tests close to the implementation.

## Non Functional Requirements

- Easy onboarding for new developers.
- Clear ownership of files and responsibilities.
- Support for future extension into fruit-specific modules.

## Business Rules

- Shared code should live in common folders.
- Feature-specific code should not be mixed into global utilities.
- Environment-specific configuration should stay outside source code.

## Acceptance Criteria

- A new developer can find the main app entry points quickly.
- Backend modules can be added without rearranging the whole project.
- UI components and pages are easy to locate.

## Database Tables

- Not applicable

## API Endpoints

- Not applicable

## UI Screens

- Not applicable

## Implementation Strategy

Use a standard monorepo-style structure with distinct frontend and backend folders. Keep a shared docs folder at the repository root and separate test folders for unit and integration coverage.

```text
harvest-tracker/
  docs/
  frontend/
    app/
    components/
    lib/
    hooks/
    services/
    styles/
    types/
  backend/
    src/main/java/com/harvesttracker/
      auth/
      farms/
      harvests/
      workers/
      sales/
      reports/
      common/
      config/
  database/
  tests/
```

## Manual Tasks

- Create the initial folder structure before implementation begins.
- Decide where shared UI components will live.
- Keep environment files out of version control.

## Monkey Code Prompt

Objective: Create the repository structure and base folders for frontend, backend, tests, and docs.

Current Project Context: The repository currently contains only the project README.

Files that already exist: README.md

Files to create: frontend and backend folder structure, placeholder files, and config folders.

Business Rules: Keep module boundaries clear and avoid cross-coupling.

Validation Rules: Ensure each folder has a clear purpose and no misplaced files.

Coding Standards: Use consistent naming and keep shared code under common folders.

Expected Deliverables: A complete project folder structure ready for implementation.

## Testing Checklist

- Files are placed in the expected folders.
- The structure supports future growth.
- New modules can be added without ambiguity.

## Git Commit Message

chore: scaffold frontend backend and docs folders

## Definition of Done

The repository structure is clear, consistent, and ready for implementation by phase.

## Next Phase

Use this structure to define the database design and initial implementation modules.
