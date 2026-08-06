# 18. Phase 07 — Dashboard

## Project Goal

Create the main dashboard experience for the business.

## Purpose

This phase gives users a quick view of current performance, harvest progress, revenue, and operational health.

## Business Context

Managers and owners need a summary view to understand performance without digging through multiple modules. The dashboard should support decisions quickly.

## Functional Requirements

- Show revenue, expenses, and profit summary.
- Show harvest summary by farm or season.
- Show top-performing farms and top customers.
- Provide quick navigation to core modules.

## Non Functional Requirements

- The dashboard must load quickly.
- The layout should remain readable on common screen sizes.
- KPI numbers should be easy to understand.

## Business Rules

- Dashboard metrics must use the latest committed data.
- Summary values should be calculated consistently from the transactional data.
- Users should only see data they are authorized to access.

## Acceptance Criteria

- The dashboard displays key business metrics.
- Summary cards and charts are visible and understandable.
- The dashboard links to core pages.

## Database Tables

- harvest_records
- orders
- payments
- sales
- farms

## API Endpoints

- GET /api/dashboard/summary
- GET /api/dashboard/financial
- GET /api/dashboard/harvests

## UI Screens

- Dashboard landing page
- KPI cards
- Summary charts and tables

## Implementation Strategy

Build the dashboard as a read-oriented summary layer that aggregates data from the existing modules. Use simple charts and clear cards. Keep the calculations centralized in the backend service layer.

## Manual Tasks

- Define the KPI set for the first release.
- Decide whether charts should be implemented with simple built-in components or external chart libraries.
- Prepare sample data for dashboard display.

## Monkey Code Prompt

Objective: Build the dashboard with KPI summaries, harvest metrics, and sales insights.

Current Project Context: The main business modules are implemented and the user needs an overview screen.

Files that already exist: docs/17-phase-06-sales-management.md and the current application structure.

Files to create: dashboard service, dashboard API endpoints, dashboard page, KPI components, and summary cards.

Business Rules: Use the latest committed data and enforce role-based viewing.

Validation Rules: Handle empty datasets and missing values gracefully.

Coding Standards: Use typed hooks/services and clear presentation components.

UI Requirements: Provide a simple, modern dashboard with concise metrics and navigation.

Backend Requirements: Aggregate data from farms, harvests, sales, and payments.

API Requirements: Expose summary endpoints for financial and harvest data.

Database Rules: Use existing transactional tables to compute dashboard values.

Acceptance Criteria: The dashboard displays up-to-date summaries for the business.

What NOT to modify: Do not introduce heavy analytics features beyond the phase scope.

Expected Deliverables: Dashboard page and backend summary endpoints.

Testing Requirements: Verify metrics render correctly with sample data.

Stopping Conditions: Stop once dashboard summaries are visible and accurate.

## Testing Checklist

- KPI values render correctly.
- The dashboard loads with sample data.
- Empty states are handled gracefully.

## Git Commit Message

feat: add business dashboard with summary metrics

## Definition of Done

The dashboard provides a useful operational overview for the core business users.

## Next Phase

Proceed to reports.
