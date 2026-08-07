# Schema Design

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation
**PostgreSQL**: 15+
**Schema**: `harvest_tracker`

---

## Overview

31 tables across 9 functional domains. All tables share a consistent 6-column audit pattern. Every foreign key is indexed. Soft delete is used universally.

---

## Module 1: Reference Tables

### `fruit_types`
Master list of all supported fruit types. Enables multi-fruit expansion without schema changes.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | Surrogate key |
| name | VARCHAR(100) | NOT NULL | Full name (e.g., Mango) |
| code | VARCHAR(20) | NOT NULL, UNIQUE | System code (e.g., MANGO) |
| description | TEXT | | Optional description |
| season_type | VARCHAR(50) | | SUMMER, MONSOON, YEAR_ROUND |
| sort_order | INT | NOT NULL, DEFAULT 0 | Display ordering |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | |
| created_by | BIGINT | FK → users | |
| updated_by | BIGINT | FK → users | |
| deleted_at | TIMESTAMPTZ | | Soft delete |

---

### `crop_variants`
Sub-types of fruit (e.g., Alphonso, Kesar for Mango).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | Parent fruit |
| name | VARCHAR(100) | NOT NULL | Variant name |
| code | VARCHAR(20) | NOT NULL | Variant code |
| description | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `units_of_measure`
Reference table for quantity, weight, and area units.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(50) | NOT NULL | Full name (Kilogram) |
| code | VARCHAR(10) | NOT NULL, UNIQUE | System code (KG) |
| measure_type | VARCHAR(30) | NOT NULL | WEIGHT, AREA, VOLUME |
| sort_order | INT | NOT NULL, DEFAULT 0 | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `worker_types`
Classification of workers (DAILY, SEASONAL, CONTRACT).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(100) | NOT NULL | |
| code | VARCHAR(20) | NOT NULL, UNIQUE | |
| description | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| sort_order | INT | NOT NULL, DEFAULT 0 | |
| + audit columns | | | |

---

### `payment_methods`
Reference list: CASH, BANK_TRANSFER, UPI, CHEQUE.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(100) | NOT NULL | |
| code | VARCHAR(20) | NOT NULL, UNIQUE | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| sort_order | INT | NOT NULL, DEFAULT 0 | |
| + audit columns | | | |

---

### `expense_categories`
Reference categories: LABOUR, TRANSPORT, PACKAGING, IRRIGATION, RENT, OTHER.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(100) | NOT NULL, UNIQUE | |
| code | VARCHAR(20) | NOT NULL, UNIQUE | |
| description | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| sort_order | INT | NOT NULL, DEFAULT 0 | |
| + audit columns | | | |

---

### `fruit_attributes`
EAV definition table. Defines what metadata attributes a fruit type has.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | |
| attribute_key | VARCHAR(100) | NOT NULL | Machine key (brix_level) |
| attribute_label | VARCHAR(150) | NOT NULL | Display label (Brix Level) |
| data_type | VARCHAR(20) | NOT NULL | TEXT, NUMBER, DATE, BOOLEAN |
| description | TEXT | | |
| is_required | BOOLEAN | NOT NULL, DEFAULT FALSE | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 2: Auth Tables

### `roles`
System roles: ADMIN, FARM_OWNER, MANAGER, SUPERVISOR, ACCOUNTANT.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(100) | NOT NULL, UNIQUE | |
| code | VARCHAR(30) | NOT NULL, UNIQUE | |
| description | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| sort_order | INT | NOT NULL, DEFAULT 0 | |
| + audit columns | | | |

---

### `permissions`
Fine-grained permission codes: FARM_CREATE, HARVEST_VIEW, etc.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(150) | NOT NULL | |
| code | VARCHAR(100) | NOT NULL, UNIQUE | |
| module | VARCHAR(50) | NOT NULL | FARM, HARVEST, WORKER, SALES |
| description | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `role_permissions`
Many-to-many junction: which permissions are granted to each role.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| role_id | BIGINT | NOT NULL, FK → roles | |
| permission_id | BIGINT | NOT NULL, FK → permissions | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |
| UNIQUE | | (role_id, permission_id) | |

---

