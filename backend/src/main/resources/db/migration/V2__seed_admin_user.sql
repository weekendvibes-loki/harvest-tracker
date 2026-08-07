-- V2__seed_admin_user.sql
-- Seed initial System Administrator account and grant ADMIN role permissions

-- 1. Insert System Admin User (Password: Admin@123)
-- BCrypt Hash: $2a$10$ZDXARz3tMSS.cKcCqzJlo.AGITZCU8A5O0yHlLT/TQ60qejBsHGRu
INSERT INTO harvest_tracker.users (
    email,
    password_hash,
    name,
    phone,
    role_id,
    status,
    is_active
)
SELECT
    'admin@harvesttracker.local',
    '$2a$10$ZDXARz3tMSS.cKcCqzJlo.AGITZCU8A5O0yHlLT/TQ60qejBsHGRu',
    'System Administrator',
    '+10000000000',
    r.id,
    'ACTIVE',
    true
FROM harvest_tracker.roles r
WHERE r.code = 'ADMIN'
AND NOT EXISTS (
    SELECT 1 FROM harvest_tracker.users WHERE email = 'admin@harvesttracker.local'
);

-- 2. Ensure default permissions exist for core auth
INSERT INTO harvest_tracker.permissions (code, name, module, description)
VALUES
    ('AUTH_USER_MANAGE', 'Manage Users', 'AUTH', 'Create, update, and manage user accounts'),
    ('AUTH_ROLE_MANAGE', 'Manage Roles', 'AUTH', 'Manage roles and permission assignments')
ON CONFLICT (code) DO NOTHING;

-- 3. Link permissions to ADMIN role if not already assigned
INSERT INTO harvest_tracker.role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM harvest_tracker.roles r
CROSS JOIN harvest_tracker.permissions p
WHERE r.code = 'ADMIN'
AND p.code IN ('AUTH_USER_MANAGE', 'AUTH_ROLE_MANAGE')
AND NOT EXISTS (
    SELECT 1 FROM harvest_tracker.role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id AND rp.deleted_at IS NULL
);
