# docs/decisions

This folder contains Architecture Decision Records (ADRs).

## Purpose

ADRs preserve the reasoning behind significant technical decisions. They help future developers understand why the system was built a certain way.

## Naming Convention

```
ADR-{number}-{short-description}.md
```

Example:
```
ADR-001-feature-first-architecture.md
ADR-002-flyway-database-migrations.md
ADR-003-jwt-authentication.md
```

## Template

Each ADR should use this structure:

```markdown
# ADR-{number}: {Title}

## Status
Proposed | Accepted | Deprecated | Superseded

## Context
Why was this decision needed?

## Decision
What was decided?

## Consequences
What does this enable? What is traded off?
```