### `users`
System operator accounts.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| role_id | BIGINT | NOT NULL, FK → roles | |
| name | VARCHAR(200) | NOT NULL | |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Login identifier |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt hash |
| phone | VARCHAR(20) | | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE, SUSPENDED |
| last_login_at | TIMESTAMPTZ | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 3: Farm Module

### `farms`
Core entity. Every harvest, worker, sale, and expense belongs to a farm.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| owner_id | BIGINT | NOT NULL, FK → users | Farm owner user |
| land_uom_id | BIGINT | FK → units_of_measure | Unit for land_size |
| name | VARCHAR(200) | NOT NULL, UNIQUE (partial) | |
| ownership_type | VARCHAR(20) | NOT NULL, DEFAULT 'OWNED' | OWNED or LEASED |
| land_size | NUMERIC(10,3) | CHECK > 0 | |
| gps_latitude | NUMERIC(10,7) | | |
| gps_longitude | NUMERIC(10,7) | | |
| address | TEXT | | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE, ARCHIVED |
| lease_start_date | DATE | | Required if LEASED |
| lease_end_date | DATE | | |
| lessor_name | VARCHAR(200) | | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

**Check constraints:**
- `ck_farms_land_size_positive`: land_size > 0
- `ck_farms_valid_ownership_type`: ownership_type IN ('OWNED','LEASED')
- `ck_farms_valid_status`: status IN ('ACTIVE','INACTIVE','ARCHIVED')
- `ck_farms_leased_requires_start_date`: ownership_type = 'LEASED' → lease_start_date IS NOT NULL

---

### `farm_documents`
Supporting documents: land title, lease agreement, certificates.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms CASCADE | |
| document_name | VARCHAR(255) | NOT NULL | |
| document_type | VARCHAR(50) | NOT NULL | TITLE_DEED, LEASE, CERTIFICATE, OTHER |
| file_url | VARCHAR(500) | | Storage URL |
| file_mime_type | VARCHAR(100) | | |
| file_size_bytes | BIGINT | | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `farm_fruit_types`
Junction: a farm can cultivate multiple fruit types.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | |
| is_primary | BOOLEAN | NOT NULL, DEFAULT FALSE | Primary crop |
| first_planted_date | DATE | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |
| UNIQUE | | (farm_id, fruit_type_id) | |

---

### `seasons`
Named time windows: Mango 2024, Banana Summer 2025.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | |
| name | VARCHAR(150) | NOT NULL | "Mango Season 2024" |
| year | INT | NOT NULL | CHECK 2000–2100 |
| start_date | DATE | NOT NULL | |
| end_date | DATE | NOT NULL | CHECK > start_date |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PLANNED' | PLANNED, ACTIVE, CLOSED |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 4: Harvest Module

### `harvest_records`
Core operational record. One row = one harvest batch.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms | |
| season_id | BIGINT | NOT NULL, FK → seasons | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | |
| crop_variant_id | BIGINT | FK → crop_variants | Optional |
| quantity_uom_id | BIGINT | FK → units_of_measure | KG, TON |
| supervisor_id | BIGINT | FK → users | |
| harvest_date | DATE | NOT NULL, CHECK ≤ TODAY | |
| harvest_quantity | NUMERIC(12,3) | NOT NULL, CHECK > 0 | |
| quality_grade | VARCHAR(10) | NOT NULL | A, B, C, REJECT |
| storage_location | VARCHAR(200) | | Warehouse, cold store |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'DRAFT' | DRAFT, CONFIRMED, STORED, SOLD |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `harvest_quality_checks`
Quality inspection records per harvest batch.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| harvest_record_id | BIGINT | NOT NULL, FK → harvest_records | |
| checked_by | BIGINT | FK → users | Inspector |
| check_datetime | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | |
| quality_grade | VARCHAR(10) | NOT NULL | A, B, C, REJECT |
| defect_percentage | NUMERIC(5,2) | CHECK 0–100 | |
| average_weight_grams | NUMERIC(8,2) | | |
| observations | TEXT | | |
| is_approved | BOOLEAN | NOT NULL, DEFAULT FALSE | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `harvest_workers`
Many-to-many: which workers participated in each harvest.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| harvest_record_id | BIGINT | NOT NULL, FK → harvest_records | |
| worker_id | BIGINT | NOT NULL, FK → workers | |
| role_in_harvest | VARCHAR(100) | | PICKER, PACKER, LOADER |
| hours_worked | NUMERIC(4,2) | CHECK 0–24 | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |
| UNIQUE | | (harvest_record_id, worker_id) | No double counting |

