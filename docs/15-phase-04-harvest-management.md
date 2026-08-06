# 15. Phase 04 — Harvest Management

## Project Goal

Implement harvest recording and quality tracking for farm operations.

## Purpose

This phase captures the actual harvest activity so the business can measure quantity, quality, and storage outcomes.

## Business Context

Harvest data is central to operations and reporting. It must be recorded accurately and linked to the correct farm, date, and workers.

## Functional Requirements

- Create harvest records.
- Record quantity, quality grade, harvest date, and storage location.
- Link harvest records to farms and seasons.
- Track harvest notes and quality checks.
- Associate harvests with workers and supervisors.

## Non Functional Requirements

- Data entry should be straightforward.
- The system should be resistant to duplicate or inconsistent harvest entries.
- The records should be suitable for reporting and trend analysis.

## Business Rules

- Harvest quantity must be greater than zero.
- Harvest date cannot be in the future unless explicitly allowed.
- A harvest record must belong to an existing farm.
- Harvest quality and notes should be stored with the harvest record.

## Acceptance Criteria

- A supervisor can create a harvest entry for a farm.
- Harvest details appear in a list and detail view.
- Harvest data can be updated and reviewed later.

## Database Tables

- harvest_records
- harvest_quality_checks
- seasons

## API Endpoints

- GET /api/harvests
- POST /api/harvests
- GET /api/harvests/{id}
- PUT /api/harvests/{id}

## UI Screens

- Harvest list page
- Harvest detail page
- Harvest create/edit form
- Harvest summary cards

## Implementation Strategy

Implement harvest CRUD first, then add quality checks and seasonal grouping. Keep harvest records independent from the sales module so later fruit support is easier.

## Manual Tasks

- Define quality grade values.
- Prepare sample harvest records for testing.
- Decide how season values will be created and maintained.

## Monkey Code Prompt

Objective: Build the harvest management module including recording, quality tracking, and harvest summaries.

Current Project Context: Farms are now managed, and the system must capture harvest activity as the core operation of the business.

Files that already exist: docs/14-phase-03-farm-management.md and the current backend/frontend modules.

Files to create: harvest entity, repository, service, controller, DTOs, harvest forms, detail view, and summary components.

Business Rules: Enforce valid harvest quantity, date handling, and farm association.

Validation Rules: Reject missing farm references, invalid quantities, and invalid date values.

Coding Standards: Use typed forms, service-layer validation, and clear list/detail views.

UI Requirements: Provide simple harvest entry forms and visible summaries.

Backend Requirements: Persist harvest data and quality checks with proper relation to farms and seasons.

API Requirements: Return create, read, update, and list endpoints for harvest records.

Database Rules: Store harvest records linked to farms and seasons and allow later expansion to more fruit types.

Acceptance Criteria: Harvests can be entered and reviewed with the required metadata.

What NOT to modify: Do not implement worker payments or sales workflows in this phase.

Expected Deliverables: Harvest management backend and UI screens.

Testing Requirements: Verify successful creation, editing, and validation of harvest data.

Stopping Conditions: Stop once harvest data can be tracked end-to-end.

## Testing Checklist

- Harvest entries can be created successfully.
- Invalid quantities are rejected.
- Harvest detail and list pages render correctly.

## Git Commit Message

feat: add harvest management and quality tracking

## Definition of Done

The harvest module supports reliable recording and tracking of business harvest operations.

## Next Phase

Proceed to worker management.
