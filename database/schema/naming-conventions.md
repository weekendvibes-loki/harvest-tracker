# Naming Conventions

**Project**: Harvest Tracker
**Applies to**: All PostgreSQL objects in the `harvest_tracker` schema

---

## 1. General Principles

- All names are `lowercase_snake_case`
- No abbreviations unless the full term is excessively long (see exceptions below)
- Names must be self-documenting
- Maximum identifier length: 63 characters (PostgreSQL limit)

---

## 2. Schema

The database uses a single custom schema:

```sql
CREATE SCHEMA IF NOT EXISTS harvest_tracker;
SET search_path TO harvest_tracker, public;
```

All tables, sequences, indexes, and constraints are created inside `harvest_tracker`.

---

## 3. Table Naming

| Rule | Convention | Example |
|------|-----------|---------|
| Use plural noun | `snake_case` plural | `farms`, `harvest_records` |
| Junction tables | Both entity names, alphabetical | `farm_fruit_types`, `harvest_workers` |
| Reference tables | Descriptive plural | `fruit_types`, `units_of_measure` |
| Avoid prefixes | Do not prefix with module | ~~`farm_farm_documents`~~ → `farm_documents` |

### Approved Table Names

| Module | Table Name |
|--------|-----------|
| Reference | `fruit_types` |
| Reference | `crop_variants` |
| Reference | `units_of_measure` |
| Reference | `worker_types` |
| Reference | `payment_methods` |
| Reference | `expense_categories` |
| Auth | `roles` |
| Auth | `permissions` |
| Auth | `role_permissions` |
| Auth | `users` |
| Farm | `farms` |
| Farm | `farm_documents` |
| Farm | `farm_fruit_types` |
| Farm | `seasons` |
| Harvest | `harvest_records` |
| Harvest | `harvest_quality_checks` |
| Harvest | `harvest_workers` |
| Worker | `workers` |
| Worker | `worker_attendance` |
| Worker | `worker_payments` |
| Sales | `customers` |
| Sales | `orders` |
| Sales | `order_items` |
| Sales | `sales` |
| Sales | `transport_records` |
| Sales | `invoices` |
| Sales | `payments` |
| Expense | `expenses` |
| Inventory | `inventory_batches` |
| Reporting | `report_snapshots` |
| Extensibility | `fruit_attributes` |

---

## 4. Column Naming

| Rule | Convention | Example |
|------|-----------|---------|
| Primary key | Always `id` | `id BIGSERIAL` |
| Foreign key | `{singular_referenced_table}_id` | `farm_id`, `worker_id`, `fruit_type_id` |
| Timestamps | `{event}_at` suffix | `created_at`, `harvested_at`, `invoiced_at` |
| Dates | `{event}_date` suffix | `harvest_date`, `attendance_date`, `due_date` |
| Boolean flags | `is_{adjective}` | `is_active`, `is_present`, `is_verified` |
| Status fields | `{entity}_status` or just `status` | `order_status`, `payment_status` |
| Monetary amounts | `{descriptor}_amount` | `total_amount`, `paid_amount`, `wage_amount` |
| Quantity fields | `{descriptor}_quantity` or `quantity` | `harvest_quantity`, `quantity` |
| Name fields | `name` (simple) or `{descriptor}_name` | `name`, `document_name` |
| Description | `description` or `notes` | `notes`, `quality_notes` |
| Text/email | `email` | `email` |
| Phone | `phone` | `phone` |
| Code fields | `code` | `code` (for reference tables) |
| Sort order | `sort_order` | `sort_order` |

### Approved Column Name Exceptions (abbreviations)

| Full term | Approved abbreviation | Reason |
|-----------|----------------------|--------|
| GPS latitude | `gps_latitude` | Full word kept |
| GPS longitude | `gps_longitude` | Full word kept |
| Unit of measure | `uom_id` | Standard industry abbreviation |

---

## 5. Audit Column Names

These exact column names are used on every table without variation:

```sql
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
created_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
updated_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
deleted_at  TIMESTAMPTZ
is_active   BOOLEAN NOT NULL DEFAULT TRUE
```

---

## 6. Constraint Naming

### Primary Keys

```
pk_{table_name}
```

Examples:
- `pk_farms`
- `pk_harvest_records`

Note: PostgreSQL auto-names PKs as `{table}_pkey`. Use explicit naming for clarity in migrations.

