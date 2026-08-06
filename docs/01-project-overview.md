# 01. Project Overview

## Project Goal

Build a practical harvest management platform for mango farms that supports farm operations, harvest tracking, worker management, sales, payments, and reporting.

## Purpose

This document gives the implementation team a shared understanding of the product, its users, and the problem it solves.

## Business Context

Seasonal harvest businesses often rely on disconnected tools such as paper logs, spreadsheets, and WhatsApp messages. This causes errors in harvest quantity, worker payments, sales tracking, and financial reporting. Harvest Tracker should centralize those activities and create a reliable operational record.

## Functional Requirements

- Manage farm profiles including ownership type, land details, and GPS coordinates.
- Track harvest records with quantity, quality, harvest date, and storage location.
- Manage worker details, attendance, daily wages, and payment status.
- Manage customers, orders, sales, transportation, invoices, and payments.
- Provide dashboards for revenue, expenses, profit, and key farm summaries.
- Generate reports for season, harvest, sales, workers, and finance.

## Non Functional Requirements

- Secure authentication for all user roles.
- Fast response times for day-to-day operations.
- Mobile-friendly design for supervisors and field workers.
- Reliable audit trail for critical transactions.
- Clear separation between business logic and UI logic.

## Business Rules

- Each harvest record must belong to one farm and one season.
- A worker can be assigned to multiple harvest batches but must not be double-counted in a single batch.
- Payments must be traceable to a worker, a period, and a status.
- Orders must be linked to a customer and must have a payment state.
- Any change to financial data should be auditable.

## Acceptance Criteria

- A farm owner can create and manage multiple farms.
- A supervisor can record harvest data and assign workers.
- An accountant can track payment statuses and invoice states.
- An admin can manage roles and system-wide settings.

## Database Tables

- users
- farms
- farm_documents
- seasons
- harvest_records
- harvest_quality_checks
- workers
- worker_attendance
- worker_payments
- customers
- orders
- sales
- transport_records
- invoices
- payments
- reports

## API Endpoints

- /auth/login
- /farms
- /harvests
- /workers
- /customers
- /orders
- /sales
- /payments
- /reports

## UI Screens

- Login
- Dashboard
- Farm list and detail
- Harvest entry form
- Worker management screen
- Sales order screen
- Reports screen

## Implementation Strategy

Use a modular full-stack implementation with a Next.js frontend, a Spring Boot backend, and PostgreSQL as the primary data store. Start with core entities and role-based access control. Keep the system extensible so fruit-specific logic can be added later.

## Manual Tasks

- Define user roles and permission matrix.
- Collect sample farm and harvest data for testing.
- Prepare business terminology for invoices, wages, and reports.

## Monkey Code Prompt

Objective: Create the foundational product structure and initial domain model for Harvest Tracker.

Current Project Context: The project is a web application for mango farms with future multi-fruit support. The initial release should support farms, harvests, workers, sales, and reporting.

Files that already exist: README.md

Files to create: frontend and backend starter structure, initial domain models, and base configuration.

Business Rules: Enforce role-based access, farm ownership, and harvest traceability.

Validation Rules: Required fields must be enforced, and financial fields must be positive.

Coding Standards: Use TypeScript on the frontend, Java 21 on the backend, and PostgreSQL-compatible schema design.

Expected Deliverables: Project scaffold, initial config, and base domain entities.

## Testing Checklist

- Role-based access works as intended.
- Farm creation and editing work.
- Basic data validation prevents invalid records.

## Git Commit Message

feat: initialize harvest tracker project structure and domain model

## Definition of Done

The product scope, business domain, and implementation direction are clear enough for Phase 1 work to begin.

## Next Phase

Proceed to authentication and access control setup.
