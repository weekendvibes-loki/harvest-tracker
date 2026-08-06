# 23. Future Fruit Support

## Project Goal

Define how Harvest Tracker can evolve from a mango-focused product to a generic fruit harvest management system.

## Purpose

This document ensures the architecture and data model remain flexible enough for future fruit support with minimal rework.

## Business Context

The product is initially built for mango farms, but the long-term vision is a more general fruit harvest platform. The system should support additional crops without requiring a full rewrite.

## Functional Requirements

- Abstract fruit-specific logic behind configurable modules.
- Support new fruit types with minimal changes.
- Keep farm, harvest, worker, and sales modules reusable.
- Introduce fruit-specific metadata and rules later.

## Non Functional Requirements

- Extensibility and maintainability.
- Clear abstraction for domain-specific behavior.
- Minimal risk when adding a new fruit type.

## Business Rules

- Fruit-specific rules should be configurable rather than hardcoded where possible.
- Core entities must remain generic and reusable.
- New fruit types should be added through extension points rather than duplication.

## Acceptance Criteria

- The system can support a second fruit type without redesigning the core platform.
- Fruit-specific attributes can be added as optional metadata.
- Existing modules remain functional when new fruit support is added.

## Database Tables

- fruit_types
- crop_variants
- fruit_specific_attributes

## API Endpoints

- GET /api/fruits
- POST /api/fruits
- GET /api/fruits/{id}/attributes

## UI Screens

- Fruit configuration screen
- Fruit-specific form extensions
- Crop selection and mapping UI

## Implementation Strategy

Design the platform around generic entities such as farms, harvests, workers, and sales, then add fruit-specific extensions through configuration and optional metadata. Keep the core domain independent from mango-specific assumptions.

## Manual Tasks

- Define the first fruit extension model.
- Identify which fields should remain generic and which should be fruit-specific.
- Prepare a migration strategy for future data expansion.

## Monkey Code Prompt

Objective: Prepare the architecture and data model for future support of multiple fruit types.

Current Project Context: The system is currently mango-focused, but it should be designed for broader crop support later.

Files that already exist: docs/22-roadmap.md and the current architecture docs.

Files to create: fruit abstraction model, configuration hooks, and extension interfaces.

Business Rules: Keep the core domain reusable and place fruit-specific behavior behind extension points.

Validation Rules: Ensure new fruit types do not break existing workflows.

Coding Standards: Use modular abstractions and configuration-driven behavior.

UI Requirements: Allow optional fruit-specific fields without breaking the core UI.

Backend Requirements: Keep the domain model generic and make fruit-specific data optional.

API Requirements: Expose configuration and metadata endpoints for fruit-specific behavior.

Database Rules: Use generic relationships and optional extension tables rather than hardcoding fruit assumptions.

Acceptance Criteria: A second fruit type can be introduced with limited code changes.

What NOT to modify: Do not force the current mango workflows to become fruit-specific in the core design.

Expected Deliverables: Extension-ready architecture and future support plan.

Testing Requirements: Verify that existing fruit workflows continue to work after adding an extension point.

Stopping Conditions: Stop once the architecture supports future fruit expansion without major rework.

## Testing Checklist

- Core modules remain unchanged when fruit extensions are introduced.
- Fruit-specific fields can be added safely.
- The platform remains maintainable as new fruit types are introduced.

## Git Commit Message

feat: add extensibility for future fruit support

## Definition of Done

The system is structured so that future fruit types can be added without major redesign.

## Next Phase

Begin implementation of the initial release based on the phased documents.
