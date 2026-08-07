-- =============================================================================
-- V1__initial_schema.sql
-- =============================================================================
-- Project      : Harvest Tracker
-- Phase        : 0.6 — Database Foundation
-- PostgreSQL   : 15+
-- Migration    : V1 — Initial Schema
-- Description  : Creates the complete database schema for the Harvest Tracker
--                platform, including all 31 tables, constraints, indexes,
--                triggers, and seeds for reference data.
--
-- Flyway convention: V{version}__{description}.sql
--                    Two underscores between version and description.
--
-- WARNING: Never modify this file after it has been applied to any environment.
--          Create a new migration file (V2__...) for all future changes.
-- =============================================================================

-- =============================================================================
-- EXTENSIONS
-- =============================================================================

CREATE EXTENSION IF NOT EXISTS pg_trgm;       -- Trigram text search
CREATE EXTENSION IF NOT EXISTS pgcrypto;      -- gen_random_uuid() if needed later

-- =============================================================================
-- SCHEMA
-- =============================================================================

CREATE SCHEMA IF NOT EXISTS harvest_tracker;

-- Set search path for this migration session
SET search_path TO harvest_tracker, public;

-- =============================================================================
-- SHARED TRIGGER FUNCTION: set_updated_at()
-- Applied to every table to auto-maintain the updated_at column.
-- =============================================================================

