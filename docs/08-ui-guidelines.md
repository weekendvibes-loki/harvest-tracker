# 08. UI Guidelines

## Project Goal

Define the user interface direction for Harvest Tracker so the product feels practical, clear, and reliable.

## Purpose

This document helps implement screens and components that are simple for field and office users to operate.

## Business Context

The target users include farm owners, supervisors, workers, accountants, and admins. The UI should be clear enough for daily operational tasks, not overly complex.

## Functional Requirements

- Provide role-based dashboards and navigation.
- Use simple forms for data entry.
- Show status clearly for harvests, orders, and payments.
- Support responsive behaviour for tablets and desktops.

## Non Functional Requirements

- Fast and responsive screens.
- Clear visual hierarchy.
- Consistent use of components and spacing.

## Business Rules

- Only valid actions should be available based on the user role.
- Important data such as payment status and harvest quantity must be visible without extra clicks.
- Error messages should be plain language.

## Acceptance Criteria

- Users can complete main tasks without confusion.
- Navigation is consistent across modules.
- The design supports future feature addition without major rework.

## Database Tables

- Not applicable

## API Endpoints

- Not applicable

## UI Screens

- Login screen
- Dashboard screen
- Farms screen
- Harvest entry screen
- Worker management screen
- Sales order screen
- Reports screen

## Implementation Strategy

Use Tailwind CSS and shadcn/ui components to build a professional but practical interface. Favor simple layouts, strong labels, and compact tables. Avoid heavy animation or decorative visual noise.

### Design Principles
- Keep actions obvious.
- Show status using badges and color with care.
- Use tables for structured records.
- Use cards for summaries and KPIs.
- Keep forms short and grouped by task.

## Manual Tasks

- Prepare screen wireframes for the most important modules.
- Agree on the primary color palette and typography.
- Decide which screens require mobile-friendly behavior.

## Monkey Code Prompt

**MANDATORY PREFIX**: Read docs/frontend/DESIGN_SYSTEM.md and follow it strictly.

Objective: Create a consistent UI shell and the first set of screens for auth, dashboard, farms, and harvests.

Current Project Context: The app will be used by field and office users and must remain practical and easy to use.

Files that already exist: docs/07-coding-standards.md

Files to create: layout components, dashboard page, farm list page, harvest form page, and shared form components.

Business Rules: Show role-specific options and protect sensitive actions.

Validation Rules: Display clear inline error messages for required values.

Coding Standards: Use reusable components, TypeScript, Tailwind, and shadcn/ui.

Expected Deliverables: Initial UI layout and core screens.

## Testing Checklist

- Navigation and layout work on common screen sizes.
- Core screens render without broken states.
- Forms provide clear feedback.

## Git Commit Message

ui: add core dashboard and module screen layout

## Definition of Done

The UI foundation is clear and ready for functional integration.

## Next Phase

Use these UI patterns during authentication and module implementation.
