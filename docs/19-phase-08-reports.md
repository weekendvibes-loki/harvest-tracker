# 19. Phase 08 — Reports

## Project Goal

Implement reporting for harvest, sales, worker, and financial performance.

## Purpose

This phase allows the business to review operational and financial results in a structured format.

## Business Context

Managers and accountants need reports to understand the business over time. Reports should be clear, exportable, and based on trusted data.

## Functional Requirements

- Generate season reports.
- Generate harvest reports.
- Generate worker reports.
- Generate sales and financial reports.
- Provide export-ready report data.

## Non Functional Requirements

- Reports must be accurate and based on current data.
- Export formats should be simple and practical.
- The reporting flow should be easy to operate.

## Business Rules

- Reports must only include records the user is authorized to view.
- Report filters must be explicit and easy to understand.
- Date ranges must be validated.

## Acceptance Criteria

- A user can open the reports screen and generate a report.
- Report results are based on the selected filters.
- Report data can be exported or viewed in the UI.

## Database Tables

- reports
- harvest_records
- orders
- payments
- worker_attendance
- workers

## API Endpoints

- GET /api/reports/season
- GET /api/reports/harvest
- GET /api/reports/workers
- GET /api/reports/financial

## UI Screens

- Reports landing page
- Report filter form
- Report results table
- Export actions

## Implementation Strategy

Build reporting as a read-only reporting layer based on the existing transactional data. Apply filters and aggregate data on the backend so calculations stay consistent and simple. Support CSV export first and optionally PDF later.

## Manual Tasks

- Define the report filters and columns.
- Prepare sample report data.
- Decide the initial export format.

## Monkey Code Prompt

Objective: Implement report generation for harvest, sales, worker, and financial data.

Current Project Context: The business now needs reporting views for operations and decision-making.

Files that already exist: docs/18-phase-07-dashboard.md and current modules.

Files to create: report service, report endpoints, reports UI page, filters, and export helpers.

Business Rules: Enforce access control and validate date ranges.

Validation Rules: Reject invalid filters and empty report requests.

Coding Standards: Keep report logic server-side and present results in structured UI components.

UI Requirements: Provide a practical report screen with filters and clear result tables.

Backend Requirements: Aggregate and return report data from transactional tables.

API Requirements: Expose report endpoints with filter support.

Database Rules: Use normalized source records and avoid duplicating business logic in the UI.

Acceptance Criteria: Reports can be generated and reviewed from the UI.

What NOT to modify: Do not add experimental AI features in this phase.

Expected Deliverables: Reports API and UI screens.

Testing Requirements: Verify report generation with sample data and filter combinations.

Stopping Conditions: Stop once reports can be generated and reviewed successfully.

## Testing Checklist

- Report filters work as expected.
- Export action is available.
- The report results match the expected data.

## Git Commit Message

feat: add reporting for harvest sales and finance

## Definition of Done

Reporting is available for key business areas and is built on the core operational data.

## Next Phase

Move to testing, deployment, and long-term roadmap planning.
