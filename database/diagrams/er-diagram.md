# Entity-Relationship Diagram

**Project**: Harvest Tracker
**Phase**: 0.6 — Database Foundation
**Notation**: Crow's Foot (Mermaid `erDiagram`)

---

## Full ER Diagram

```mermaid
erDiagram

    %% ─────────────────────────────────────────
    %% REFERENCE TABLES
    %% ─────────────────────────────────────────

    fruit_types {
        bigserial id PK
        varchar   name
        varchar   code
        text      description
        varchar   season_type
        int       sort_order
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    crop_variants {
        bigserial id PK
        bigint    fruit_type_id FK
        varchar   name
        varchar   code
        text      description
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    units_of_measure {
        bigserial id PK
        varchar   name
        varchar   code
        varchar   measure_type
        boolean   is_active
        int       sort_order
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    worker_types {
        bigserial id PK
        varchar   name
        varchar   code
        text      description
        boolean   is_active
        int       sort_order
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    payment_methods {
        bigserial id PK
        varchar   name
        varchar   code
        boolean   is_active
        int       sort_order
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    expense_categories {
        bigserial id PK
        varchar   name
        varchar   code
        text      description
        boolean   is_active
        int       sort_order
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    fruit_attributes {
        bigserial id PK
        bigint    fruit_type_id FK
        varchar   attribute_key
        varchar   attribute_label
        varchar   data_type
        text      description
        boolean   is_required
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% AUTH
    %% ─────────────────────────────────────────

    roles {
        bigserial id PK
        varchar   name
        varchar   code
        text      description
        boolean   is_active
        int       sort_order
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    permissions {
        bigserial id PK
        varchar   name
        varchar   code
        varchar   module
        text      description
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    role_permissions {
        bigserial id PK
        bigint    role_id FK
        bigint    permission_id FK
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    users {
        bigserial id PK
        bigint    role_id FK
        varchar   name
        varchar   email
        varchar   password_hash
        varchar   phone
        varchar   status
        timestamptz last_login_at
        boolean   is_active
        timestamptz created_at
        timestamptz updated_at
        bigint    created_by
        bigint    updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% FARM MODULE
    %% ─────────────────────────────────────────

    farms {
        bigserial    id PK
        bigint       owner_id FK
        bigint       land_uom_id FK
        varchar      name
        varchar      ownership_type
        numeric      land_size
        numeric      gps_latitude
        numeric      gps_longitude
        text         address
        varchar      status
        date         lease_start_date
        date         lease_end_date
        varchar      lessor_name
        text         notes
        boolean      is_active
        timestamptz  created_at
        timestamptz  updated_at
        bigint       created_by
        bigint       updated_by
        timestamptz  deleted_at
    }

    farm_documents {
        bigserial   id PK
        bigint      farm_id FK
        varchar     document_name
        varchar     document_type
        varchar     file_url
        varchar     file_mime_type
        bigint      file_size_bytes
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    farm_fruit_types {
        bigserial   id PK
        bigint      farm_id FK
        bigint      fruit_type_id FK
        boolean     is_primary
        date        first_planted_date
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    seasons {
        bigserial   id PK
        bigint      farm_id FK
        bigint      fruit_type_id FK
        varchar     name
        int         year
        date        start_date
        date        end_date
        varchar     status
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% HARVEST MODULE
    %% ─────────────────────────────────────────

    harvest_records {
        bigserial   id PK
        bigint      farm_id FK
        bigint      season_id FK
        bigint      fruit_type_id FK
        bigint      crop_variant_id FK
        bigint      quantity_uom_id FK
        bigint      supervisor_id FK
        date        harvest_date
        numeric     harvest_quantity
        varchar     quality_grade
        varchar     storage_location
        varchar     status
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    harvest_quality_checks {
        bigserial   id PK
        bigint      harvest_record_id FK
        bigint      checked_by FK
        timestamptz check_datetime
        varchar     quality_grade
        numeric     defect_percentage
        numeric     average_weight_grams
        text        observations
        boolean     is_approved
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    harvest_workers {
        bigserial   id PK
        bigint      harvest_record_id FK
        bigint      worker_id FK
        varchar     role_in_harvest
        numeric     hours_worked
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% WORKER MODULE
    %% ─────────────────────────────────────────

    workers {
        bigserial   id PK
        bigint      farm_id FK
        bigint      worker_type_id FK
        varchar     name
        varchar     phone
        varchar     id_card_number
        numeric     daily_wage_rate
        bigint      wage_uom_id FK
        varchar     status
        date        joining_date
        text        address
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    worker_attendance {
        bigserial   id PK
        bigint      worker_id FK
        bigint      harvest_record_id FK
        date        attendance_date
        boolean     is_present
        numeric     hours_worked
        text        remarks
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    worker_payments {
        bigserial   id PK
        bigint      worker_id FK
        bigint      payment_method_id FK
        date        period_start
        date        period_end
        int         total_days_worked
        numeric     daily_wage_rate
        numeric     amount
        varchar     payment_status
        date        paid_date
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% SALES MODULE
    %% ─────────────────────────────────────────

    customers {
        bigserial   id PK
        varchar     name
        varchar     phone
        varchar     email
        text        address
        varchar     customer_type
        varchar     status
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    orders {
        bigserial   id PK
        bigint      customer_id FK
        date        order_date
        varchar     order_status
        numeric     total_amount
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    order_items {
        bigserial   id PK
        bigint      order_id FK
        bigint      harvest_record_id FK
        bigint      fruit_type_id FK
        bigint      crop_variant_id FK
        bigint      quantity_uom_id FK
        numeric     quantity
        numeric     unit_price
        numeric     line_total
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    sales {
        bigserial   id PK
        bigint      order_id FK
        bigint      farm_id FK
        date        sale_date
        numeric     quantity_sold
        bigint      quantity_uom_id FK
        numeric     sale_price_per_unit
        numeric     total_sale_amount
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    transport_records {
        bigserial   id PK
        bigint      order_id FK
        varchar     vehicle_number
        varchar     driver_name
        varchar     driver_phone
        varchar     transporter_name
        numeric     transport_cost
        date        dispatch_date
        date        delivery_date
        varchar     delivery_status
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    invoices {
        bigserial   id PK
        bigint      order_id FK
        bigint      customer_id FK
        varchar     invoice_number
        timestamptz issued_at
        date        due_date
        numeric     total_amount
        numeric     paid_amount
        varchar     invoice_status
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    payments {
        bigserial   id PK
        bigint      invoice_id FK
        bigint      payment_method_id FK
        date        payment_date
        numeric     amount
        varchar     payment_status
        varchar     reference_number
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% EXPENSE MODULE
    %% ─────────────────────────────────────────

    expenses {
        bigserial   id PK
        bigint      farm_id FK
        bigint      harvest_record_id FK
        bigint      expense_category_id FK
        bigint      payment_method_id FK
        date        expense_date
        numeric     amount
        varchar     status
        text        description
        text        notes
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% INVENTORY MODULE
    %% ─────────────────────────────────────────

    inventory_batches {
        bigserial   id PK
        bigint      harvest_record_id FK
        bigint      farm_id FK
        bigint      fruit_type_id FK
        bigint      quantity_uom_id FK
        numeric     total_quantity
        numeric     available_quantity
        varchar     storage_location
        varchar     batch_code
        date        best_before_date
        varchar     status
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% REPORTING
    %% ─────────────────────────────────────────

    report_snapshots {
        bigserial   id PK
        bigint      farm_id FK
        bigint      season_id FK
        varchar     report_type
        varchar     report_period
        date        period_start
        date        period_end
        jsonb       report_data
        varchar     status
        boolean     is_active
        timestamptz created_at
        timestamptz updated_at
        bigint      created_by
        bigint      updated_by
        timestamptz deleted_at
    }

    %% ─────────────────────────────────────────
    %% RELATIONSHIPS
    %% ─────────────────────────────────────────

    fruit_types        ||--o{ crop_variants          : "has variants"
    fruit_types        ||--o{ fruit_attributes        : "has attributes"
    fruit_types        ||--o{ farm_fruit_types        : "cultivated on"
    fruit_types        ||--o{ seasons                 : "season for"
    fruit_types        ||--o{ harvest_records         : "fruit of"

    crop_variants      ||--o{ harvest_records         : "variant harvested"
    crop_variants      ||--o{ order_items             : "variant in order"

    roles              ||--o{ role_permissions        : "grants"
    roles              ||--o{ users                   : "assigned to"
    permissions        ||--o{ role_permissions        : "included in"

    users              ||--o{ farms                   : "owns"
    users              ||--o{ harvest_records         : "supervised"

    farms              ||--o{ farm_documents          : "has documents"
    farms              ||--o{ farm_fruit_types        : "cultivates"
    farms              ||--o{ seasons                 : "has seasons"
    farms              ||--o{ harvest_records         : "produces"
    farms              ||--o{ workers                 : "employs"
    farms              ||--o{ expenses                : "incurs"
    farms              ||--o{ inventory_batches       : "stores in"
    farms              ||--o{ sales                   : "sells from"
    farms              ||--o{ report_snapshots        : "reports for"

    seasons            ||--o{ harvest_records         : "groups"
    seasons            ||--o{ report_snapshots        : "summarised"

    harvest_records    ||--o{ harvest_quality_checks  : "quality checked"
    harvest_records    ||--o{ harvest_workers         : "worked by"
    harvest_records    ||--o{ worker_attendance       : "attendance for"
    harvest_records    ||--o{ order_items             : "sold as"
    harvest_records    ||--o{ expenses                : "expenses for"
    harvest_records    ||--o{ inventory_batches       : "stored as"

    workers            ||--o{ harvest_workers         : "works on"
    workers            ||--o{ worker_attendance       : "has attendance"
    workers            ||--o{ worker_payments         : "receives payment"

    worker_types       ||--o{ workers                 : "classifies"
    payment_methods    ||--o{ worker_payments         : "paid via"
    payment_methods    ||--o{ payments                : "paid via"

    units_of_measure   ||--o{ farms                   : "land unit"
    units_of_measure   ||--o{ harvest_records         : "quantity unit"
    units_of_measure   ||--o{ workers                 : "wage unit"
    units_of_measure   ||--o{ order_items             : "quantity unit"
    units_of_measure   ||--o{ sales                   : "quantity unit"
    units_of_measure   ||--o{ inventory_batches       : "quantity unit"

    customers          ||--o{ orders                  : "places"
    customers          ||--o{ invoices                : "billed to"

    orders             ||--o{ order_items             : "contains"
    orders             ||--|{ invoices                : "billed as"
    orders             ||--o{ transport_records       : "transported via"
    orders             ||--o{ sales                   : "fulfilled by"

    invoices           ||--o{ payments                : "paid by"

    expense_categories ||--o{ expenses                : "categorised as"
```

---

## Entity Groups Summary

```
Reference Tables (7)
├── fruit_types
├── crop_variants
├── units_of_measure
├── worker_types
├── payment_methods
├── expense_categories
└── fruit_attributes

Auth (4)
├── roles
├── permissions
├── role_permissions
└── users

Farm Module (4)
├── farms
├── farm_documents
├── farm_fruit_types
└── seasons

Harvest Module (3)
├── harvest_records
├── harvest_quality_checks
└── harvest_workers

Worker Module (3)
├── workers
├── worker_attendance
└── worker_payments

Sales Module (7)
├── customers
├── orders
├── order_items
├── sales
├── transport_records
├── invoices
└── payments

Expense Module (1)
└── expenses

Inventory Module (1)
└── inventory_batches

Reporting (1)
└── report_snapshots

Total: 31 tables
```