---

## Module 5: Worker Module

### `workers`
Field labourers. Separate from system users.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms | Primary farm |
| worker_type_id | BIGINT | FK → worker_types | |
| wage_uom_id | BIGINT | FK → units_of_measure | PER_DAY |
| name | VARCHAR(200) | NOT NULL | |
| phone | VARCHAR(20) | UNIQUE (partial) | |
| id_card_number | VARCHAR(50) | | Aadhaar, etc. |
| daily_wage_rate | NUMERIC(10,2) | NOT NULL, CHECK > 0 | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE |
| joining_date | DATE | | |
| address | TEXT | | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `worker_attendance`
Daily attendance records.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| worker_id | BIGINT | NOT NULL, FK → workers | |
| harvest_record_id | BIGINT | FK → harvest_records | Optional link |
| attendance_date | DATE | NOT NULL | |
| is_present | BOOLEAN | NOT NULL | |
| hours_worked | NUMERIC(4,2) | CHECK 0–24 | |
| remarks | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |
| UNIQUE | | (worker_id, attendance_date) | One record per day |

---

### `worker_payments`
Payroll settlement records per period.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| worker_id | BIGINT | NOT NULL, FK → workers | |
| payment_method_id | BIGINT | FK → payment_methods | |
| period_start | DATE | NOT NULL | |
| period_end | DATE | NOT NULL, CHECK ≥ period_start | |
| total_days_worked | INT | NOT NULL | Computed from attendance |
| daily_wage_rate | NUMERIC(10,2) | NOT NULL | Snapshot at payment time |
| amount | NUMERIC(12,2) | NOT NULL, CHECK > 0 | |
| payment_status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | PENDING, PAID, CANCELLED |
| paid_date | DATE | | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 6: Sales Module

### `customers`
Buyer profiles.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| name | VARCHAR(200) | NOT NULL | |
| phone | VARCHAR(20) | UNIQUE (partial) | |
| email | VARCHAR(255) | | |
| address | TEXT | | |
| customer_type | VARCHAR(30) | NOT NULL, DEFAULT 'RETAIL' | RETAIL, WHOLESALE, EXPORT |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, INACTIVE |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `orders`
Sales order header.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| customer_id | BIGINT | NOT NULL, FK → customers | |
| order_date | DATE | NOT NULL, CHECK ≤ TODAY | |
| order_status | VARCHAR(20) | NOT NULL, DEFAULT 'DRAFT' | State machine |
| total_amount | NUMERIC(15,2) | NOT NULL, DEFAULT 0 | Denormalised sum |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `order_items`
Line items within an order. Each item references a harvest batch.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| order_id | BIGINT | NOT NULL, FK → orders | |
| harvest_record_id | BIGINT | FK → harvest_records | Source harvest |
| fruit_type_id | BIGINT | FK → fruit_types | |
| crop_variant_id | BIGINT | FK → crop_variants | |
| quantity_uom_id | BIGINT | FK → units_of_measure | |
| quantity | NUMERIC(12,3) | NOT NULL, CHECK > 0 | |
| unit_price | NUMERIC(12,2) | NOT NULL, CHECK ≥ 0 | |
| line_total | NUMERIC(15,2) | NOT NULL | quantity × unit_price |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `sales`
Actual sale event (dispatch confirmation).

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| order_id | BIGINT | NOT NULL, FK → orders | |
| farm_id | BIGINT | NOT NULL, FK → farms | Source farm |
| quantity_uom_id | BIGINT | FK → units_of_measure | |
| sale_date | DATE | NOT NULL | |
| quantity_sold | NUMERIC(12,3) | NOT NULL, CHECK > 0 | |
| sale_price_per_unit | NUMERIC(12,2) | NOT NULL | |
| total_sale_amount | NUMERIC(15,2) | NOT NULL | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `transport_records`
Logistics details per order.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| order_id | BIGINT | NOT NULL, FK → orders | |
| vehicle_number | VARCHAR(20) | | |
| driver_name | VARCHAR(200) | | |
| driver_phone | VARCHAR(20) | | |
| transporter_name | VARCHAR(200) | | |
| transport_cost | NUMERIC(12,2) | | |
| dispatch_date | DATE | | |
| delivery_date | DATE | | |
| delivery_status | VARCHAR(30) | | PENDING, IN_TRANSIT, DELIVERED, FAILED |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