### Foreign Keys

```
fk_{child_table}_{parent_table}
```

Examples:
- `fk_farms_users` — farms.created_by → users.id
- `fk_harvest_records_farms` — harvest_records.farm_id → farms.id
- `fk_harvest_records_seasons` — harvest_records.season_id → seasons.id

When a table has multiple FKs to the same parent, include the column:
```
fk_{child_table}_{column}_{parent_table}
```
Example: `fk_farms_created_by_users`

### Unique Constraints

```
uq_{table}_{column(s)}
```

Examples:
- `uq_users_email`
- `uq_farms_name`
- `uq_fruit_types_code`
- `uq_seasons_farm_fruit_year` — composite

### Check Constraints

```
ck_{table}_{rule_description}
```

Examples:
- `ck_harvest_records_quantity_positive`
- `ck_orders_valid_status`
- `ck_farms_valid_ownership_type`
- `ck_invoices_paid_not_exceed_total`
- `ck_farms_leased_requires_start_date`

### Not Null Constraints

Not null constraints are inline column definitions. No separate naming needed.

---

## 7. Index Naming

```
idx_{table}_{column(s)}
```

For composite indexes, list columns in selectivity order (most selective first):

Examples:
- `idx_harvest_records_farm_id`
- `idx_harvest_records_season_id`
- `idx_harvest_records_farm_id_season_id` — composite
- `idx_orders_customer_id`
- `idx_payments_invoice_id`
- `idx_worker_attendance_worker_id_attendance_date` — composite

For partial indexes, append the condition concept:
```
idx_{table}_{column}_active
```
Example: `idx_farms_name_active` — WHERE deleted_at IS NULL

---

## 8. Sequence Naming

PostgreSQL auto-generates sequences for BIGSERIAL columns as:

```
{table}_{column}_seq
```

Examples:
- `farms_id_seq`
- `harvest_records_id_seq`

Do not create custom sequences. Use BIGSERIAL.

---

## 9. Trigger Naming

```
trg_{table}_{action}
```

Examples:
- `trg_farms_updated_at`
- `trg_harvest_records_updated_at`
- `trg_orders_updated_at`

Trigger function (reusable):
```
set_updated_at()
```

---

## 10. View Naming

```
v_{descriptive_name}
```

Examples (for future reporting views):
- `v_harvest_summary_by_farm`
- `v_season_revenue_summary`
- `v_worker_payment_status`

---

## 11. Function Naming

```
fn_{action}_{subject}()
```

Examples:
- `fn_recalculate_invoice_total()`
- `fn_close_season()`

---

## 12. Status Value Conventions

All status columns use uppercase strings stored in VARCHAR with CHECK constraints.

| Entity | Status Values |
|--------|--------------|
| `farms.status` | `ACTIVE`, `INACTIVE`, `ARCHIVED` |
| `seasons.status` | `PLANNED`, `ACTIVE`, `CLOSED` |
| `harvest_records.status` | `DRAFT`, `CONFIRMED`, `STORED`, `SOLD` |
| `orders.order_status` | `DRAFT`, `CONFIRMED`, `DISPATCHED`, `DELIVERED`, `INVOICED`, `PAID`, `CANCELLED` |
| `invoices.invoice_status` | `DRAFT`, `ISSUED`, `PARTIALLY_PAID`, `PAID`, `OVERDUE`, `CANCELLED` |
| `payments.payment_status` | `PENDING`, `COMPLETED`, `FAILED`, `REFUNDED` |
| `worker_payments.payment_status` | `PENDING`, `PAID`, `CANCELLED` |
| `workers.status` | `ACTIVE`, `INACTIVE` |
| `customers.status` | `ACTIVE`, `INACTIVE` |

---

## 13. Boolean Column Naming Rules

- Use `is_` prefix: `is_active`, `is_present`, `is_verified`, `is_primary`
- Always provide a default value
- No nullable booleans — use `NOT NULL DEFAULT FALSE` or `NOT NULL DEFAULT TRUE`

---

## 14. File Naming

| File type | Convention | Example |
|-----------|-----------|---------|
| Flyway migrations | `V{n}__{description}.sql` | `V1__initial_schema.sql` |
| Seed files | `S{n}__{description}.sql` | `S1__reference_data.sql` |
| Schema docs | `{topic}.md` | `schema-design.md` |
