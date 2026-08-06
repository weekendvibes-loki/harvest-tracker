# 03. Business Workflow

## Project Goal

Describe the real-world business processes that the software must support from start to finish.

## Purpose

This document turns business requirements into clear operational workflows for backend, frontend, and data design.

## Business Context

The system should support the actual flow of farm operations: farm setup, harvest collection, worker payments, customer sales, and financial review.

## Functional Requirements

- Farm onboarding workflow.
- Harvest recording workflow.
- Worker attendance and wage workflow.
- Sales and payment workflow.
- Reporting and review workflow.

## Non Functional Requirements

- The workflow must be easy for non-technical users.
- Data capture should be fast and low-friction.
- Workflow states must be consistent and visible.

## Business Rules

- A harvest cannot be confirmed without at least one assigned worker.
- A wage settlement cannot be completed without verified attendance.
- An order must move through clear statuses: draft, confirmed, shipped, invoiced, paid.
- A farm assignment must always resolve to a valid active farm.

## Acceptance Criteria

- A supervisor can create a harvest record and assign workers in one session.
- An accountant can review payments and update statuses.
- A manager can view the real-time business summary from dashboard screens.

## Database Tables

- workflow_statuses
- harvest_records
- worker_attendance
- worker_payments
- orders
- invoices
- payments

## API Endpoints

- POST /api/harvests/confirm
- POST /api/workers/attendance
- POST /api/orders/confirm
- POST /api/payments/settle

## UI Screens

- Harvest creation wizard
- Attendance capture screen
- Payment review screen
- Order status board

## Implementation Strategy

Model workflows as stateful transactions with explicit statuses. Keep business logic in the backend service layer so the UI remains thin and consistent. Add events or audit entries for key status changes.

## Manual Tasks

- Prepare workflow diagrams for each key process.
- Define who can approve each state change.
- Review the terminology used by field staff.

## Monkey Code Prompt

Objective: Implement the core business workflow modules and state transitions for harvest, worker payments, and order handling.

Current Project Context: The business process includes farm operations, workforce processing, and sales lifecycle management.

Files that already exist: docs/02-product-requirements.md

Files to create: workflow service layer, status enums, and workflow screens.

Business Rules: Protect transitions with validation and authorization.

Validation Rules: Status changes must follow the allowed sequence.

Coding Standards: Use explicit domain services, enums, and auditable state changes.

Expected Deliverables: Workflow services and UI forms for core tasks.

## Testing Checklist

- Workflow state transitions are valid.
- Access rules prevent unauthorized status changes.
- Audit trail is created for important transitions.

## Git Commit Message

feat: define business workflows for harvest and sales operations

## Definition of Done

The main business operations have been translated into clear, testable workflow rules.

## Next Phase

Use these workflows to define the technical architecture and module boundaries.
