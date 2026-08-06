# 06. Database Design

## Project Goal

Define the relational data model required to support farms, harvests, workers, sales, and finance.

## Purpose

This document gives the implementation team a stable schema blueprint for PostgreSQL and backend services.

## Business Context

Harvest operations depend on accurate, linked records. The database must preserve relationships between farms, harvests, workers, customers, orders, invoices, and payments.

## Functional Requirements

- Store core master data for farms, workers, customers, and users.
- Track operational records such as harvests and attendance.
- Keep financial transactions linked to their source records.

## Non Functional Requirements

- Data integrity must be enforced at the database level where possible.
- The schema must support future fruit-based expansion.
- Queries should remain simple enough for standard indexing and reporting.

## Business Rules

- Every harvest record must belong to a farm and a season.
- Every payment must reference a payment source and status.
- Orders must be tied to one customer.
- Workers must be unique per business context and identified with a clear business identifier.

## Acceptance Criteria

- The schema supports farm, harvest, worker, customer, and payment management.
- Relationships between modules are explicit and normalized.
- The design can be extended to a multi-fruit model later.

## Database Tables

### Core Tables
- users
- roles
- permissions
- farms
- farm_documents
- seasons

### Operations Tables
- harvest_records
- harvest_quality_checks
- workers
- worker_attendance
- worker_payments

### Sales Tables
- customers
- orders
- sales
- transport_records
- invoices
- payments

### Reporting Tables
- reports

### Suggested Key Fields

| Table | Key Fields |
| --- | --- |
| users | id, name, email, password_hash, role_id, created_at |
| farms | id, name, owner_id, ownership_type, land_size, gps_location, status |
| harvest_records | id, farm_id, season_id, harvest_date, quantity, quality_grade, storage_location |
| workers | id, farm_id, name, phone, wage_rate, status |
| worker_attendance | id, worker_id, attendance_date, present, remarks |
| worker_payments | id, worker_id, period_start, period_end, amount, status |
| customers | id, name, phone, address, customer_type |
| orders | id, customer_id, order_date, order_status, total_amount |
| invoices | id, order_id, invoice_number, issued_at, due_date, total_amount |
| payments | id, invoice_id, payment_date, amount, payment_status |

## API Endpoints

- /api/farms
- /api/harvests
- /api/workers
- /api/orders
- /api/payments

## UI Screens

- Data entry forms
- Record detail panels
- Summary tables

## Implementation Strategy

Use PostgreSQL with clear primary and foreign keys. Add indexes for common lookup operations, and use UUIDs or bigints as primary keys. Keep tables normalized, but allow denormalized reporting views later.

## Manual Tasks

- Create the initial SQL migration scripts.
- Decide on naming conventions for columns and tables.
- Plan indexing for report queries.

## Monkey Code Prompt

Objective: Generate the initial PostgreSQL schema and migration files for the core domain model.

Current Project Context: The system needs farm, harvest, worker, sales, and payment data models.

Files that already exist: docs/05-folder-structure.md

Files to create: migration SQL files, entity classes, and repository interfaces.

Business Rules: Maintain relationships and enforce non-null and positive value constraints.

Validation Rules: Enforce required fields, unique business keys, and positive monetary values.

Coding Standards: Use lowercase snake_case for database objects and camelCase in app code.

Expected Deliverables: Initial migration scripts and entity definitions.

## Testing Checklist

- Foreign key relationships work correctly.
- Required fields enforce non-null values.
- Basic report queries return expected results.

## Git Commit Message

db: add initial schema for farms harvests and sales

## Definition of Done

The database design supports the initial release and can be extended for future fruit support.

## Next Phase

Use the schema to implement coding standards and the first development workflow.
