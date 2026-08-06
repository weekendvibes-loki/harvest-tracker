# 16. Phase 05 — Worker Management

## Project Goal

Implement worker management, attendance tracking, and wage payment workflows.

## Purpose

This phase ensures that field workers are properly tracked, paid, and connected to harvest operations.

## Business Context

Worker management is critical for operational control and payroll. The system must support daily attendance records and payment statuses in a clear way.

## Functional Requirements

- Create and manage worker profiles.
- Record attendance for selected dates.
- Track daily wage and payment status.
- Link workers to assigned harvests.

## Non Functional Requirements

- The data capture process should be fast.
- Payroll data should be reliable and auditable.
- The module should support future wage rules and worker categories.

## Business Rules

- A worker must have a unique business identity within the scope.
- Attendance records must reference a worker and a date.
- Wage payments must be tied to a worker and a payment period.
- A worker can be assigned to multiple harvests but should not be double-counted in one harvest batch.

## Acceptance Criteria

- A manager can create a worker record.
- Attendance can be recorded and reviewed.
- Worker payments can be marked as pending, paid, or cancelled.

## Database Tables

- workers
- worker_attendance
- worker_payments

## API Endpoints

- GET /api/workers
- POST /api/workers
- GET /api/workers/{id}
- POST /api/workers/attendance
- GET /api/workers/payments

## UI Screens

- Worker list page
- Worker detail page
- Attendance capture screen
- Payment summary screen

## Implementation Strategy

Implement worker CRUD first, then attendance and payment workflows. Keep the business logic server-side so payroll operations are consistent and auditable.

## Manual Tasks

- Define wage calculation rules.
- Prepare sample worker datasets.
- Decide how attendance statuses will be represented.

## Monkey Code Prompt

Objective: Build the worker management workflow including worker records, attendance, and wage payment tracking.

Current Project Context: Harvest operations are now recorded and the business needs a workforce layer for attendance and payments.

Files that already exist: docs/15-phase-04-harvest-management.md and the current application structure.

Files to create: worker entity, attendance records, payment entities, service layer, list/detail screens, attendance form, and payment summary UI.

Business Rules: Enforce valid attendance and payment states, and prevent double-counting in a single harvest batch.

Validation Rules: Reject invalid dates, missing worker references, and invalid wage values.

Coding Standards: Keep payment logic centralized and auditable.

UI Requirements: Provide clear worker and attendance screens with compact summaries.

Backend Requirements: Persist worker, attendance, and payment data with proper relationships.

API Requirements: Add endpoints for worker management and payment summaries.

Database Rules: Store worker data and payment history in a normalized structure.

Acceptance Criteria: Workers can be managed, attended, and paid according to the defined rules.

What NOT to modify: Do not implement customer or finance reporting features in this phase.

Expected Deliverables: Worker management backend and UI screens.

Testing Requirements: Verify attendance entries and payment status updates.

Stopping Conditions: Stop once worker operations can be managed end-to-end.

## Testing Checklist

- Workers can be created and edited.
- Attendance records are saved correctly.
- Payment statuses can be updated.

## Git Commit Message

feat: add worker attendance and payment tracking

## Definition of Done

The worker module supports attendance entry and payment tracking for farm operations.

## Next Phase

Proceed to sales management.
