# 02. Product Requirements

## Project Goal

Translate the business needs of Harvest Tracker into a clear product requirement set for implementation.

## Purpose

This document defines what the system must do, what it must not do yet, and how success will be measured.

## Business Context

The product must support day-to-day operations for farm businesses while remaining adaptable for future fruit expansion.

## Functional Requirements

### Farm Management
- Create, edit, view, and archive farms.
- Record ownership type: owned or leased.
- Store land size, unit, GPS, and farm documents.

### Harvest Management
- Record harvest batches by farm, date, quantity, quality, and storage location.
- Track quality grade and harvest notes.
- Associate harvests with workers and supervisors.

### Worker Management
- Manage personal worker information.
- Record attendance and daily wage.
- Track payment status and wage settlements.

### Sales Management
- Manage customers, orders, sales, transport, invoices, and payments.
- Support partial and full payment tracking.

### Dashboard
- Show revenue, expenses, profit, and harvest summary.
- Show top farms and top customers.

### Reports
- Generate season, harvest, sales, worker, and finance reports.

## Non Functional Requirements

- Data must be protected with role-based access control.
- Core pages must load quickly on standard internet connections.
- The UI must work well on desktop and tablet screens.
- All financial records should be accurate and auditable.

## Business Rules

- Farm records must include a unique identifier and a status.
- Harvest quantity must be greater than zero.
- A worker payment entry must reference an attendance period.
- A customer order cannot be saved without a customer reference.
- Invoices should not be issued for unpaid orders unless explicitly allowed.

## Acceptance Criteria

- Farm owner can manage farms and documents.
- Supervisor can add harvest details and assign workers.
- Accountant can review invoices and payments.
- Admin can manage roles and permissions.

## Database Tables

- farms
- farm_documents
- seasons
- harvest_records
- worker_attendance
- worker_payments
- customers
- orders
- sales
- invoices
- payments

## API Endpoints

- GET /api/farms
- POST /api/farms
- GET /api/harvests
- POST /api/harvests
- GET /api/workers
- POST /api/workers
- GET /api/orders
- POST /api/orders
- GET /api/reports/financial

## UI Screens

- Farm management screen
- Harvest entry form
- Worker pay summary screen
- Customer and order screens
- Reports screen

## Implementation Strategy

Implement the product in phases starting with the foundation, then authentication, then core farm and harvest modules, followed by sales and reporting. Keep modules decoupled so fruit-specific extensions do not force a rewrite.

## Manual Tasks

- Validate field names and terminology with the business owner.
- Prepare sample data for testing.
- Document the permission levels for each role.

## Monkey Code Prompt

Objective: Build the initial product requirements into a scoped implementation plan for the first release.

Current Project Context: The product is a harvest management system for mango farms with future multi-fruit support.

Files that already exist: docs/01-project-overview.md

Files to create: implementation backlog, API contract draft, and role-permission matrix.

Business Rules: Apply validation and role-based constraints to core modules.

Validation Rules: Required fields, uniqueness, positive amounts, and date constraints.

Coding Standards: Use explicit DTOs, service-layer validation, and server-side data checks.

Expected Deliverables: Requirement traceability list and phase plan.

## Testing Checklist

- Business rules are enforced.
- Required fields cannot be left empty.
- Orders and payments follow the expected lifecycle.

## Git Commit Message

feat: add product requirements and role-based scope

## Definition of Done

The implementation scope is clear, measurable, and ready for technical design.

## Next Phase

Use these requirements to define business workflows and the system architecture.
