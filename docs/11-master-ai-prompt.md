# 11. Master AI Prompt

## Project Goal

Provide one reusable implementation prompt for future AI-driven development work.

## Purpose

This document allows another AI agent to continue implementation without needing to re-explain the product context each time.

## Business Context

Harvest Tracker is a practical platform for farm harvest management with future multi-fruit support. The system must be built incrementally and remain adaptable.

## Functional Requirements

- Implement features according to the documented phase plan.
- Follow the architecture, standards, and business rules already defined.
- Keep future fruit support in mind during implementation.

## Non Functional Requirements

- Maintain readable code and clean module boundaries.
- Avoid shortcuts that create rework later.
- Respect role-based access and data validation requirements.

## Business Rules

- Backend must enforce business rules.
- Frontend must be role-aware and user-friendly.
- No feature should ignore validation or audit needs.

## Acceptance Criteria

- The implementation aligns with the current phase document.
- The code follows the defined standards and folder structure.
- Testing and documentation are included.

## Database Tables

- Use the schema defined in the database design document.

## API Endpoints

- Implement endpoints according to the API guidelines.

## UI Screens

- Build screens according to the current phase requirements and UI guidelines.

## Implementation Strategy

Use this prompt as the default starting point for any implementation task. Replace the phase-specific details with the current phase document before execution.

## Manual Tasks

- Read the relevant phase document before coding.
- Review the architecture and database documents.
- Confirm acceptance criteria before implementation.

## Monkey Code Prompt

Objective: Execute the next implementation phase for Harvest Tracker using the existing documentation set.

Current Project Context: The project is in early development and should be built in phased milestones. The system must support mango farms now and later support other fruits.

Files that already exist: docs/README.md and all prior phase docs.

Files to create: Task-specific source files, tests, and documentation updates based on the phase being implemented.

Business Rules: Follow role-based access, validation, and audit expectations.

Validation Rules: Use backend validation and UI validation. Do not accept incomplete or conflicting data.

Coding Standards: Follow the documented frontend, backend, API, and UI standards.

UI Requirements: Build practical screens with clear navigation, forms, and status visibility.

Backend Requirements: Keep logic in services, validate inputs, and use repositories for persistence.

API Requirements: Use consistent REST payloads, authentication, and structured errors.

Database Rules: Use the documented schema and keep relationships explicit.

Acceptance Criteria: Complete the current phase and verify it against the documentation checklist.

What NOT to modify: Do not change unrelated modules, do not remove the documented architecture, and do not skip tests.

Expected Deliverables: Working implementation for the current phase, tests, and updated documentation if needed.

Testing Requirements: Run relevant tests and verify the acceptance criteria manually or via automated checks.

Stopping Conditions: Stop when the phase is implemented, verified, and documented. If a requirement is unclear, pause and ask for clarification rather than guessing.

## Testing Checklist

- The task is implemented according to the selected phase.
- The resulting code is consistent with existing standards.
- Tests or manual checks confirm the result.

## Git Commit Message

feat: implement current phase using project playbook

## Definition of Done

The AI agent can continue implementation using this prompt without needing additional setup from the human team.

## Next Phase

Use this prompt for each following implementation phase.
