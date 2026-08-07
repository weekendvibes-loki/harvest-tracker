# Database Design Decisions

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation
**PostgreSQL Target Version**: 15+

---

## 1. Primary Key Strategy

### Decision: BIGSERIAL

Every table uses a surrogate primary key:

```sql
id BIGSERIAL PRIMARY KEY
```

### Evaluation

| Criterion | BIGSERIAL | UUID | Identity Column |
|-----------|-----------|------|-----------------|
| Storage per row | 8 bytes | 16 bytes | 8 bytes |
| Index size | Compact, sequential | Larger, random | Compact |
| Join performance | Excellent | Moderate | Excellent |
| Readability in logs | Human-readable | Opaque | Human-readable |
| Global uniqueness | DB-scoped | Global | DB-scoped |
| Generation | Server-side | Client or server | Server-side |

### Recommendation: BIGSERIAL

**Reasons:**
1. Harvest Tracker is a single-tenant system. Global uniqueness is not required in V1.
2. Sequential integer keys give compact B-Tree indexes and fast range scans.
3. Joins across 31 tables are frequent in reporting. Integer joins outperform UUID comparisons.
4. BIGSERIAL supports ~9.2 quintillion values — unlimited for agricultural operations.
5. Human-readable IDs simplify debugging and support.

> If external API exposure ever requires opaque IDs, a `public_id UUID DEFAULT gen_random_uuid()` column can be added to specific tables in a future migration without changing the primary key strategy.

---

## 2. Audit Strategy

### Decision: Five audit columns on every table, no hard deletes

Every table carries exactly these five audit columns:

```sql
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
created_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
updated_by  BIGINT REFERENCES users(id) ON DELETE SET NULL
deleted_at  TIMESTAMPTZ
```

Plus a business-state column:

```sql
is_active   BOOLEAN NOT NULL DEFAULT TRUE
```

### Evaluation

| Option | Decision | Reason |
|--------|----------|--------|
| `created_at` | YES | Every record needs a creation timestamp |
| `updated_at` | YES | Tracks mutation time, updated by trigger |
| `created_by` | YES | Financial audit requires user attribution |
| `updated_by` | YES | Ownership of changes |
| `deleted_at` | YES | Soft delete preserves referential integrity |
| `is_active` | YES | Business activation without deletion |
| Hard delete | NO | Financial records must never be destroyed |
| Separate audit log table | V2 | Adds complexity, not in scope for Phase 0.6 |

### Soft Delete Rules

- `deleted_at IS NULL` = active record
- `deleted_at IS NOT NULL` = soft-deleted record
- All application queries filter `WHERE deleted_at IS NULL`
- Unique partial indexes exclude deleted records:
  ```sql
  CREATE UNIQUE INDEX uq_farms_name_active ON farms(name)
    WHERE deleted_at IS NULL;
  ```

### `updated_at` Trigger

A single reusable function is created once and applied to every table:

```sql
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

Applied as:
```sql
CREATE TRIGGER trg_farms_updated_at
  BEFORE UPDATE ON farms
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();
```

---

## 3. Naming Conventions

See `schema/naming-conventions.md` for complete specification.

Summary:

| Object | Convention | Example |
|--------|-----------|---------|
| Tables | `snake_case`, plural | `harvest_records` |
| Columns | `snake_case` | `harvest_date` |
| Primary keys | Always `id` | `id BIGSERIAL` |
| Foreign keys | `{singular}_id` | `farm_id`, `worker_id` |
| Indexes | `idx_{table}_{column}` | `idx_harvest_records_farm_id` |
| Unique constraints | `uq_{table}_{columns}` | `uq_farms_name` |
| Check constraints | `ck_{table}_{rule}` | `ck_orders_total_positive` |

---

## 4. Normalization Level: 3NF with Justified Denormalization

The schema targets **Third Normal Form (3NF)**:
- Every non-key attribute is fully dependent only on the primary key
- No transitive dependencies between non-key columns

### Justified denormalization

| Column | Table | Reason |
|--------|-------|--------|
| `total_amount` | `orders` | Stored for performance; recalculated on item change |
| `total_amount` | `invoices` | Required for statement queries without rejoining items |
| `total_days_worked` | `worker_payments` | Stored computation from attendance date range |
| `paid_amount` | `invoices` | Incrementally updated to avoid SUM query on every read |

---

## 5. Future Fruit Support: Configuration-Driven Design

### Decision: Reference tables + junction, NOT separate schema per fruit

| Approach | Decision | Reason |
|----------|----------|--------|
| `fruit_type ENUM` in harvest | NO | ENUMs require ALTER TABLE to add values |
| Separate table per fruit | NO | Schema explosion, duplicated business logic |
| JSONB blob for all attributes | NO | Loses query ability, no constraint enforcement |
| Reference tables + EAV | YES | New fruit = one INSERT into `fruit_types` |

### Tables that enable fruit extensibility

1. **`fruit_types`** — master list: Mango, Banana, Orange, Apple, Grapes, Pomegranate
2. **`crop_variants`** — sub-types per fruit: Alphonso, Kesar (Mango); Cavendish (Banana)
3. **`fruit_attributes`** — EAV: fruit-specific metadata (skin_colour, brix_level, etc.)
4. **`farm_fruit_types`** — a farm can cultivate multiple fruits
5. **`harvest_records.fruit_type_id`** — every harvest is tied to a fruit type

### Adding Banana support in future

```sql
-- Only requires:
INSERT INTO fruit_types (name, code) VALUES ('Banana', 'BANANA');
INSERT INTO crop_variants (fruit_type_id, name) VALUES ((SELECT id FROM fruit_types WHERE code='BANANA'), 'Cavendish');
-- No ALTER TABLE on any core table
```

---

## 6. Farm Ownership Model

### Decision: CHECK constraint with VARCHAR

```sql
ownership_type VARCHAR(20) NOT NULL DEFAULT 'OWNED'
  CHECK (ownership_type IN ('OWNED', 'LEASED'))
