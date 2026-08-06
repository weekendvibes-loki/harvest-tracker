# 21. Deployment

## Project Goal

Define the production deployment approach for Harvest Tracker.

## Purpose

This document helps the team deploy the application safely using the proposed cloud hosting stack.

## Business Context

The application will eventually host operational and financial data. Deployment must be secure, repeatable, and observable.

## Functional Requirements

- Deploy the frontend to Vercel.
- Deploy the backend to Railway.
- Configure PostgreSQL and environment variables.
- Set up CI/CD with GitHub Actions.

## Non Functional Requirements

- Secure runtime configuration.
- Reliable health checks.
- Clear logs and deployment visibility.

## Business Rules

- Production secrets must not be stored in the repo.
- Database migrations must run before the new application version goes live.
- Deployment should include smoke tests.

## Acceptance Criteria

- The frontend and backend are reachable in the target environment.
- Environment variables are configured.
- The health endpoint works in production.

## Database Tables

- Not applicable

## API Endpoints

- /api/health

## UI Screens

- Production login page

## Implementation Strategy

Use Vercel for the frontend, Railway for the backend, and PostgreSQL for persistence. Add GitHub Actions to run tests and deploy on merge. Keep deployment settings in environment-based config files.

## Manual Tasks

- Set up hosting accounts and domains.
- Configure secrets and deployment variables.
- Prepare rollback and backup procedures.

## Monkey Code Prompt

Objective: Configure deployment workflow, production environment settings, and deployment automation.

Current Project Context: The product has passed the implementation phases and is ready for release.

Files that already exist: docs/20-testing.md and the application code.

Files to create: CI workflow files, deployment config, environment templates, and deployment checklist.

Business Rules: Keep secrets out of source control and ensure migrations run safely.

Validation Rules: Verify deployment health and application availability after rollout.

Coding Standards: Keep deployment scripts clear and reusable.

UI Requirements: Ensure the production build is healthy and login works.

Backend Requirements: Expose a health endpoint and ensure environment configuration is valid.

API Requirements: Ensure deployed APIs return healthy responses.

Database Rules: Use migration execution for schema changes.

Acceptance Criteria: The application can be deployed and is accessible in the target environment.

What NOT to modify: Do not bypass security checks or skip rollback planning.

Expected Deliverables: Deployment config and production-ready release workflow.

Testing Requirements: Validate the production deployment with smoke tests.

Stopping Conditions: Stop once the system is deployed and health checks pass.

## Testing Checklist

- Frontend deployment works.
- Backend deployment works.
- Database connection is correct.

## Git Commit Message

chore: add deployment workflow for vercel and railway

## Definition of Done

The system is deployable and its release process is documented and repeatable.

## Next Phase

Plan the roadmap and future expansion.
