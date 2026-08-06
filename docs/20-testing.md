# 20. Testing

## Project Goal

Define the testing strategy for Harvest Tracker across unit, integration, and user validation.

## Purpose

This document ensures the implementation is reliable, maintainable, and safe to release.

## Business Context

The system handles operational and financial data. Testing is essential to prevent errors that could affect harvest records, worker payments, and customer invoices.

## Functional Requirements

- Write tests for business logic.
- Verify API endpoints and authentication flows.
- Validate key UI workflows.
- Ensure reports and dashboards reflect accurate data.

## Non Functional Requirements

- Tests should be stable and repeatable.
- Time to execute tests should remain practical.
- The system should be testable in local and CI environments.

## Business Rules

- Business rules must be covered by automated tests.
- Validation errors must be tested for key inputs.
- Protected routes must enforce access control.

## Acceptance Criteria

- Core modules have automated coverage.
- At least the critical paths are verified before deployment.
- Test results are visible in CI.

## Database Tables

- Not applicable

## API Endpoints

- Not applicable

## UI Screens

- Not applicable

## Implementation Strategy

Use unit tests for business logic and service layers, integration tests for APIs and database interactions, and end-to-end tests for the most important flows. Add a basic smoke test suite for authentication, farm creation, harvest entry, and order/payment handling.

### Test Types
- Unit tests: validation rules, service logic
- Integration tests: controllers, repositories, security
- End-to-end tests: login, dashboard, farm creation, harvest entry, sales flow

## Manual Tasks

- Review manually with sample business data.
- Check reports against known outcomes.
- Validate role-based access with test users.

## Monkey Code Prompt

Objective: Implement the initial automated test strategy and add tests for the core modules.

Current Project Context: The application is now feature-complete at the initial release scope and needs verification before deployment.

Files that already exist: docs/19-phase-08-reports.md and the implemented modules.

Files to create: test folders, unit tests, integration tests, and CI test configuration.

Business Rules: Cover business rules and validation behavior.

Validation Rules: Verify both success and failure paths.

Coding Standards: Keep tests readable and focused on behavior.

UI Requirements: Cover key user journeys for login, dashboards, farms, harvests, workers, and sales.

Backend Requirements: Test endpoints, service rules, and database interactions.

API Requirements: Verify request and response behavior.

Database Rules: Use test databases or fixtures for persistence-related tests.

Acceptance Criteria: Critical flows are covered and passing.

What NOT to modify: Do not weaken validation or skip security tests.

Expected Deliverables: Automated test suite and CI integration.

Testing Requirements: Run tests locally and in CI before release.

Stopping Conditions: Stop once critical flows are verified and the release is ready.

## Testing Checklist

- Authentication tests pass.
- Farm, harvest, worker, and sales flows pass.
- Reports and dashboard outputs are verified.

## Git Commit Message

test: add core automated test suite

## Definition of Done

The platform has a practical test foundation that protects the release.

## Next Phase

Proceed to deployment and rollout.