```

- Two ownership types are stable. A CHECK constraint is simpler than a reference table.
- Lease-specific columns (`lease_start_date`, `lease_end_date`, `lessor_name`) are nullable.
- A check constraint enforces that leased farms have a `lease_start_date`.

---

## 7. Order Lifecycle State Machine

```
DRAFT → CONFIRMED → DISPATCHED → DELIVERED → INVOICED → PAID → CANCELLED
```

- States stored as VARCHAR with a CHECK constraint listing all valid values.
- Transition logic (e.g., cannot go from PAID to DRAFT) is enforced at the application service layer.
- The database enforces valid state values only.

---

## 8. Financial Precision

All monetary columns use `NUMERIC(15, 2)`.

| Type | Problem |
|------|---------|
| `FLOAT` | Rounding errors in financial calculations |
| `DECIMAL` without precision | Undefined precision |
| `NUMERIC(15, 2)` | Exact arithmetic, supports up to 999,999,999,999,999.99 |

Two decimal places matches INR (Indian Rupee) conventions.

---

## 9. GPS Coordinates

```sql
gps_latitude   NUMERIC(10, 7)
gps_longitude  NUMERIC(10, 7)
```

Plain numeric columns are used for simplicity in Phase 0.6. PostGIS `GEOMETRY` can be added in a future migration when spatial queries (nearest farm, geofencing) are required. Seven decimal places gives approximately 1 cm precision.

---

## 10. Season Model

A season belongs to a specific farm and fruit type:

```sql
seasons(id, farm_id, fruit_type_id, name, year, start_date, end_date, status, ...)
```

This allows independent mango and banana seasons on the same farm.

---

## 11. Payment vs. Invoice Relationship

- **One Invoice per Order** (1:1)
- **Multiple Payments per Invoice** (1:M) — partial payment support
- `invoices.paid_amount` is updated by the application on each payment
- Check constraint: `paid_amount <= total_amount`

---

## 12. Worker vs. User Separation

- **Workers** — field labourers tracked for attendance and payroll; no system login required
- **Users** — system operators with login credentials (admin, manager, supervisor, accountant)

These are separate entities. A supervisor user is not the same record as a worker. Future linkage can be achieved via an optional `user_id` FK on the `workers` table if supervisors also perform field work.

---

## 13. Expense Tracking

Expenses are recorded at farm level with an optional harvest link:

- Farm-level general expenses: land rent, irrigation, tools
- Harvest-specific expenses: packaging, cold storage, transport
- Season aggregation: via join through harvest_records.season_id

---

## 14. Inventory Model

`inventory_batches` tracks harvested fruit lifecycle:

1. Created from a `harvest_record`
2. Reduced when linked to an `order_item`
3. Quantity can only decrease (check prevents negative)
4. Supports future lot-tracking and traceability

---

## 15. Reporting Architecture

- **Live dashboard**: Query transactional tables directly using indexed columns
- **Historical reports**: `report_snapshots` stores JSONB results for closed-period reports
- Snapshots are immutable once a season is closed, preventing retroactive data changes
