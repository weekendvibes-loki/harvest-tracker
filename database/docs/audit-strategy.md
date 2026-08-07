# Audit Strategy

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation

---

## 1. Strategy Overview

Every table in the schema follows a uniform audit pattern. The goals are:

1. **Accountability** — know who created or changed every record
2. **Traceability** — know when every change occurred
3. **Data preservation** — financial and operational records are never destroyed
4. **Simplicity** — one consistent pattern, no special cases

---

## 2. Audit Columns

The following five columns are defined on every table:

```sql
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
created_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
updated_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
deleted_at  TIMESTAMPTZ
```

Plus one business-state column on every table:

```sql
is_active   BOOLEAN NOT NULL DEFAULT TRUE
```

### Column Definitions

| Column | Type | Nullable | Default | Purpose |
|--------|------|----------|---------|---------|
| `created_at` | TIMESTAMPTZ | No | NOW() | Record creation timestamp |
| `updated_at` | TIMESTAMPTZ | No | NOW() | Last modification timestamp |
| `created_by` | BIGINT → users | Yes | NULL | User who created the record |
| `updated_by` | BIGINT → users | Yes | NULL | User who last updated the record |
| `deleted_at` | TIMESTAMPTZ | Yes | NULL | Soft delete timestamp; NULL = active |
| `is_active` | BOOLEAN | No | TRUE | Business activation state |

### Why `created_by` / `updated_by` are nullable

The `ON DELETE SET NULL` clause means that if a user is deleted (soft-deleted or in a data cleanup scenario), the audit reference becomes NULL rather than causing a FK violation. The timestamp and data remain intact. Accountability is preserved through the timestamp even without the user reference.

---

## 3. Soft Delete Design

### How it works

- **Delete** = set `deleted_at = NOW()`; never issue `DELETE FROM`
- **Active records** = `WHERE deleted_at IS NULL`
- **Deleted records** = `WHERE deleted_at IS NOT NULL`
- `is_active` is set to `FALSE` at the same time as `deleted_at`

### Soft delete example

```sql
-- Soft delete a farm
UPDATE farms
SET
    deleted_at = NOW(),
    is_active = FALSE,
    updated_at = NOW(),
    updated_by = :userId
WHERE id = :farmId
  AND deleted_at IS NULL;
```

### Recovery (undelete)

Soft-deleted records can be restored by setting `deleted_at = NULL` and `is_active = TRUE`. This is only permitted by admins.

### Tables where hard delete is prohibited

The following tables must never use hard delete under any circumstances:

- `invoices`
- `payments`
- `orders`
- `order_items`
- `worker_payments`
- `expenses`
- `harvest_records`
- `sales`

---

## 4. Unique Constraints and Soft Delete

Standard unique constraints do not account for soft-deleted records. To allow the same name to be reused after deletion, all business-unique constraints use **partial unique indexes** excluding soft-deleted records:

```sql
-- Example: farm name unique among active records only
CREATE UNIQUE INDEX uq_farms_name_active
    ON farms(name)
    WHERE deleted_at IS NULL;
```

Applied to:
- `farms.name`
- `workers.phone`
- `customers.phone`
- `users.email`
- `fruit_types.code`
- `units_of_measure.code`

---

## 5. `updated_at` Trigger

The `updated_at` column is automatically maintained by a PostgreSQL trigger. The trigger function is defined once and reused:

```sql
-- Reusable trigger function
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

Applied to every table:

```sql
CREATE TRIGGER trg_farms_updated_at
    BEFORE UPDATE ON farms
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_harvest_records_updated_at
    BEFORE UPDATE ON harvest_records
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ... (one trigger per table)
```

This ensures `updated_at` is always accurate regardless of whether the application remembers to set it.

---

## 6. Tables and Their Audit Requirements

| Table | created_at | updated_at | created_by | updated_by | deleted_at | is_active |
|-------|------------|------------|------------|------------|------------|-----------|
| `fruit_types` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `crop_variants` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `units_of_measure` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `worker_types` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `payment_methods` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `expense_categories` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `roles` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `permissions` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `role_permissions` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `users` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `farms` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `farm_documents` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `farm_fruit_types` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `seasons` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `harvest_records` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `harvest_quality_checks` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `harvest_workers` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `workers` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `worker_attendance` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `worker_payments` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `customers` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `orders` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `order_items` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `sales` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `transport_records` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `invoices` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `payments` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `expenses` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `inventory_batches` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `report_snapshots` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| `fruit_attributes` | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |

---

## 7. Considerations for V2

The following audit enhancements are deferred to a later phase:

| Enhancement | When | Description |
|-------------|------|-------------|
| Change log table | V2 | Separate `audit_log` table capturing old/new row values |
| JSONB snapshots | V2 | Store full row JSON on change for financial tables |
| IP address logging | V2 | Log client IP alongside user for sensitive changes |
| Immutable payment records | V2 | Trigger that prevents UPDATE on `payments` after COMPLETED |

---

## 8. Time Zone Policy

All timestamp columns use `TIMESTAMPTZ` (timestamp with time zone).

- The database `timezone` parameter is set to `UTC`
- Application servers must also operate in UTC
- Conversion to IST (UTC+5:30) or any local time zone is the responsibility of the frontend
- Reports include both UTC and local time where needed

```sql
-- Set database timezone in PostgreSQL config or per session:
SET timezone = 'UTC';
```