CREATE OR REPLACE FUNCTION harvest_tracker.set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =============================================================================
-- MODULE 1: REFERENCE TABLES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- fruit_types
-- Master list of all fruit types supported by the platform.
-- Adding a new fruit requires only an INSERT here — no schema change.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.fruit_types (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(20)     NOT NULL,
    description     TEXT,
    season_type     VARCHAR(50),
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_fruit_types_valid_season_type
        CHECK (season_type IN ('SUMMER', 'MONSOON', 'WINTER', 'YEAR_ROUND') OR season_type IS NULL)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_fruit_types_code
    ON harvest_tracker.fruit_types(code);

CREATE UNIQUE INDEX IF NOT EXISTS uq_fruit_types_name
    ON harvest_tracker.fruit_types(name)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_fruit_types_updated_at
    BEFORE UPDATE ON harvest_tracker.fruit_types
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- crop_variants
-- Sub-types per fruit (Alphonso, Kesar for Mango; Cavendish for Banana).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.crop_variants (
    id              BIGSERIAL       PRIMARY KEY,
    fruit_type_id   BIGINT          NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(30)     NOT NULL,
    description     TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_crop_variants_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_crop_variants_fruit_type_id
    ON harvest_tracker.crop_variants(fruit_type_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_crop_variants_fruit_code
    ON harvest_tracker.crop_variants(fruit_type_id, code)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_crop_variants_updated_at
    BEFORE UPDATE ON harvest_tracker.crop_variants
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- units_of_measure
-- Reference table for all measurement units: KG, TON, ACRE, HECTARE, etc.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.units_of_measure (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(50)     NOT NULL,
    code            VARCHAR(10)     NOT NULL,
    measure_type    VARCHAR(30)     NOT NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_units_of_measure_valid_type
        CHECK (measure_type IN ('WEIGHT', 'AREA', 'VOLUME', 'COUNT', 'TIME'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_units_of_measure_code
    ON harvest_tracker.units_of_measure(code);

CREATE TRIGGER trg_units_of_measure_updated_at
    BEFORE UPDATE ON harvest_tracker.units_of_measure
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- worker_types
-- Classifies workers: DAILY, SEASONAL, CONTRACT, SUPERVISOR.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.worker_types (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(20)     NOT NULL,
    description     TEXT,
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_worker_types_code
    ON harvest_tracker.worker_types(code);

CREATE TRIGGER trg_worker_types_updated_at
    BEFORE UPDATE ON harvest_tracker.worker_types
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- payment_methods
-- Reference: CASH, BANK_TRANSFER, UPI, CHEQUE, NEFT, RTGS.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.payment_methods (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(20)     NOT NULL,
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_payment_methods_code
    ON harvest_tracker.payment_methods(code);

CREATE TRIGGER trg_payment_methods_updated_at
    BEFORE UPDATE ON harvest_tracker.payment_methods
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- expense_categories
-- Reference: LABOUR, TRANSPORT, PACKAGING, IRRIGATION, RENT, FERTILIZER, OTHER.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.expense_categories (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(20)     NOT NULL,
    description     TEXT,
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_expense_categories_code
    ON harvest_tracker.expense_categories(code);

CREATE UNIQUE INDEX IF NOT EXISTS uq_expense_categories_name
    ON harvest_tracker.expense_categories(name)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_expense_categories_updated_at
    BEFORE UPDATE ON harvest_tracker.expense_categories
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- fruit_attributes
-- EAV attribute definitions per fruit type.
-- Allows fruit-specific metadata without altering core tables.
-- Example: fruit_type=MANGO, attribute_key='brix_level', data_type='NUMBER'
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.fruit_attributes (
    id              BIGSERIAL       PRIMARY KEY,
    fruit_type_id   BIGINT          NOT NULL,
    attribute_key   VARCHAR(100)    NOT NULL,
    attribute_label VARCHAR(150)    NOT NULL,
    data_type       VARCHAR(20)     NOT NULL DEFAULT 'TEXT',
    description     TEXT,
    is_required     BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_fruit_attributes_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT,
    CONSTRAINT ck_fruit_attributes_valid_data_type
        CHECK (data_type IN ('TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'SELECT'))
);

CREATE INDEX IF NOT EXISTS idx_fruit_attributes_fruit_type_id
    ON harvest_tracker.fruit_attributes(fruit_type_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_fruit_attributes_fruit_key
    ON harvest_tracker.fruit_attributes(fruit_type_id, attribute_key)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_fruit_attributes_updated_at
    BEFORE UPDATE ON harvest_tracker.fruit_attributes
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 2: AUTH TABLES
-- =============================================================================

-- -----------------------------------------------------------------------------
-- roles
-- System roles: ADMIN, FARM_OWNER, MANAGER, SUPERVISOR, ACCOUNTANT.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.roles (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    code            VARCHAR(30)     NOT NULL,
    description     TEXT,
    sort_order      INT             NOT NULL DEFAULT 0,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_roles_code
    ON harvest_tracker.roles(code);

CREATE UNIQUE INDEX IF NOT EXISTS uq_roles_name
    ON harvest_tracker.roles(name)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_roles_updated_at
    BEFORE UPDATE ON harvest_tracker.roles
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- permissions
-- Fine-grained capability codes: FARM_CREATE, HARVEST_VIEW, etc.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.permissions (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL,
    code            VARCHAR(100)    NOT NULL,
    module          VARCHAR(50)     NOT NULL,
    description     TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT ck_permissions_valid_module
        CHECK (module IN ('AUTH', 'FARM', 'HARVEST', 'WORKER', 'SALES', 'EXPENSE',
                          'INVENTORY', 'REPORT', 'ADMIN'))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_permissions_code
    ON harvest_tracker.permissions(code);

CREATE INDEX IF NOT EXISTS idx_permissions_module
    ON harvest_tracker.permissions(module);

CREATE TRIGGER trg_permissions_updated_at
    BEFORE UPDATE ON harvest_tracker.permissions
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- role_permissions
-- Many-to-many junction between roles and permissions.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.role_permissions (
    id              BIGSERIAL       PRIMARY KEY,
    role_id         BIGINT          NOT NULL,
    permission_id   BIGINT          NOT NULL,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_role_permissions_roles
        FOREIGN KEY (role_id) REFERENCES harvest_tracker.roles(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permissions
        FOREIGN KEY (permission_id) REFERENCES harvest_tracker.permissions(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id
    ON harvest_tracker.role_permissions(role_id);

CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id
    ON harvest_tracker.role_permissions(permission_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_role_permissions_pair
    ON harvest_tracker.role_permissions(role_id, permission_id)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_role_permissions_updated_at
    BEFORE UPDATE ON harvest_tracker.role_permissions
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- users
-- System operator accounts. Separate from field workers.
-- Password stored as BCrypt hash only. Plain text never stored.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.users (
    id              BIGSERIAL       PRIMARY KEY,
    role_id         BIGINT          NOT NULL,
    name            VARCHAR(200)    NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL,
    phone           VARCHAR(20),
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    last_login_at   TIMESTAMPTZ,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_users_roles
        FOREIGN KEY (role_id) REFERENCES harvest_tracker.roles(id)
        ON DELETE RESTRICT,
    CONSTRAINT ck_users_valid_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT ck_users_name_not_empty
        CHECK (TRIM(name) <> '')
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_users_email
    ON harvest_tracker.users(email)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_users_role_id
    ON harvest_tracker.users(role_id);

CREATE INDEX IF NOT EXISTS idx_users_status
    ON harvest_tracker.users(status)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON harvest_tracker.users
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- Now add FK references for audit columns back to users
-- (Added after users table exists)
ALTER TABLE harvest_tracker.fruit_types
    ADD CONSTRAINT fk_fruit_types_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_fruit_types_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.crop_variants
    ADD CONSTRAINT fk_crop_variants_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_crop_variants_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.units_of_measure
    ADD CONSTRAINT fk_uom_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_uom_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.worker_types
    ADD CONSTRAINT fk_worker_types_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_worker_types_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.payment_methods
    ADD CONSTRAINT fk_payment_methods_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_payment_methods_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.expense_categories
    ADD CONSTRAINT fk_expense_categories_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_expense_categories_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.fruit_attributes
    ADD CONSTRAINT fk_fruit_attributes_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_fruit_attributes_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.roles
    ADD CONSTRAINT fk_roles_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_roles_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.permissions
    ADD CONSTRAINT fk_permissions_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_permissions_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.role_permissions
    ADD CONSTRAINT fk_role_permissions_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_role_permissions_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

ALTER TABLE harvest_tracker.users
    ADD CONSTRAINT fk_users_created_by FOREIGN KEY (created_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL,
    ADD CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by)
        REFERENCES harvest_tracker.users(id) ON DELETE SET NULL;

-- =============================================================================
-- MODULE 3: FARM MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- farms
-- Core entity. Foundation of all downstream operations.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.farms (
    id                  BIGSERIAL       PRIMARY KEY,
    owner_id            BIGINT          NOT NULL,
    land_uom_id         BIGINT,
    name                VARCHAR(200)    NOT NULL,
    ownership_type      VARCHAR(20)     NOT NULL DEFAULT 'OWNED',
    land_size           NUMERIC(10,3),
    gps_latitude        NUMERIC(10,7),
    gps_longitude       NUMERIC(10,7),
    address             TEXT,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    lease_start_date    DATE,
    lease_end_date      DATE,
    lessor_name         VARCHAR(200),
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_farms_owner
        FOREIGN KEY (owner_id) REFERENCES harvest_tracker.users(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_farms_land_uom
        FOREIGN KEY (land_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_farms_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_farms_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_farms_valid_ownership_type
        CHECK (ownership_type IN ('OWNED', 'LEASED')),
    CONSTRAINT ck_farms_valid_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT ck_farms_land_size_positive
        CHECK (land_size IS NULL OR land_size > 0),
    CONSTRAINT ck_farms_leased_requires_start_date
        CHECK (ownership_type <> 'LEASED' OR lease_start_date IS NOT NULL),
    CONSTRAINT ck_farms_name_not_empty
        CHECK (TRIM(name) <> ''),
    CONSTRAINT ck_farms_gps_latitude_range
        CHECK (gps_latitude IS NULL OR (gps_latitude BETWEEN -90 AND 90)),
    CONSTRAINT ck_farms_gps_longitude_range
        CHECK (gps_longitude IS NULL OR (gps_longitude BETWEEN -180 AND 180))
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_farms_name_active
    ON harvest_tracker.farms(name)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_farms_owner_id
    ON harvest_tracker.farms(owner_id);

CREATE INDEX IF NOT EXISTS idx_farms_status
    ON harvest_tracker.farms(status)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_farms_name_trgm
    ON harvest_tracker.farms USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_farms_updated_at
    BEFORE UPDATE ON harvest_tracker.farms
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- farm_documents
-- Supporting documents per farm (land title, lease contract, certificates).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.farm_documents (
    id              BIGSERIAL       PRIMARY KEY,
    farm_id         BIGINT          NOT NULL,
    document_name   VARCHAR(255)    NOT NULL,
    document_type   VARCHAR(50)     NOT NULL DEFAULT 'OTHER',
    file_url        VARCHAR(500),
    file_mime_type  VARCHAR(100),
    file_size_bytes BIGINT,
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_farm_documents_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_farm_documents_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_farm_documents_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_farm_documents_valid_type
        CHECK (document_type IN ('TITLE_DEED', 'LEASE_AGREEMENT', 'CERTIFICATE',
                                 'GOVERNMENT_ID', 'INSURANCE', 'OTHER'))
);

CREATE INDEX IF NOT EXISTS idx_farm_documents_farm_id
    ON harvest_tracker.farm_documents(farm_id);

CREATE TRIGGER trg_farm_documents_updated_at
    BEFORE UPDATE ON harvest_tracker.farm_documents
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- farm_fruit_types
-- Junction: a farm can cultivate multiple fruit types.
-- Enables multi-fruit farms without schema changes.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.farm_fruit_types (
    id                  BIGSERIAL       PRIMARY KEY,
    farm_id             BIGINT          NOT NULL,
    fruit_type_id       BIGINT          NOT NULL,
    is_primary          BOOLEAN         NOT NULL DEFAULT FALSE,
    first_planted_date  DATE,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_farm_fruit_types_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_farm_fruit_types_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_farm_fruit_types_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_farm_fruit_types_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_farm_fruit_types_farm_id
    ON harvest_tracker.farm_fruit_types(farm_id);

CREATE INDEX IF NOT EXISTS idx_farm_fruit_types_fruit_type_id
    ON harvest_tracker.farm_fruit_types(fruit_type_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_farm_fruit_types_pair
    ON harvest_tracker.farm_fruit_types(farm_id, fruit_type_id)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_farm_fruit_types_updated_at
    BEFORE UPDATE ON harvest_tracker.farm_fruit_types
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- seasons
-- Named harvest time windows per farm and fruit type.
-- Example: Mango Season 2024 (Alphonso Farm A, Jan–May 2024)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.seasons (
    id              BIGSERIAL       PRIMARY KEY,
    farm_id         BIGINT          NOT NULL,
    fruit_type_id   BIGINT          NOT NULL,
    name            VARCHAR(150)    NOT NULL,
    year            INT             NOT NULL,
    start_date      DATE            NOT NULL,
    end_date        DATE            NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PLANNED',
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_seasons_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_seasons_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_seasons_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_seasons_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_seasons_valid_status
        CHECK (status IN ('PLANNED', 'ACTIVE', 'CLOSED')),
    CONSTRAINT ck_seasons_year_range
        CHECK (year BETWEEN 2000 AND 2100),
    CONSTRAINT ck_seasons_dates_valid
        CHECK (end_date > start_date)
);

CREATE INDEX IF NOT EXISTS idx_seasons_farm_id
    ON harvest_tracker.seasons(farm_id);

CREATE INDEX IF NOT EXISTS idx_seasons_fruit_type_id
    ON harvest_tracker.seasons(fruit_type_id);

CREATE INDEX IF NOT EXISTS idx_seasons_farm_fruit_year
    ON harvest_tracker.seasons(farm_id, fruit_type_id, year);

CREATE UNIQUE INDEX IF NOT EXISTS uq_seasons_farm_fruit_year_name
    ON harvest_tracker.seasons(farm_id, fruit_type_id, year, name)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_seasons_updated_at
    BEFORE UPDATE ON harvest_tracker.seasons
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 4: HARVEST MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- harvest_records
-- Core operational record. One row = one harvest batch from one farm.
-- Business rule: harvest_quantity > 0
-- Business rule: harvest_date <= CURRENT_DATE
-- Business rule: must have at least one worker (enforced by application layer)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.harvest_records (
    id                  BIGSERIAL       PRIMARY KEY,
    farm_id             BIGINT          NOT NULL,
    season_id           BIGINT          NOT NULL,
    fruit_type_id       BIGINT          NOT NULL,
    crop_variant_id     BIGINT,
    quantity_uom_id     BIGINT,
    supervisor_id       BIGINT,
    harvest_date        DATE            NOT NULL,
    harvest_quantity    NUMERIC(12,3)   NOT NULL,
    quality_grade       VARCHAR(10)     NOT NULL DEFAULT 'B',
    storage_location    VARCHAR(200),
    status              VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_harvest_records_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_harvest_records_seasons
        FOREIGN KEY (season_id) REFERENCES harvest_tracker.seasons(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_harvest_records_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_harvest_records_crop_variants
        FOREIGN KEY (crop_variant_id) REFERENCES harvest_tracker.crop_variants(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_records_quantity_uom
        FOREIGN KEY (quantity_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_records_supervisor
        FOREIGN KEY (supervisor_id) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_records_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_records_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_harvest_records_quantity_positive
        CHECK (harvest_quantity > 0),
    CONSTRAINT ck_harvest_records_date_not_future
        CHECK (harvest_date <= CURRENT_DATE),
    CONSTRAINT ck_harvest_records_valid_grade
        CHECK (quality_grade IN ('A', 'B', 'C', 'REJECT')),
    CONSTRAINT ck_harvest_records_valid_status
        CHECK (status IN ('DRAFT', 'CONFIRMED', 'STORED', 'SOLD'))
);

CREATE INDEX IF NOT EXISTS idx_harvest_records_farm_id
    ON harvest_tracker.harvest_records(farm_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_season_id
    ON harvest_tracker.harvest_records(season_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_fruit_type_id
    ON harvest_tracker.harvest_records(fruit_type_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_crop_variant_id
    ON harvest_tracker.harvest_records(crop_variant_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_supervisor_id
    ON harvest_tracker.harvest_records(supervisor_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_farm_season
    ON harvest_tracker.harvest_records(farm_id, season_id);

CREATE INDEX IF NOT EXISTS idx_harvest_records_farm_date
    ON harvest_tracker.harvest_records(farm_id, harvest_date);

CREATE INDEX IF NOT EXISTS idx_harvest_records_harvest_date
    ON harvest_tracker.harvest_records(harvest_date)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_harvest_records_updated_at
    BEFORE UPDATE ON harvest_tracker.harvest_records
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- harvest_quality_checks
-- Quality inspection records per harvest batch.
-- Multiple checks can be recorded for one harvest.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.harvest_quality_checks (
    id                  BIGSERIAL       PRIMARY KEY,
    harvest_record_id   BIGINT          NOT NULL,
    checked_by          BIGINT,
    check_datetime      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    quality_grade       VARCHAR(10)     NOT NULL,
    defect_percentage   NUMERIC(5,2),
    average_weight_grams NUMERIC(8,2),
    observations        TEXT,
    is_approved         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_harvest_quality_checks_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_harvest_quality_checks_checked_by
        FOREIGN KEY (checked_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_quality_checks_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_quality_checks_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_harvest_quality_checks_valid_grade
        CHECK (quality_grade IN ('A', 'B', 'C', 'REJECT')),
    CONSTRAINT ck_harvest_quality_checks_defect_pct
        CHECK (defect_percentage IS NULL OR (defect_percentage BETWEEN 0 AND 100)),
    CONSTRAINT ck_harvest_quality_checks_avg_weight_positive
        CHECK (average_weight_grams IS NULL OR average_weight_grams > 0)
);

CREATE INDEX IF NOT EXISTS idx_harvest_quality_checks_harvest_record_id
    ON harvest_tracker.harvest_quality_checks(harvest_record_id);

CREATE TRIGGER trg_harvest_quality_checks_updated_at
    BEFORE UPDATE ON harvest_tracker.harvest_quality_checks
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- harvest_workers
-- Many-to-many junction: which workers participated in each harvest.
-- Business rule: one worker cannot appear twice in the same harvest.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.harvest_workers (
    id                  BIGSERIAL       PRIMARY KEY,
    harvest_record_id   BIGINT          NOT NULL,
    worker_id           BIGINT          NOT NULL,
    role_in_harvest     VARCHAR(100),
    hours_worked        NUMERIC(4,2),
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_harvest_workers_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_harvest_workers_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_harvest_workers_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_harvest_workers_hours_valid
        CHECK (hours_worked IS NULL OR (hours_worked BETWEEN 0 AND 24))
);

CREATE INDEX IF NOT EXISTS idx_harvest_workers_harvest_record_id
    ON harvest_tracker.harvest_workers(harvest_record_id);

CREATE INDEX IF NOT EXISTS idx_harvest_workers_worker_id
    ON harvest_tracker.harvest_workers(worker_id);

-- Worker cannot appear twice in same harvest (prevents double-counting)
CREATE UNIQUE INDEX IF NOT EXISTS uq_harvest_workers_harvest_worker
    ON harvest_tracker.harvest_workers(harvest_record_id, worker_id)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_harvest_workers_updated_at
    BEFORE UPDATE ON harvest_tracker.harvest_workers
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 5: WORKER MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- workers
-- Field labourers. Separate from system users.
-- A worker may not have a system login account.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.workers (
    id              BIGSERIAL       PRIMARY KEY,
    farm_id         BIGINT          NOT NULL,
    worker_type_id  BIGINT,
    wage_uom_id     BIGINT,
    name            VARCHAR(200)    NOT NULL,
    phone           VARCHAR(20),
    id_card_number  VARCHAR(50),
    daily_wage_rate NUMERIC(10,2)   NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    joining_date    DATE,
    address         TEXT,
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_workers_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_workers_worker_types
        FOREIGN KEY (worker_type_id) REFERENCES harvest_tracker.worker_types(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_workers_wage_uom
        FOREIGN KEY (wage_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_workers_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_workers_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_workers_valid_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_workers_wage_rate_positive
        CHECK (daily_wage_rate > 0),
    CONSTRAINT ck_workers_name_not_empty
        CHECK (TRIM(name) <> '')
);

-- Enforce FK from harvest_workers.worker_id now that workers table exists
ALTER TABLE harvest_tracker.harvest_workers
    ADD CONSTRAINT fk_harvest_workers_workers
        FOREIGN KEY (worker_id) REFERENCES harvest_tracker.workers(id)
        ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_workers_farm_id
    ON harvest_tracker.workers(farm_id);

CREATE INDEX IF NOT EXISTS idx_workers_worker_type_id
    ON harvest_tracker.workers(worker_type_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_workers_phone_active
    ON harvest_tracker.workers(phone)
    WHERE deleted_at IS NULL AND phone IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_workers_active_farm
    ON harvest_tracker.workers(farm_id)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_workers_name_trgm
    ON harvest_tracker.workers USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_workers_updated_at
    BEFORE UPDATE ON harvest_tracker.workers
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- worker_attendance
-- Daily attendance records per worker.
-- Business rule: one record per worker per date.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.worker_attendance (
    id                  BIGSERIAL       PRIMARY KEY,
    worker_id           BIGINT          NOT NULL,
    harvest_record_id   BIGINT,
    attendance_date     DATE            NOT NULL,
    is_present          BOOLEAN         NOT NULL,
    hours_worked        NUMERIC(4,2),
    remarks             TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_worker_attendance_workers
        FOREIGN KEY (worker_id) REFERENCES harvest_tracker.workers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_worker_attendance_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_worker_attendance_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_worker_attendance_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_worker_attendance_hours_valid
        CHECK (hours_worked IS NULL OR (hours_worked BETWEEN 0 AND 24))
);

CREATE INDEX IF NOT EXISTS idx_worker_attendance_worker_id
    ON harvest_tracker.worker_attendance(worker_id);

CREATE INDEX IF NOT EXISTS idx_worker_attendance_harvest_record_id
    ON harvest_tracker.worker_attendance(harvest_record_id);

CREATE INDEX IF NOT EXISTS idx_worker_attendance_worker_date
    ON harvest_tracker.worker_attendance(worker_id, attendance_date);

CREATE INDEX IF NOT EXISTS idx_worker_attendance_date
    ON harvest_tracker.worker_attendance(attendance_date)
    WHERE deleted_at IS NULL;

-- One attendance record per worker per date
CREATE UNIQUE INDEX IF NOT EXISTS uq_worker_attendance_worker_date
    ON harvest_tracker.worker_attendance(worker_id, attendance_date)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_worker_attendance_updated_at
    BEFORE UPDATE ON harvest_tracker.worker_attendance
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- worker_payments
-- Payroll settlement records per worker per period.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.worker_payments (
    id                  BIGSERIAL       PRIMARY KEY,
    worker_id           BIGINT          NOT NULL,
    payment_method_id   BIGINT,
    period_start        DATE            NOT NULL,
    period_end          DATE            NOT NULL,
    total_days_worked   INT             NOT NULL DEFAULT 0,
    daily_wage_rate     NUMERIC(10,2)   NOT NULL,
    amount              NUMERIC(12,2)   NOT NULL,
    payment_status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    paid_date           DATE,
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_worker_payments_workers
        FOREIGN KEY (worker_id) REFERENCES harvest_tracker.workers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_worker_payments_payment_methods
        FOREIGN KEY (payment_method_id) REFERENCES harvest_tracker.payment_methods(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_worker_payments_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_worker_payments_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_worker_payments_valid_status
        CHECK (payment_status IN ('PENDING', 'PAID', 'CANCELLED')),
    CONSTRAINT ck_worker_payments_amount_positive
        CHECK (amount > 0),
    CONSTRAINT ck_worker_payments_period_valid
        CHECK (period_end >= period_start),
    CONSTRAINT ck_worker_payments_daily_rate_positive
        CHECK (daily_wage_rate > 0),
    CONSTRAINT ck_worker_payments_days_non_negative
        CHECK (total_days_worked >= 0)
);

CREATE INDEX IF NOT EXISTS idx_worker_payments_worker_id
    ON harvest_tracker.worker_payments(worker_id);

CREATE INDEX IF NOT EXISTS idx_worker_payments_payment_method_id
    ON harvest_tracker.worker_payments(payment_method_id);

CREATE INDEX IF NOT EXISTS idx_worker_payments_worker_period
    ON harvest_tracker.worker_payments(worker_id, period_start, period_end);

CREATE INDEX IF NOT EXISTS idx_worker_payments_pending
    ON harvest_tracker.worker_payments(worker_id)
    WHERE payment_status = 'PENDING' AND deleted_at IS NULL;

CREATE TRIGGER trg_worker_payments_updated_at
    BEFORE UPDATE ON harvest_tracker.worker_payments
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 6: SALES MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- customers
-- Buyer profiles. Separate from system users.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.customers (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    phone           VARCHAR(20),
    email           VARCHAR(255),
    address         TEXT,
    customer_type   VARCHAR(30)     NOT NULL DEFAULT 'RETAIL',
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_customers_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_customers_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_customers_valid_type
        CHECK (customer_type IN ('RETAIL', 'WHOLESALE', 'EXPORT', 'CORPORATE')),
    CONSTRAINT ck_customers_valid_status
        CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_customers_name_not_empty
        CHECK (TRIM(name) <> '')
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_customers_phone_active
    ON harvest_tracker.customers(phone)
    WHERE deleted_at IS NULL AND phone IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_customers_status
    ON harvest_tracker.customers(status)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_customers_name_trgm
    ON harvest_tracker.customers USING gin(name gin_trgm_ops)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_customers_updated_at
    BEFORE UPDATE ON harvest_tracker.customers
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- orders
-- Sales order header. State machine: DRAFT → CONFIRMED → DISPATCHED →
--   DELIVERED → INVOICED → PAID → CANCELLED
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.orders (
    id              BIGSERIAL       PRIMARY KEY,
    customer_id     BIGINT          NOT NULL,
    order_date      DATE            NOT NULL DEFAULT CURRENT_DATE,
    order_status    VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    total_amount    NUMERIC(15,2)   NOT NULL DEFAULT 0,
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_orders_customers
        FOREIGN KEY (customer_id) REFERENCES harvest_tracker.customers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_orders_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_orders_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_orders_valid_status
        CHECK (order_status IN ('DRAFT', 'CONFIRMED', 'DISPATCHED', 'DELIVERED',
                                'INVOICED', 'PAID', 'CANCELLED')),
    CONSTRAINT ck_orders_total_non_negative
        CHECK (total_amount >= 0),
    CONSTRAINT ck_orders_date_not_future
        CHECK (order_date <= CURRENT_DATE)
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id
    ON harvest_tracker.orders(customer_id);

CREATE INDEX IF NOT EXISTS idx_orders_order_date
    ON harvest_tracker.orders(order_date)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_orders_customer_status
    ON harvest_tracker.orders(customer_id, order_status);

CREATE INDEX IF NOT EXISTS idx_orders_active_status
    ON harvest_tracker.orders(order_status)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_orders_updated_at
    BEFORE UPDATE ON harvest_tracker.orders
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- order_items
-- Line items within a sales order. Each item references a harvest batch.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.order_items (
    id                  BIGSERIAL       PRIMARY KEY,
    order_id            BIGINT          NOT NULL,
    harvest_record_id   BIGINT,
    fruit_type_id       BIGINT,
    crop_variant_id     BIGINT,
    quantity_uom_id     BIGINT,
    quantity            NUMERIC(12,3)   NOT NULL,
    unit_price          NUMERIC(12,2)   NOT NULL,
    line_total          NUMERIC(15,2)   NOT NULL,
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_order_items_orders
        FOREIGN KEY (order_id) REFERENCES harvest_tracker.orders(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_order_items_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_order_items_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_order_items_crop_variants
        FOREIGN KEY (crop_variant_id) REFERENCES harvest_tracker.crop_variants(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_order_items_quantity_uom
        FOREIGN KEY (quantity_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_order_items_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_order_items_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_order_items_quantity_positive
        CHECK (quantity > 0),
    CONSTRAINT ck_order_items_price_non_negative
        CHECK (unit_price >= 0),
    CONSTRAINT ck_order_items_line_total_non_negative
        CHECK (line_total >= 0)
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id
    ON harvest_tracker.order_items(order_id);

CREATE INDEX IF NOT EXISTS idx_order_items_harvest_record_id
    ON harvest_tracker.order_items(harvest_record_id);

CREATE INDEX IF NOT EXISTS idx_order_items_fruit_type_id
    ON harvest_tracker.order_items(fruit_type_id);

CREATE TRIGGER trg_order_items_updated_at
    BEFORE UPDATE ON harvest_tracker.order_items
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- sales
-- Actual sale event confirming dispatch/delivery from a farm.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.sales (
    id                      BIGSERIAL       PRIMARY KEY,
    order_id                BIGINT          NOT NULL,
    farm_id                 BIGINT          NOT NULL,
    quantity_uom_id         BIGINT,
    sale_date               DATE            NOT NULL DEFAULT CURRENT_DATE,
    quantity_sold           NUMERIC(12,3)   NOT NULL,
    sale_price_per_unit     NUMERIC(12,2)   NOT NULL,
    total_sale_amount       NUMERIC(15,2)   NOT NULL,
    notes                   TEXT,
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              BIGINT,
    updated_by              BIGINT,
    deleted_at              TIMESTAMPTZ,

    CONSTRAINT fk_sales_orders
        FOREIGN KEY (order_id) REFERENCES harvest_tracker.orders(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_sales_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_sales_quantity_uom
        FOREIGN KEY (quantity_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sales_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_sales_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_sales_quantity_positive
        CHECK (quantity_sold > 0),
    CONSTRAINT ck_sales_price_non_negative
        CHECK (sale_price_per_unit >= 0),
    CONSTRAINT ck_sales_total_non_negative
        CHECK (total_sale_amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_sales_order_id
    ON harvest_tracker.sales(order_id);

CREATE INDEX IF NOT EXISTS idx_sales_farm_id
    ON harvest_tracker.sales(farm_id);

CREATE INDEX IF NOT EXISTS idx_sales_sale_date
    ON harvest_tracker.sales(sale_date)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_sales_updated_at
    BEFORE UPDATE ON harvest_tracker.sales
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- transport_records
-- Logistics details per order (vehicle, driver, transport cost).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.transport_records (
    id                  BIGSERIAL       PRIMARY KEY,
    order_id            BIGINT          NOT NULL,
    vehicle_number      VARCHAR(20),
    driver_name         VARCHAR(200),
    driver_phone        VARCHAR(20),
    transporter_name    VARCHAR(200),
    transport_cost      NUMERIC(12,2),
    dispatch_date       DATE,
    delivery_date       DATE,
    delivery_status     VARCHAR(30)     NOT NULL DEFAULT 'PENDING',
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_transport_records_orders
        FOREIGN KEY (order_id) REFERENCES harvest_tracker.orders(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_transport_records_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_transport_records_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_transport_records_valid_delivery_status
        CHECK (delivery_status IN ('PENDING', 'IN_TRANSIT', 'DELIVERED', 'FAILED', 'RETURNED')),
    CONSTRAINT ck_transport_records_cost_non_negative
        CHECK (transport_cost IS NULL OR transport_cost >= 0),
    CONSTRAINT ck_transport_records_delivery_after_dispatch
        CHECK (delivery_date IS NULL OR dispatch_date IS NULL OR delivery_date >= dispatch_date)
);

CREATE INDEX IF NOT EXISTS idx_transport_records_order_id
    ON harvest_tracker.transport_records(order_id);

CREATE TRIGGER trg_transport_records_updated_at
    BEFORE UPDATE ON harvest_tracker.transport_records
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- invoices
-- One invoice per order. 1:1 relationship enforced by unique constraint.
-- paid_amount is updated incrementally as payments are added.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.invoices (
    id              BIGSERIAL       PRIMARY KEY,
    order_id        BIGINT          NOT NULL,
    customer_id     BIGINT          NOT NULL,
    invoice_number  VARCHAR(50)     NOT NULL,
    issued_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    due_date        DATE            NOT NULL,
    total_amount    NUMERIC(15,2)   NOT NULL,
    paid_amount     NUMERIC(15,2)   NOT NULL DEFAULT 0,
    invoice_status  VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    notes           TEXT,
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_invoices_orders
        FOREIGN KEY (order_id) REFERENCES harvest_tracker.orders(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_invoices_customers
        FOREIGN KEY (customer_id) REFERENCES harvest_tracker.customers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_invoices_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_invoices_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_invoices_valid_status
        CHECK (invoice_status IN ('DRAFT', 'ISSUED', 'PARTIALLY_PAID', 'PAID',
                                  'OVERDUE', 'CANCELLED')),
    CONSTRAINT ck_invoices_total_positive
        CHECK (total_amount > 0),
    CONSTRAINT ck_invoices_paid_non_negative
        CHECK (paid_amount >= 0),
    CONSTRAINT ck_invoices_paid_not_exceed_total
        CHECK (paid_amount <= total_amount),
    CONSTRAINT ck_invoices_due_date_valid
        CHECK (due_date >= issued_at::date)
);

-- 1:1 relationship: one invoice per order
CREATE UNIQUE INDEX IF NOT EXISTS uq_invoices_order_id
    ON harvest_tracker.invoices(order_id)
    WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_invoices_invoice_number
    ON harvest_tracker.invoices(invoice_number)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_invoices_order_id
    ON harvest_tracker.invoices(order_id);

CREATE INDEX IF NOT EXISTS idx_invoices_customer_id
    ON harvest_tracker.invoices(customer_id);

CREATE INDEX IF NOT EXISTS idx_invoices_customer_status
    ON harvest_tracker.invoices(customer_id, invoice_status);

-- Open invoices for dashboard
CREATE INDEX IF NOT EXISTS idx_invoices_open
    ON harvest_tracker.invoices(invoice_status, due_date)
    WHERE deleted_at IS NULL AND invoice_status NOT IN ('PAID', 'CANCELLED');

CREATE TRIGGER trg_invoices_updated_at
    BEFORE UPDATE ON harvest_tracker.invoices
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- -----------------------------------------------------------------------------
-- payments
-- Individual payment transactions against an invoice.
-- Multiple payments per invoice are allowed (partial payment support).
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.payments (
    id                  BIGSERIAL       PRIMARY KEY,
    invoice_id          BIGINT          NOT NULL,
    payment_method_id   BIGINT,
    payment_date        DATE            NOT NULL DEFAULT CURRENT_DATE,
    amount              NUMERIC(12,2)   NOT NULL,
    payment_status      VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    reference_number    VARCHAR(100),
    notes               TEXT,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_payments_invoices
        FOREIGN KEY (invoice_id) REFERENCES harvest_tracker.invoices(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_payments_payment_methods
        FOREIGN KEY (payment_method_id) REFERENCES harvest_tracker.payment_methods(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_payments_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_payments_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_payments_valid_status
        CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT ck_payments_amount_positive
        CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_payments_invoice_id
    ON harvest_tracker.payments(invoice_id);

CREATE INDEX IF NOT EXISTS idx_payments_payment_method_id
    ON harvest_tracker.payments(payment_method_id);

CREATE INDEX IF NOT EXISTS idx_payments_invoice_status
    ON harvest_tracker.payments(invoice_id, payment_status);

CREATE INDEX IF NOT EXISTS idx_payments_payment_date
    ON harvest_tracker.payments(payment_date)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_payments_updated_at
    BEFORE UPDATE ON harvest_tracker.payments
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 7: EXPENSE MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- expenses
-- Operational costs at farm or harvest level.
-- General farm expenses: land rent, irrigation.
-- Harvest-specific: packaging, cold storage.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.expenses (
    id                      BIGSERIAL       PRIMARY KEY,
    farm_id                 BIGINT          NOT NULL,
    harvest_record_id       BIGINT,
    expense_category_id     BIGINT          NOT NULL,
    payment_method_id       BIGINT,
    expense_date            DATE            NOT NULL DEFAULT CURRENT_DATE,
    amount                  NUMERIC(12,2)   NOT NULL,
    status                  VARCHAR(20)     NOT NULL DEFAULT 'RECORDED',
    description             TEXT,
    notes                   TEXT,
    is_active               BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by              BIGINT,
    updated_by              BIGINT,
    deleted_at              TIMESTAMPTZ,

    CONSTRAINT fk_expenses_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_expenses_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_expenses_expense_categories
        FOREIGN KEY (expense_category_id) REFERENCES harvest_tracker.expense_categories(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_expenses_payment_methods
        FOREIGN KEY (payment_method_id) REFERENCES harvest_tracker.payment_methods(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_expenses_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_expenses_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_expenses_amount_positive
        CHECK (amount > 0),
    CONSTRAINT ck_expenses_date_not_future
        CHECK (expense_date <= CURRENT_DATE),
    CONSTRAINT ck_expenses_valid_status
        CHECK (status IN ('RECORDED', 'APPROVED', 'REJECTED'))
);

CREATE INDEX IF NOT EXISTS idx_expenses_farm_id
    ON harvest_tracker.expenses(farm_id);

CREATE INDEX IF NOT EXISTS idx_expenses_harvest_record_id
    ON harvest_tracker.expenses(harvest_record_id);

CREATE INDEX IF NOT EXISTS idx_expenses_expense_category_id
    ON harvest_tracker.expenses(expense_category_id);

CREATE INDEX IF NOT EXISTS idx_expenses_payment_method_id
    ON harvest_tracker.expenses(payment_method_id);

CREATE INDEX IF NOT EXISTS idx_expenses_farm_date
    ON harvest_tracker.expenses(farm_id, expense_date);

CREATE INDEX IF NOT EXISTS idx_expenses_expense_date
    ON harvest_tracker.expenses(expense_date)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_expenses_updated_at
    BEFORE UPDATE ON harvest_tracker.expenses
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 8: INVENTORY MODULE
-- =============================================================================

-- -----------------------------------------------------------------------------
-- inventory_batches
-- Tracks available fruit quantity from harvest through sale.
-- available_quantity decreases as order_items reference this batch.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.inventory_batches (
    id                  BIGSERIAL       PRIMARY KEY,
    harvest_record_id   BIGINT          NOT NULL,
    farm_id             BIGINT          NOT NULL,
    fruit_type_id       BIGINT          NOT NULL,
    quantity_uom_id     BIGINT,
    batch_code          VARCHAR(50),
    total_quantity      NUMERIC(12,3)   NOT NULL,
    available_quantity  NUMERIC(12,3)   NOT NULL,
    storage_location    VARCHAR(200),
    best_before_date    DATE,
    status              VARCHAR(20)     NOT NULL DEFAULT 'IN_STOCK',
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_inventory_batches_harvest_records
        FOREIGN KEY (harvest_record_id) REFERENCES harvest_tracker.harvest_records(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_batches_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_batches_fruit_types
        FOREIGN KEY (fruit_type_id) REFERENCES harvest_tracker.fruit_types(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_inventory_batches_quantity_uom
        FOREIGN KEY (quantity_uom_id) REFERENCES harvest_tracker.units_of_measure(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_inventory_batches_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_inventory_batches_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_inventory_batches_total_positive
        CHECK (total_quantity > 0),
    CONSTRAINT ck_inventory_batches_quantity_non_negative
        CHECK (available_quantity >= 0),
    CONSTRAINT ck_inventory_batches_available_le_total
        CHECK (available_quantity <= total_quantity),
    CONSTRAINT ck_inventory_batches_valid_status
        CHECK (status IN ('IN_STOCK', 'PARTIAL', 'DEPLETED', 'EXPIRED'))
);

CREATE INDEX IF NOT EXISTS idx_inventory_batches_harvest_record_id
    ON harvest_tracker.inventory_batches(harvest_record_id);

CREATE INDEX IF NOT EXISTS idx_inventory_batches_farm_id
    ON harvest_tracker.inventory_batches(farm_id);

CREATE INDEX IF NOT EXISTS idx_inventory_batches_fruit_type_id
    ON harvest_tracker.inventory_batches(fruit_type_id);

CREATE INDEX IF NOT EXISTS idx_inventory_batches_status
    ON harvest_tracker.inventory_batches(status)
    WHERE deleted_at IS NULL;

CREATE TRIGGER trg_inventory_batches_updated_at
    BEFORE UPDATE ON harvest_tracker.inventory_batches
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- MODULE 9: REPORTING
-- =============================================================================

-- -----------------------------------------------------------------------------
-- report_snapshots
-- Stores closed-period report results as JSONB.
-- Used for: historical seasonal summaries, exported reports, closed-period data.
-- Live dashboard queries run directly against transactional tables.
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS harvest_tracker.report_snapshots (
    id              BIGSERIAL       PRIMARY KEY,
    farm_id         BIGINT,
    season_id       BIGINT,
    report_type     VARCHAR(50)     NOT NULL,
    report_period   VARCHAR(50)     NOT NULL,
    period_start    DATE            NOT NULL,
    period_end      DATE            NOT NULL,
    report_data     JSONB           NOT NULL DEFAULT '{}',
    status          VARCHAR(20)     NOT NULL DEFAULT 'GENERATED',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_report_snapshots_farms
        FOREIGN KEY (farm_id) REFERENCES harvest_tracker.farms(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_report_snapshots_seasons
        FOREIGN KEY (season_id) REFERENCES harvest_tracker.seasons(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_report_snapshots_created_by
        FOREIGN KEY (created_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT fk_report_snapshots_updated_by
        FOREIGN KEY (updated_by) REFERENCES harvest_tracker.users(id)
        ON DELETE SET NULL,
    CONSTRAINT ck_report_snapshots_valid_type
        CHECK (report_type IN ('SEASON', 'HARVEST', 'FINANCIAL', 'WORKER', 'SALES', 'CUSTOM')),
    CONSTRAINT ck_report_snapshots_valid_period
        CHECK (report_period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'SEASONAL', 'ANNUAL', 'CUSTOM')),
    CONSTRAINT ck_report_snapshots_valid_status
        CHECK (status IN ('GENERATED', 'FINAL', 'ARCHIVED')),
    CONSTRAINT ck_report_snapshots_period_valid
        CHECK (period_end >= period_start)
);

CREATE INDEX IF NOT EXISTS idx_report_snapshots_farm_id
    ON harvest_tracker.report_snapshots(farm_id);

CREATE INDEX IF NOT EXISTS idx_report_snapshots_season_id
    ON harvest_tracker.report_snapshots(season_id);

CREATE INDEX IF NOT EXISTS idx_report_snapshots_type_period
    ON harvest_tracker.report_snapshots(report_type, report_period);

CREATE TRIGGER trg_report_snapshots_updated_at
    BEFORE UPDATE ON harvest_tracker.report_snapshots
    FOR EACH ROW EXECUTE FUNCTION harvest_tracker.set_updated_at();

-- =============================================================================
-- REFERENCE DATA SEEDS
-- Master data only. No production data. No users.
-- =============================================================================

-- -----------------------------------------------------------------------------
-- Fruit Types
-- Initial fruit types. New fruits can be added by INSERT only.
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.fruit_types (name, code, description, season_type, sort_order)
VALUES
    ('Mango',        'MANGO',        'Tropical stone fruit',            'SUMMER',    1),
    ('Banana',       'BANANA',       'Tropical herbaceous plant fruit', 'YEAR_ROUND', 2),
    ('Orange',       'ORANGE',       'Citrus fruit',                    'WINTER',    3),
    ('Apple',        'APPLE',        'Pomaceous fruit',                 'WINTER',    4),
    ('Grapes',       'GRAPES',       'Berry fruit grown on vines',      'SUMMER',    5),
    ('Pomegranate',  'POMEGRANATE',  'Thick-skinned berry fruit',       'SUMMER',    6);

-- -----------------------------------------------------------------------------
-- Crop Variants (Mango varieties — initial set)
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.crop_variants (fruit_type_id, name, code, description)
SELECT ft.id, v.name, v.code, v.description
FROM harvest_tracker.fruit_types ft
CROSS JOIN (VALUES
    ('Alphonso',    'ALPHONSO',    'Premium Alphonso variety — Devgad, Ratnagiri'),
    ('Kesar',       'KESAR',       'Kesar variety — Junagadh, Gujarat'),
    ('Dasheri',     'DASHERI',     'Dasheri variety — Malihabad, UP'),
    ('Langra',      'LANGRA',      'Langra variety — Varanasi'),
    ('Totapuri',    'TOTAPURI',    'Totapuri variety — South India'),
    ('Banganapalli', 'BANGANAPALLI', 'Banganapalli variety — Andhra Pradesh')
) AS v(name, code, description)
WHERE ft.code = 'MANGO';

-- Banana variants
INSERT INTO harvest_tracker.crop_variants (fruit_type_id, name, code, description)
SELECT ft.id, v.name, v.code, v.description
FROM harvest_tracker.fruit_types ft
CROSS JOIN (VALUES
    ('Cavendish',   'CAVENDISH',   'Most common commercial variety'),
    ('Robusta',     'ROBUSTA',     'Robusta variety — India'),
    ('Nendran',     'NENDRAN',     'Nendran variety — Kerala')
) AS v(name, code, description)
WHERE ft.code = 'BANANA';

-- -----------------------------------------------------------------------------
-- Units of Measure
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.units_of_measure (name, code, measure_type, sort_order)
VALUES
    -- Weight
    ('Kilogram',    'KG',       'WEIGHT', 1),
    ('Metric Ton',  'TON',      'WEIGHT', 2),
    ('Gram',        'GM',       'WEIGHT', 3),
    ('Quintal',     'QTL',      'WEIGHT', 4),
    -- Area
    ('Acre',        'ACRE',     'AREA',   10),
    ('Hectare',     'HA',       'AREA',   11),
    ('Guntha',      'GUNTHA',   'AREA',   12),
    -- Count
    ('Piece',       'PCS',      'COUNT',  20),
    ('Box',         'BOX',      'COUNT',  21),
    ('Crate',       'CRATE',    'COUNT',  22),
    -- Time
    ('Per Day',     'PER_DAY',  'TIME',   30),
    ('Per Hour',    'PER_HOUR', 'TIME',   31);

-- -----------------------------------------------------------------------------
-- Worker Types
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.worker_types (name, code, description, sort_order)
VALUES
    ('Daily Labourer',  'DAILY',      'Hired on a daily basis',       1),
    ('Seasonal Worker', 'SEASONAL',   'Hired for the harvest season', 2),
    ('Contract Worker', 'CONTRACT',   'Contracted for specific work', 3),
    ('Supervisor',      'SUPERVISOR', 'Oversees field operations',    4),
    ('Permanent Staff', 'PERMANENT',  'Full-time permanent employee', 5);

-- -----------------------------------------------------------------------------
-- Payment Methods
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.payment_methods (name, code, sort_order)
VALUES
    ('Cash',            'CASH',          1),
    ('Bank Transfer',   'BANK_TRANSFER', 2),
    ('UPI',             'UPI',           3),
    ('Cheque',          'CHEQUE',        4),
    ('NEFT',            'NEFT',          5),
    ('RTGS',            'RTGS',          6),
    ('IMPS',            'IMPS',          7);

-- -----------------------------------------------------------------------------
-- Expense Categories
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.expense_categories (name, code, description, sort_order)
VALUES
    ('Labour',              'LABOUR',       'Field worker wages and labour costs',          1),
    ('Transport',           'TRANSPORT',    'Vehicle hire and transport costs',             2),
    ('Packaging',           'PACKAGING',    'Boxes, crates, packaging materials',          3),
    ('Irrigation',          'IRRIGATION',   'Water supply and irrigation costs',            4),
    ('Land Rent / Lease',   'RENT',         'Land lease or rental payments',               5),
    ('Fertilizer',          'FERTILIZER',   'Fertilizers and soil amendments',             6),
    ('Pesticides',          'PESTICIDES',   'Pesticides and crop protection chemicals',    7),
    ('Equipment',           'EQUIPMENT',    'Tools, machinery, and equipment costs',       8),
    ('Cold Storage',        'COLD_STORAGE', 'Refrigeration and cold storage charges',     9),
    ('Other',               'OTHER',        'Miscellaneous operational expenses',          99);

-- -----------------------------------------------------------------------------
-- Roles
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.roles (name, code, description, sort_order)
VALUES
    ('Administrator',   'ADMIN',        'Full system access — manages users, roles, settings',    1),
    ('Farm Owner',      'FARM_OWNER',   'Manages owned farms, reviews all reports',               2),
    ('Manager',         'MANAGER',      'Manages operations, workers, and sales',                 3),
    ('Supervisor',      'SUPERVISOR',   'Records harvests and manages attendance',                4),
    ('Accountant',      'ACCOUNTANT',   'Manages invoices, payments, and financial records',      5);

-- -----------------------------------------------------------------------------
-- Permissions
-- -----------------------------------------------------------------------------
INSERT INTO harvest_tracker.permissions (name, code, module, description)
VALUES
    -- Auth
    ('Manage Users',            'AUTH_USER_MANAGE',         'AUTH',      'Create, edit, deactivate users'),
    ('Manage Roles',            'AUTH_ROLE_MANAGE',         'AUTH',      'Assign and revoke roles'),
    -- Farm
    ('View Farms',              'FARM_VIEW',                'FARM',      'List and view farm details'),
    ('Create Farms',            'FARM_CREATE',              'FARM',      'Create new farm records'),
    ('Edit Farms',              'FARM_EDIT',                'FARM',      'Update farm details'),
    ('Delete Farms',            'FARM_DELETE',              'FARM',      'Soft-delete farm records'),
    ('Manage Farm Documents',   'FARM_DOCUMENT_MANAGE',     'FARM',      'Upload and manage farm documents'),
    -- Harvest
    ('View Harvests',           'HARVEST_VIEW',             'HARVEST',   'List and view harvest records'),
    ('Create Harvests',         'HARVEST_CREATE',           'HARVEST',   'Record new harvest batches'),
    ('Edit Harvests',           'HARVEST_EDIT',             'HARVEST',   'Update harvest data'),
    ('Confirm Harvests',        'HARVEST_CONFIRM',          'HARVEST',   'Confirm and close harvest records'),
    -- Worker
    ('View Workers',            'WORKER_VIEW',              'WORKER',    'List and view worker profiles'),
    ('Create Workers',          'WORKER_CREATE',            'WORKER',    'Add new worker records'),
    ('Edit Workers',            'WORKER_EDIT',              'WORKER',    'Update worker information'),
    ('Record Attendance',       'WORKER_ATTENDANCE_RECORD', 'WORKER',    'Record daily attendance'),
    ('Manage Payments',         'WORKER_PAYMENT_MANAGE',    'WORKER',    'Create and update worker payments'),
    -- Sales
    ('View Customers',          'SALES_CUSTOMER_VIEW',      'SALES',     'List and view customers'),
    ('Manage Customers',        'SALES_CUSTOMER_MANAGE',    'SALES',     'Create and update customers'),
    ('View Orders',             'SALES_ORDER_VIEW',         'SALES',     'List and view orders'),
    ('Manage Orders',           'SALES_ORDER_MANAGE',       'SALES',     'Create and update orders'),
    ('View Invoices',           'SALES_INVOICE_VIEW',       'SALES',     'List and view invoices'),
    ('Manage Invoices',         'SALES_INVOICE_MANAGE',     'SALES',     'Create and update invoices'),
    ('Record Payments',         'SALES_PAYMENT_RECORD',     'SALES',     'Record customer payments'),
    -- Expense
    ('View Expenses',           'EXPENSE_VIEW',             'EXPENSE',   'List and view expenses'),
    ('Record Expenses',         'EXPENSE_RECORD',           'EXPENSE',   'Record new expenses'),
    ('Approve Expenses',        'EXPENSE_APPROVE',          'EXPENSE',   'Approve or reject expense records'),
    -- Reports
    ('View Reports',            'REPORT_VIEW',              'REPORT',    'Access reports and dashboards'),
    ('Generate Reports',        'REPORT_GENERATE',          'REPORT',    'Generate and export reports'),
    -- Admin
    ('System Configuration',    'ADMIN_CONFIG',             'ADMIN',     'Manage system settings and reference data');

-- =============================================================================
-- END OF MIGRATION V1__initial_schema.sql
-- =============================================================================
-- Tables created : 31
-- Indexes created: ~65 (FK + composite + partial + unique + text)
-- Triggers created: 31 (one per table for updated_at)
-- Reference rows : fruit_types(6), crop_variants(9), uom(12), worker_types(5),
--                  payment_methods(7), expense_categories(10), roles(5),
--                  permissions(29)
-- =============================================================================