### `invoices`
One invoice per order. 1:1 relationship.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| order_id | BIGINT | NOT NULL, FK → orders, UNIQUE | |
| customer_id | BIGINT | NOT NULL, FK → customers | Denormalised for query speed |
| invoice_number | VARCHAR(50) | NOT NULL, UNIQUE | INV-2024-001 |
| issued_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | |
| due_date | DATE | NOT NULL | |
| total_amount | NUMERIC(15,2) | NOT NULL | |
| paid_amount | NUMERIC(15,2) | NOT NULL, DEFAULT 0 | Updated on payment |
| invoice_status | VARCHAR(20) | NOT NULL, DEFAULT 'DRAFT' | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

**Check constraints:**
- `ck_invoices_paid_not_exceed_total`: paid_amount ≤ total_amount
- `ck_invoices_due_date_valid`: due_date ≥ issued_at::date
- `ck_invoices_valid_status`: invoice_status IN ('DRAFT','ISSUED','PARTIALLY_PAID','PAID','OVERDUE','CANCELLED')

---

### `payments`
Individual payment transactions against an invoice.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| invoice_id | BIGINT | NOT NULL, FK → invoices | |
| payment_method_id | BIGINT | FK → payment_methods | |
| payment_date | DATE | NOT NULL | |
| amount | NUMERIC(12,2) | NOT NULL, CHECK > 0 | |
| payment_status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | |
| reference_number | VARCHAR(100) | | Bank ref, UTR |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 7: Expense Module

### `expenses`
Operational costs at farm or harvest level.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | NOT NULL, FK → farms | |
| harvest_record_id | BIGINT | FK → harvest_records | Optional |
| expense_category_id | BIGINT | NOT NULL, FK → expense_categories | |
| payment_method_id | BIGINT | FK → payment_methods | |
| expense_date | DATE | NOT NULL, CHECK ≤ TODAY | |
| amount | NUMERIC(12,2) | NOT NULL, CHECK > 0 | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'RECORDED' | RECORDED, APPROVED, REJECTED |
| description | TEXT | | |
| notes | TEXT | | |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

---

## Module 8: Inventory

### `inventory_batches`
Tracks available fruit from harvest to sale.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| harvest_record_id | BIGINT | NOT NULL, FK → harvest_records | |
| farm_id | BIGINT | NOT NULL, FK → farms | |
| fruit_type_id | BIGINT | NOT NULL, FK → fruit_types | |
| quantity_uom_id | BIGINT | FK → units_of_measure | |
| batch_code | VARCHAR(50) | | INV-2024-001 |
| total_quantity | NUMERIC(12,3) | NOT NULL, CHECK > 0 | At harvest |
| available_quantity | NUMERIC(12,3) | NOT NULL, CHECK ≥ 0 | Remaining |
| storage_location | VARCHAR(200) | | |
| best_before_date | DATE | | |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'IN_STOCK' | IN_STOCK, PARTIAL, DEPLETED |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |

**Check constraints:**
- `ck_inventory_batches_quantity_non_negative`: available_quantity ≥ 0
- `ck_inventory_batches_available_le_total`: available_quantity ≤ total_quantity

---

## Module 9: Reporting

### `report_snapshots`
Stores closed-period report results as JSONB.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGSERIAL | PK | |
| farm_id | BIGINT | FK → farms | |
| season_id | BIGINT | FK → seasons | |
| report_type | VARCHAR(50) | NOT NULL | SEASON, HARVEST, FINANCIAL, WORKER |
| report_period | VARCHAR(50) | NOT NULL | MONTHLY, SEASONAL, ANNUAL |
| period_start | DATE | NOT NULL | |
| period_end | DATE | NOT NULL | |
| report_data | JSONB | NOT NULL | Serialized report payload |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'GENERATED' | GENERATED, FINAL, ARCHIVED |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | |
| + audit columns | | | |
