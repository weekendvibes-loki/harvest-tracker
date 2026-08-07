# Business Rules

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation

All business rules below are enforced either at the database level (constraint, trigger, FK) or at the application service layer (noted where applicable).

---

## Module 1: Reference Data

### BR-REF-01: Fruit Types are System-Managed
- `fruit_types` rows are inserted by migrations or admin seeding only.
- Application users cannot delete fruit types that have associated harvests.
- **Enforcement**: FK ON DELETE RESTRICT on `harvest_records.fruit_type_id`

### BR-REF-02: Units of Measure are Unique by Code
- Each unit (KG, TON, ACRE, etc.) has a unique system code.
- **Enforcement**: `uq_units_of_measure_code`

### BR-REF-03: Expense Categories are Unique by Name
- Category names must be unique within the system.
- **Enforcement**: `uq_expense_categories_name`

---

## Module 2: Authentication

### BR-AUTH-01: User Email is Globally Unique
- No two users may share the same email address.
- **Enforcement**: `uq_users_email`

### BR-AUTH-02: Password Must Be Hashed
- `password_hash` must never store a plain-text password.
- **Enforcement**: Application layer (Spring Security BCrypt)

### BR-AUTH-03: Every User Must Have a Role
- `role_id` on users is NOT NULL.
- **Enforcement**: `NOT NULL` constraint

### BR-AUTH-04: Roles Have Permissions
- Permissions are granted to roles, not directly to users.
- Fine-grained access is managed via `role_permissions` junction table.
- **Enforcement**: Schema design

---

## Module 3: Farm Management

### BR-FARM-01: Farm Name Must Be Unique (Active Records)
- No two active farms (where `deleted_at IS NULL`) may share the same name.
- **Enforcement**: Partial unique index `uq_farms_name_active`

### BR-FARM-02: Land Size Must Be Positive
- `land_size > 0`
- **Enforcement**: `ck_farms_land_size_positive`

### BR-FARM-03: Ownership Type Must Be Valid
- Only `'OWNED'` or `'LEASED'` are valid values.
- **Enforcement**: `ck_farms_valid_ownership_type`

### BR-FARM-04: Leased Farms Must Have Lease Start Date
- If `ownership_type = 'LEASED'` then `lease_start_date IS NOT NULL`
- **Enforcement**: `ck_farms_leased_requires_start_date`

### BR-FARM-05: Farm Documents Must Reference an Existing Farm
- Every `farm_documents` record must link to a valid farm.
- **Enforcement**: FK `fk_farm_documents_farms` ON DELETE CASCADE

### BR-FARM-06: Farm Must Have a Status
- `status` is NOT NULL with DEFAULT `'ACTIVE'`
- **Enforcement**: `NOT NULL` + `ck_farms_valid_status`

### BR-FARM-07: A Farm Can Grow Multiple Fruit Types
- The `farm_fruit_types` junction table allows many-to-many between farms and fruits.
- Duplicate fruit-farm pairs are rejected.
- **Enforcement**: Composite PK on `farm_fruit_types(farm_id, fruit_type_id)`

---

## Module 4: Seasons

### BR-SEASON-01: Season Must Belong to a Farm and Fruit Type
- Every season references both a `farm_id` and a `fruit_type_id`.
- **Enforcement**: NOT NULL FKs

### BR-SEASON-02: Season End Date Must Be After Start Date
- `end_date > start_date`
- **Enforcement**: `ck_seasons_dates_valid`

### BR-SEASON-03: Season Year Must Be a Realistic Value
- `year BETWEEN 2000 AND 2100`
- **Enforcement**: `ck_seasons_year_range`

### BR-SEASON-04: Only One Active Season Per Farm-Fruit Combination
- A farm cannot have two overlapping ACTIVE seasons for the same fruit.
- **Enforcement**: Application service layer (complex overlap check)

---

## Module 5: Harvest Records

### BR-HARVEST-01: Harvest Must Belong to a Farm
- `farm_id` is NOT NULL FK.
- **Enforcement**: FK `fk_harvest_records_farms`

