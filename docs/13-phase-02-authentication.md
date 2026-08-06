# 13. Phase 02 — Authentication

## Project Goal

Implement secure authentication and role-based access control for Harvest Tracker.

## Purpose

This phase ensures that only authorized users can view and modify farm, harvest, worker, sales, and financial information.

## Business Context

The product handles sensitive operational and financial data. Authentication and authorization are therefore mandatory foundations for all later modules.

## Functional Requirements

- Support user login and logout.
- Assign roles to users.
- Protect routes and API endpoints based on roles.
- Support JWT-based authentication.

## Non Functional Requirements

- Secure password handling.
- Low latency for authentication requests.
- Clear error messages without exposing internal details.

## Business Rules

- Admins can manage users and roles.
- Farm owners, managers, and supervisors have role-specific permissions.
- Only authenticated users may access protected routes.
- Invalid credentials must not reveal whether the email or password is wrong.

## Acceptance Criteria

- A user can log in and receive a valid session token.
- Protected pages redirect unauthenticated users.
- Role-based access prevents unauthorized actions.

## Database Tables

- users
- roles
- permissions

## API Endpoints

- POST /api/auth/login
- POST /api/auth/logout
- POST /api/auth/refresh
- GET /api/auth/me

## UI Screens

- Login screen
- Role-based landing page
- Access denied state

## Implementation Strategy

Implement authentication on the backend first using Spring Security and JWT. Then connect the Next.js frontend with login flow, route guards, and user context. Keep role checks centralized.

## Manual Tasks

- Define the initial role matrix.
- Create default admin credentials for local development.
- Prepare password reset flow requirements.

## Monkey Code Prompt

Objective: Implement authentication, JWT handling, role-based routing, and a login UI.

Current Project Context: The base project exists and now requires secure access control for all future features.

Files that already exist: docs/12-phase-01-project-setup.md and the scaffolded frontend/backend structure.

Files to create: authentication controllers, security config, JWT utilities, user entity, login page, auth context, and route guards.

Business Rules: Enforce role-based access and protect sensitive actions.

Validation Rules: Reject missing credentials, invalid tokens, and unauthenticated requests.

Coding Standards: Use Spring Security patterns, typed frontend state, and centralized API service calls.

UI Requirements: Provide a clean login page, loading state, and access denied page.

Backend Requirements: Generate and validate JWT tokens, manage refresh tokens, and expose user profile data.

API Requirements: Return structured auth errors and consistent response payloads.

Database Rules: Store user credentials only as hashed values and preserve role references.

Acceptance Criteria: Users can authenticate and access only the pages their role permits.

What NOT to modify: Do not implement farm or harvest business logic in this phase.

Expected Deliverables: Auth backend, login UI, and protected route handling.

Testing Requirements: Verify login success, invalid credentials handling, and role-based access.

Stopping Conditions: Stop once authentication is working end-to-end and role-based access is enforced.

## Testing Checklist

- Valid login returns a valid token.
- Invalid login returns a controlled error.
- Unauthorized users cannot access protected routes.

## Git Commit Message

feat: add jwt authentication and role-based access

## Definition of Done

The application has a secure authentication layer and baseline role-based access control.

## Next Phase

Proceed to farm management.
