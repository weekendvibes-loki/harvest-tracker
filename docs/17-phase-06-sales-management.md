# 17. Phase 06 — Sales Management

## Project Goal

Implement customer, order, sales, transportation, invoice, and payment handling.

## Purpose

This phase connects farm operations to customer demand and revenue collection.

## Business Context

The business needs to manage customers and their orders from confirmation through payment. Sales data should be reliable and connected to invoices and payments.

## Functional Requirements

- Create and manage customers.
- Create sales orders.
- Record sales, transportation, invoices, and payments.
- Track order and payment status.

## Non Functional Requirements

- Sales workflows should be simple and reliable.
- Financial tracking must be accurate.
- The system should support future invoicing rules and payment methods.

## Business Rules

- Orders must belong to an existing customer.
- Payment amounts cannot exceed the outstanding invoice amount.
- Orders must move through clear lifecycle states.
- Invoices must reference a valid order.

## Acceptance Criteria

- A user can create a customer and order.
- An order can be updated to a paid or partially paid state.
- An invoice can be generated and linked to the order.

## Database Tables

- customers
- orders
- sales
- transport_records
- invoices
- payments

## API Endpoints

- GET /api/customers
- POST /api/customers
- GET /api/orders
- POST /api/orders
- GET /api/invoices
- POST /api/payments

## UI Screens

- Customer list page
- Order creation page
- Invoice view page
- Payment entry page

## Implementation Strategy

Implement sales as a separate but related business flow. Start with customers and orders, then add invoices and payments. Keep the transaction states explicit and auditable.

## Manual Tasks

- Define order status values.
- Determine invoice numbering rules.
- Prepare sample customer and order data.

## Monkey Code Prompt

Objective: Build the sales workflow including customers, orders, invoices, transport, and payments.

Current Project Context: Farm and harvest modules are implemented, and the business now needs revenue and customer management features.

Files that already exist: docs/16-phase-05-worker-management.md and the current modules.

Files to create: customer entity, order entity, invoice entity, payment entity, transport record entity, sales services, and the sales UI screens.

Business Rules: Enforce customer linkage, correct order lifecycle, and payment validation.

Validation Rules: Reject incomplete orders, invalid invoice references, and overpayment attempts.

Coding Standards: Use explicit status enums, service validation, and consistent UI forms.

UI Requirements: Provide clear sales entry screens and status views.

Backend Requirements: Persist sales records, invoices, and payments with linked relationships.

API Requirements: Add list and create/update endpoints for orders, invoices, payments, and customers.

Database Rules: Preserve the relationships between customers, orders, invoices, and payments.

Acceptance Criteria: Customers and orders can be managed and invoices/payments can be tracked.

What NOT to modify: Do not add reporting or AI features in this phase.

Expected Deliverables: Sales management backend and UI screens.

Testing Requirements: Verify order lifecycle, invoice generation, and payment validation.

Stopping Conditions: Stop once sales and payment handling work end-to-end.

## Testing Checklist

- Orders can be created and updated.
- Payments are linked to the correct invoice.
- Invalid payment amounts are rejected.

## Git Commit Message

feat: add sales and payment management workflow

## Definition of Done

Sales operations and payment tracking are implemented and connected to real business records.

## Next Phase

Proceed to dashboard and reporting.