### BR-HARVEST-02: Harvest Must Belong to a Season
- `season_id` is NOT NULL FK.
- **Enforcement**: FK `fk_harvest_records_seasons`

### BR-HARVEST-03: Harvest Quantity Must Be Positive
- `harvest_quantity > 0`
- **Enforcement**: `ck_harvest_records_quantity_positive`

### BR-HARVEST-04: Harvest Date Cannot Be in the Future
- `harvest_date <= CURRENT_DATE`
- **Enforcement**: `ck_harvest_records_date_not_future` (at INSERT time) + Application layer

### BR-HARVEST-05: A Harvest Must Have at Least One Worker
- After confirmation, at least one record in `harvest_workers` must exist.
- **Enforcement**: Application service layer (pre-confirmation check)

### BR-HARVEST-06: Quality Grade Must Be Valid
- `quality_grade IN ('A', 'B', 'C', 'REJECT')`
- **Enforcement**: `ck_harvest_records_valid_grade`

### BR-HARVEST-07: Harvest Fruit Type Must Match Farm's Fruit Types
- The `fruit_type_id` on a harvest must exist in `farm_fruit_types` for that farm.
- **Enforcement**: Application service layer

---

## Module 6: Harvest Quality Checks

### BR-QUALITY-01: Quality Check Belongs to One Harvest
- `harvest_record_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-QUALITY-02: Defect Percentage Cannot Exceed 100
- `defect_percentage BETWEEN 0 AND 100`
- **Enforcement**: `ck_harvest_quality_checks_defect_pct`

---

## Module 7: Workers

### BR-WORKER-01: Worker Phone Must Be Unique (Active)
- No two active workers share the same phone number.
- **Enforcement**: Partial unique index

### BR-WORKER-02: Daily Wage Rate Must Be Positive
- `daily_wage_rate > 0`
- **Enforcement**: `ck_workers_wage_rate_positive`

### BR-WORKER-03: Worker Must Not Be Double-Counted in One Harvest Batch
- A worker can appear in only one row in `harvest_workers` per harvest.
- **Enforcement**: Composite unique `uq_harvest_workers_harvest_worker`

---

## Module 8: Worker Attendance

### BR-ATTEND-01: Attendance Must Reference a Worker
- `worker_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-ATTEND-02: One Attendance Record Per Worker Per Date
- A worker can have only one attendance record per date.
- **Enforcement**: Composite unique `uq_worker_attendance_worker_date`

### BR-ATTEND-03: Hours Worked Must Be Between 0 and 24
- `hours_worked BETWEEN 0 AND 24`
- **Enforcement**: `ck_worker_attendance_hours_valid`

---

## Module 9: Worker Payments

### BR-WPAY-01: Payment Must Reference a Worker
- `worker_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-WPAY-02: Payment Period End Must Be After Period Start
- `period_end >= period_start`
- **Enforcement**: `ck_worker_payments_period_valid`

### BR-WPAY-03: Payment Amount Must Be Positive
- `amount > 0`
- **Enforcement**: `ck_worker_payments_amount_positive`

### BR-WPAY-04: Payment Status Must Be Valid
- `payment_status IN ('PENDING', 'PAID', 'CANCELLED')`
- **Enforcement**: `ck_worker_payments_valid_status`

---

## Module 10: Customers

### BR-CUST-01: Customer Must Have a Name
- `name` is NOT NULL and NOT empty.
- **Enforcement**: `NOT NULL` + `ck_customers_name_not_empty`

### BR-CUST-02: Customer Phone Must Be Unique (Active)
- **Enforcement**: Partial unique index

---

## Module 11: Orders

### BR-ORDER-01: Order Must Belong to a Customer
- `customer_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-ORDER-02: Order Total Must Be Non-Negative
- `total_amount >= 0`
- **Enforcement**: `ck_orders_total_non_negative`

### BR-ORDER-03: Order Status Must Be Valid
- `order_status IN ('DRAFT','CONFIRMED','DISPATCHED','DELIVERED','INVOICED','PAID','CANCELLED')`
- **Enforcement**: `ck_orders_valid_status`

