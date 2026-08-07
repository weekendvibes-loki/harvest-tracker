# Database

This folder contains all database-related files for Harvest Tracker.

## Folder Structure

```
database/
    migrations/     Flyway versioned migration scripts
    schema/         Reference schema diagrams and ERD exports
    seeds/          Seed data scripts for development and testing
    diagrams/       Visual database diagrams (PNG, SVG, drawio)
    backups/        Local database backup scripts and dumps (not committed)
```

## Flyway Migration Convention

Migration files must follow the Flyway naming convention:

```
V{version}__{description}.sql
```

Examples:
```
V1__initial_schema.sql
V2__add_seasons_table.sql
V3__add_harvest_quality_checks.sql
```

Rules:
- Version numbers must be sequential and never reused.
- Description uses double underscore after the version number.
- Use lowercase words separated by underscores in the description.
- Never modify an already-applied migration file.
- Always test migrations against a fresh database before merging.

## Backups

The `backups/` folder is listed in `.gitignore` and is for local use only.
Never commit database dumps or credentials to version control.
