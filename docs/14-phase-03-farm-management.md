# 14. Phase 03 — Farm Management

## Project Goal

Implement the core farm management module for Harvest Tracker.

## Purpose

This phase allows the business to create, update, and manage farms and related land information in a structured way.

## Business Context

Farms are the foundation of all downstream operations such as harvest tracking, worker assignment, sales, and reporting. The farm module must be reliable and easy to maintain.

## Functional Requirements

- Create, view, edit, and list farms.
- Record ownership type: owned or leased.
- Capture land area and land unit.
- Record GPS coordinates and farm documents.
- Support active and inactive farm states.

## Non Functional Requirements

- Data should be easy to search and filter.
- Forms should be clear and validation-friendly.
- The design must support future multi-fruit expansion.

## Business Rules

- A farm must have a unique name within the business scope.
- Land size must be positive.
- GPS coordinates must be stored in a normalized form.
- Farm documents must relate to an existing farm.

## Acceptance Criteria

- A user can create a farm record with complete details.
- The farm list shows essential information clearly.
- A farm can be edited and marked inactive.

## Database Tables

- farms
- farm_documents

## API Endpoints

- GET /api/farms
- POST /api/farms
- GET /api/farms/{id}
- PUT /api/farms/{id}
- POST /api/farms/{id}/documents

## UI Screens

- Farm list page
- Farm detail page
- Farm create/edit form
- Farm documents section

## Implementation Strategy

Implement farm CRUD first, then add location and document handling. Keep the module independent of harvests and sales so it can be reused later for other fruit types.

## Manual Tasks

- Prepare sample farm data for testing.
- Decide how document uploads will be handled in the initial release.
- Document ownership type values and land unit conventions.

## Monkey Code Prompt

Objective: Build the farm management module including CRUD operations, farm metadata, and document handling.

Current Project Context: Authentication is in place and the system must now support farm records as the foundation for operations.

Files that already exist: docs/13-phase-02-authentication.md and the initial scaffold.

Files to create: farm entity, repository, service, controller, DTOs, farm list page, farm detail page, and create/edit forms.

Business Rules: Enforce unique farms, positive land area, and role-based access to farm records.

Validation Rules: Reject missing names, invalid land size, and invalid GPS values.

Coding Standards: Follow the backend service pattern and use typed frontend forms.

UI Requirements: The farm screens should be practical, with clear labels and summary cards.

Backend Requirements: Persist farm metadata and linked documents in the database.

API Requirements: Return list, detail, create, and update endpoints for farms.

Database Rules: Store farm records and documents with explicit relationships.

Acceptance Criteria: Farms can be created, edited, viewed, and listed successfully.

What NOT to modify: Do not implement sales or reporting features in this phase.

Expected Deliverables: Farm management backend and UI screens.

Testing Requirements: Verify create, read, update, and list flows.

Stopping Conditions: Stop once farm records can be managed end-to-end.

## Testing Checklist

- Farm creation works.
- Farm editing works.
- Validation prevents invalid farm data.

## Git Commit Message

feat: add farm management module

## Definition of Done

The farm module supports the core business need of managing farms and associated land information.

## Next Phase

Proceed to harvest management.