### BR-ORDER-04: Order Date Cannot Be in the Future
- `order_date <= CURRENT_DATE`
- **Enforcement**: `ck_orders_date_not_future`

---

## Module 12: Order Items

### BR-ITEM-01: Order Item Must Reference an Order
- `order_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-ITEM-02: Quantity Must Be Positive
- `quantity > 0`
- **Enforcement**: `ck_order_items_quantity_positive`

### BR-ITEM-03: Unit Price Must Be Non-Negative
- `unit_price >= 0`
- **Enforcement**: `ck_order_items_price_non_negative`

---

## Module 13: Invoices

### BR-INV-01: Invoice Must Reference an Order
- `order_id` is NOT NULL FK (1:1 relationship).
- **Enforcement**: FK + `uq_invoices_order_id`

### BR-INV-02: Invoice Number Must Be Globally Unique
- **Enforcement**: `uq_invoices_invoice_number`

### BR-INV-03: Due Date Must Be On or After Issue Date
- `due_date >= issued_at::date`
- **Enforcement**: `ck_invoices_due_date_valid`

### BR-INV-04: Paid Amount Cannot Exceed Total Amount
- `paid_amount <= total_amount`
- **Enforcement**: `ck_invoices_paid_not_exceed_total`

### BR-INV-05: Invoice Status Must Be Valid
- `invoice_status IN ('DRAFT','ISSUED','PARTIALLY_PAID','PAID','OVERDUE','CANCELLED')`
- **Enforcement**: `ck_invoices_valid_status`

---

## Module 14: Payments

### BR-PAY-01: Payment Must Reference an Invoice
- `invoice_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-PAY-02: Payment Amount Must Be Positive
- `amount > 0`
- **Enforcement**: `ck_payments_amount_positive`

### BR-PAY-03: Payment Cannot Cause Overpayment
- The sum of all payments for an invoice must not exceed `invoice.total_amount`.
- **Enforcement**: Application service layer (database trigger in V2)

### BR-PAY-04: Payment Status Must Be Valid
- `payment_status IN ('PENDING','COMPLETED','FAILED','REFUNDED')`
- **Enforcement**: `ck_payments_valid_status`

---

## Module 15: Expenses

### BR-EXP-01: Expense Must Belong to a Farm
- `farm_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-EXP-02: Expense Amount Must Be Positive
- `amount > 0`
- **Enforcement**: `ck_expenses_amount_positive`

### BR-EXP-03: Expense Date Cannot Be in the Future
- `expense_date <= CURRENT_DATE`
- **Enforcement**: `ck_expenses_date_not_future`

---

## Module 16: Inventory

### BR-INV-BATCH-01: Inventory Batch References a Harvest
- `harvest_record_id` is NOT NULL FK.
- **Enforcement**: FK

### BR-INV-BATCH-02: Available Quantity Cannot Be Negative
- `available_quantity >= 0`
- **Enforcement**: `ck_inventory_batches_quantity_non_negative`

### BR-INV-BATCH-03: Available Cannot Exceed Total
- `available_quantity <= total_quantity`
- **Enforcement**: `ck_inventory_batches_available_le_total`

---

## Module 17: Global Business Rules

### BR-GLOBAL-01: No Hard Deletes on Financial Records
- Records in `invoices`, `payments`, `orders`, `worker_payments`, `expenses` must use soft delete only.
- **Enforcement**: Application layer + `deleted_at` column design

### BR-GLOBAL-02: All Financial Amounts Use Numeric(15,2)
- Prevents floating-point rounding errors.
- **Enforcement**: Schema design

### BR-GLOBAL-03: All Timestamps Are Timezone-Aware
- All timestamp columns use `TIMESTAMPTZ`.
- **Enforcement**: Schema design

### BR-GLOBAL-04: All Status Columns Have a Default
- Every status column has a valid DEFAULT value.
- **Enforcement**: Schema design
