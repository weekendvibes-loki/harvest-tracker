# Indexing Strategy

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation

---

## 1. Indexing Principles

1. **Index foreign keys** — every FK column gets an index. PostgreSQL does not auto-create FK indexes.
2. **Index query predicates** — columns in frequent WHERE, JOIN ON, and ORDER BY clauses.
3. **Use partial indexes** for soft-delete patterns to exclude deleted rows.
4. **Use composite indexes** when two columns are always queried together.
5. **Avoid over-indexing** — every index slows INSERT/UPDATE/DELETE. Only index what queries need.
6. **Unique indexes double as constraints** — prefer `CREATE UNIQUE INDEX` over separate UNIQUE CONSTRAINT where partial indexes are needed.

---

## 2. Primary Key Indexes (Automatic)

PostgreSQL automatically creates a B-Tree index on every primary key. No manual creation needed.

| Table | Auto Index |
|-------|-----------|
| All 31 tables | `{table}_pkey` on `id` |

---

## 3. Foreign Key Indexes

Every foreign key column must have an index to prevent full table scans on JOIN and ON DELETE/UPDATE operations.

```sql
-- Reference Tables
CREATE INDEX idx_crop_variants_fruit_type_id ON crop_variants(fruit_type_id);

-- Auth
CREATE INDEX idx_users_role_id ON users(role_id);

-- Farm Module
CREATE INDEX idx_farm_documents_farm_id ON farm_documents(farm_id);
CREATE INDEX idx_farm_fruit_types_farm_id ON farm_fruit_types(farm_id);
CREATE INDEX idx_farm_fruit_types_fruit_type_id ON farm_fruit_types(fruit_type_id);
CREATE INDEX idx_seasons_farm_id ON seasons(farm_id);
CREATE INDEX idx_seasons_fruit_type_id ON seasons(fruit_type_id);

-- Harvest Module
CREATE INDEX idx_harvest_records_farm_id ON harvest_records(farm_id);
CREATE INDEX idx_harvest_records_season_id ON harvest_records(season_id);
CREATE INDEX idx_harvest_records_fruit_type_id ON harvest_records(fruit_type_id);
CREATE INDEX idx_harvest_records_crop_variant_id ON harvest_records(crop_variant_id);
CREATE INDEX idx_harvest_quality_checks_harvest_record_id ON harvest_quality_checks(harvest_record_id);
CREATE INDEX idx_harvest_workers_harvest_record_id ON harvest_workers(harvest_record_id);
CREATE INDEX idx_harvest_workers_worker_id ON harvest_workers(worker_id);

-- Worker Module
CREATE INDEX idx_workers_worker_type_id ON workers(worker_type_id);
CREATE INDEX idx_worker_attendance_worker_id ON worker_attendance(worker_id);
CREATE INDEX idx_worker_attendance_harvest_record_id ON worker_attendance(harvest_record_id);
CREATE INDEX idx_worker_payments_worker_id ON worker_payments(worker_id);
CREATE INDEX idx_worker_payments_payment_method_id ON worker_payments(payment_method_id);

-- Sales Module
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_harvest_record_id ON order_items(harvest_record_id);
CREATE INDEX idx_sales_order_id ON sales(order_id);
CREATE INDEX idx_sales_farm_id ON sales(farm_id);
CREATE INDEX idx_transport_records_order_id ON transport_records(order_id);
CREATE INDEX idx_invoices_order_id ON invoices(order_id);
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_payments_invoice_id ON payments(invoice_id);
CREATE INDEX idx_payments_payment_method_id ON payments(payment_method_id);

-- Expense Module
CREATE INDEX idx_expenses_farm_id ON expenses(farm_id);
CREATE INDEX idx_expenses_harvest_record_id ON expenses(harvest_record_id);
CREATE INDEX idx_expenses_expense_category_id ON expenses(expense_category_id);

-- Inventory Module
CREATE INDEX idx_inventory_batches_harvest_record_id ON inventory_batches(harvest_record_id);
CREATE INDEX idx_inventory_batches_farm_id ON inventory_batches(farm_id);

-- Fruit Attributes
CREATE INDEX idx_fruit_attributes_fruit_type_id ON fruit_attributes(fruit_type_id);
```

---

## 4. Composite Indexes

These index pairs that are always queried together for optimal performance:

| Index Name | Table | Columns | Query Pattern |
|-----------|-------|---------|---------------|
| `idx_harvest_records_farm_season` | `harvest_records` | `(farm_id, season_id)` | All harvests for a farm in a season |
| `idx_harvest_records_farm_date` | `harvest_records` | `(farm_id, harvest_date)` | Harvest list for a farm filtered by date |
| `idx_worker_attendance_worker_date` | `worker_attendance` | `(worker_id, attendance_date)` | Attendance lookup per worker per day |
| `idx_worker_payments_worker_period` | `worker_payments` | `(worker_id, period_start, period_end)` | Payroll period queries |
| `idx_orders_customer_status` | `orders` | `(customer_id, order_status)` | Active orders per customer |
| `idx_invoices_customer_status` | `invoices` | `(customer_id, invoice_status)` | Outstanding invoices per customer |
| `idx_payments_invoice_status` | `payments` | `(invoice_id, payment_status)` | Payment status per invoice |
| `idx_expenses_farm_date` | `expenses` | `(farm_id, expense_date)` | Farm expenses by date range |
| `idx_seasons_farm_fruit_year` | `seasons` | `(farm_id, fruit_type_id, year)` | Season lookup for dashboard |

```sql
CREATE INDEX idx_harvest_records_farm_season
    ON harvest_records(farm_id, season_id);

CREATE INDEX idx_harvest_records_farm_date
    ON harvest_records(farm_id, harvest_date);

CREATE INDEX idx_worker_attendance_worker_date
    ON worker_attendance(worker_id, attendance_date);

CREATE INDEX idx_worker_payments_worker_period
    ON worker_payments(worker_id, period_start, period_end);

CREATE INDEX idx_orders_customer_status
    ON orders(customer_id, order_status);

CREATE INDEX idx_invoices_customer_status
    ON invoices(customer_id, invoice_status);

CREATE INDEX idx_payments_invoice_status
    ON payments(invoice_id, payment_status);

CREATE INDEX idx_expenses_farm_date
    ON expenses(farm_id, expense_date);

CREATE INDEX idx_seasons_farm_fruit_year
    ON seasons(farm_id, fruit_type_id, year);
```

---

## 5. Partial Indexes (Soft Delete)

These indexes include only active (non-deleted) records:

```sql
-- Active farms lookup
CREATE INDEX idx_farms_active
    ON farms(name)
    WHERE deleted_at IS NULL;

-- Active workers lookup
CREATE INDEX idx_workers_active_farm
    ON workers(farm_id)
    WHERE deleted_at IS NULL;

-- Active orders
CREATE INDEX idx_orders_active_status
    ON orders(order_status)
    WHERE deleted_at IS NULL;

-- Open invoices (for dashboard: pending amount calculations)
CREATE INDEX idx_invoices_open
    ON invoices(invoice_status, due_date)
    WHERE deleted_at IS NULL AND invoice_status NOT IN ('PAID', 'CANCELLED');

-- Pending worker payments
CREATE INDEX idx_worker_payments_pending
    ON worker_payments(worker_id)
    WHERE payment_status = 'PENDING' AND deleted_at IS NULL;
```

---

## 6. Unique Indexes

Unique constraints implemented as partial indexes to support soft delete:

```sql
-- Users
CREATE UNIQUE INDEX uq_users_email
    ON users(email)
    WHERE deleted_at IS NULL;

-- Farms
CREATE UNIQUE INDEX uq_farms_name_active
    ON farms(name)
    WHERE deleted_at IS NULL;

-- Workers
CREATE UNIQUE INDEX uq_workers_phone_active
    ON workers(phone)
    WHERE deleted_at IS NULL AND phone IS NOT NULL;

-- Customers
CREATE UNIQUE INDEX uq_customers_phone_active
    ON customers(phone)
    WHERE deleted_at IS NULL AND phone IS NOT NULL;

-- Fruit types (system codes must always be unique)
CREATE UNIQUE INDEX uq_fruit_types_code
    ON fruit_types(code);

-- Units of measure
CREATE UNIQUE INDEX uq_units_of_measure_code
    ON units_of_measure(code);

-- One invoice per order
CREATE UNIQUE INDEX uq_invoices_order_id
    ON invoices(order_id)
    WHERE deleted_at IS NULL;

-- Invoice number globally unique
CREATE UNIQUE INDEX uq_invoices_invoice_number
    ON invoices(invoice_number)
    WHERE deleted_at IS NULL;

-- One attendance record per worker per date
CREATE UNIQUE INDEX uq_worker_attendance_worker_date
    ON worker_attendance(worker_id, attendance_date)
    WHERE deleted_at IS NULL;

-- Worker not duplicated in a harvest batch
CREATE UNIQUE INDEX uq_harvest_workers_harvest_worker
    ON harvest_workers(harvest_record_id, worker_id)
    WHERE deleted_at IS NULL;

-- Season uniqueness per farm/fruit/year/name
CREATE UNIQUE INDEX uq_seasons_farm_fruit_year_name
    ON seasons(farm_id, fruit_type_id, year, name)
    WHERE deleted_at IS NULL;

-- Roles
CREATE UNIQUE INDEX uq_roles_name
    ON roles(name)
    WHERE deleted_at IS NULL;

-- Permissions
CREATE UNIQUE INDEX uq_permissions_code
    ON permissions(code)
    WHERE deleted_at IS NULL;

-- Role-permission pairs
CREATE UNIQUE INDEX uq_role_permissions_role_permission
    ON role_permissions(role_id, permission_id)
    WHERE deleted_at IS NULL;

-- Farm-fruit type pairs
CREATE UNIQUE INDEX uq_farm_fruit_types_pair
    ON farm_fruit_types(farm_id, fruit_type_id)
    WHERE deleted_at IS NULL;
```

---

## 7. Search / Text Indexes

For text search on common lookup fields:

```sql
-- Customer name search
CREATE INDEX idx_customers_name_trgm
    ON customers USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;

-- Worker name search
CREATE INDEX idx_workers_name_trgm
    ON workers USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;

-- Farm name search
CREATE INDEX idx_farms_name_trgm
    ON farms USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;
```

> Requires `pg_trgm` extension:
> ```sql
> CREATE EXTENSION IF NOT EXISTS pg_trgm;
> ```

---

## 8. Date-Based Indexes (Report Queries)

Reports and dashboard queries are heavily date-filtered:

```sql
-- Harvest records by date for seasonal reports
CREATE INDEX idx_harvest_records_harvest_date
    ON harvest_records(harvest_date)
    WHERE deleted_at IS NULL;

-- Payments by date for financial reports
CREATE INDEX idx_payments_payment_date
    ON payments(payment_date)
    WHERE deleted_at IS NULL;

-- Expenses by date
CREATE INDEX idx_expenses_expense_date
    ON expenses(expense_date)
    WHERE deleted_at IS NULL;

-- Orders by order date
CREATE INDEX idx_orders_order_date
    ON orders(order_date)
    WHERE deleted_at IS NULL;

-- Worker attendance by date
CREATE INDEX idx_worker_attendance_date
    ON worker_attendance(attendance_date)
    WHERE deleted_at IS NULL;
```

---

## 9. Index Summary Table

| Index Name | Table | Type | Columns | Purpose |
|-----------|-------|------|---------|---------|
| `uq_users_email` | users | Unique Partial | email | Login lookup |
| `idx_users_role_id` | users | B-Tree | role_id | Role-based access |
| `idx_harvest_records_farm_id` | harvest_records | B-Tree | farm_id | FK lookup |
| `idx_harvest_records_season_id` | harvest_records | B-Tree | season_id | FK lookup |
| `idx_harvest_records_farm_season` | harvest_records | Composite | farm_id, season_id | Season reports |
| `idx_harvest_records_harvest_date` | harvest_records | Partial | harvest_date | Date filters |
| `idx_worker_attendance_worker_date` | worker_attendance | Composite | worker_id, date | Payroll queries |
| `uq_worker_attendance_worker_date` | worker_attendance | Unique Composite | worker_id, date | Prevent duplicates |
| `idx_orders_customer_status` | orders | Composite | customer_id, status | Customer order views |
| `uq_invoices_order_id` | invoices | Unique Partial | order_id | 1:1 invoice per order |
| `idx_invoices_open` | invoices | Partial | status, due_date | Dashboard: open invoices |
| `idx_payments_invoice_id` | payments | B-Tree | invoice_id | Payment history |
| `idx_expenses_farm_date` | expenses | Composite | farm_id, date | Expense reports |
| `idx_farms_name_trgm` | farms | GIN trgm | name | Farm search |
| `idx_customers_name_trgm` | customers | GIN trgm | name | Customer search |
| `idx_workers_name_trgm` | workers | GIN trgm | name | Worker search |
