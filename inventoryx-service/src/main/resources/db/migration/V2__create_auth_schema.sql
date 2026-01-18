-- ============================================================================
-- Flyway Script: V2__create_auth_schema.sql
-- Description: Creates tables for Authentication & Authorization system
-- ============================================================================

-- 1. Create permissions table
CREATE TABLE inventory.permissions (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- e.g., 'STOCK_READ', 'USER_WRITE'
    description VARCHAR(255),
    resource VARCHAR(50) NOT NULL,    -- e.g., 'STOCK', 'USER'
    action VARCHAR(50) NOT NULL,      -- e.g., 'READ', 'WRITE', 'DELETE'
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create roles table
CREATE TABLE inventory.roles (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE, -- e.g., 'ADMIN', 'STAFF'
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create role_permissions join table
CREATE TABLE inventory.role_permissions (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES inventory.roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES inventory.permissions(id) ON DELETE CASCADE
);

-- 4. Create users table
CREATE TABLE inventory.users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    failed_attempts INTEGER DEFAULT 0,
    last_login_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Create user_roles join table
CREATE TABLE inventory.user_roles (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(36), -- user_id of who assigned the role
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES inventory.users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES inventory.roles(id) ON DELETE CASCADE
);

-- 6. Create refresh_tokens table (for JWT)
CREATE TABLE inventory.refresh_tokens (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES inventory.users(id) ON DELETE CASCADE
);

-- 7. Create audit_logs table
CREATE TABLE inventory.audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36), -- Nullable for system actions or unauthenticated attempts
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(50),
    resource_id VARCHAR(36),
    details TEXT, -- JSON payload of changes or arguments
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    status VARCHAR(20), -- SUCCESS, FAILURE
    performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Seed Initial Data
-- ============================================================================

-- A. Standard Permissions
INSERT INTO inventory.permissions (id, name, description, resource, action) VALUES
    ('perm-stock-read', 'STOCK_READ', 'View stock levels', 'STOCK', 'READ'),
    ('perm-stock-write', 'STOCK_WRITE', 'Create/Update items', 'STOCK', 'WRITE'),
    ('perm-stock-adjust', 'STOCK_ADJUST', 'Adjust stock quantities', 'STOCK', 'ADJUST'),
    ('perm-stock-delete', 'STOCK_DELETE', 'Delete items', 'STOCK', 'DELETE'),
    
    ('perm-stock-receive', 'STOCK_RECEIVE', 'Receive goods', 'STOCK', 'RECEIVE'),
    ('perm-stock-withdraw', 'STOCK_WITHDRAW', 'Withdraw goods', 'STOCK', 'WITHDRAW'),
    ('perm-stock-reserve', 'STOCK_RESERVE', 'Reserve stock', 'STOCK', 'RESERVE'),
    ('perm-stock-sale', 'STOCK_SALE', 'Process quick sales', 'STOCK', 'SALE'),
    
    ('perm-report-read', 'REPORT_READ', 'View dashboard/reports', 'REPORT', 'READ'),
    ('perm-report-export', 'REPORT_EXPORT', 'Export reports', 'REPORT', 'EXPORT'),
    
    ('perm-user-read', 'USER_READ', 'View users', 'USER', 'READ'),
    ('perm-user-write', 'USER_WRITE', 'Manage users', 'USER', 'WRITE'),
    ('perm-user-delete', 'USER_DELETE', 'Delete users', 'USER', 'DELETE'),
    
    ('perm-audit-read', 'AUDIT_READ', 'View audit logs', 'AUDIT', 'READ');

-- B. Standard Roles
INSERT INTO inventory.roles (id, name, description) VALUES
    ('role-admin', 'ADMIN', 'Super Administrator - Full Access'),
    ('role-manager', 'MANAGER', 'Inventory Manager - Can manage stock but not system users'),
    ('role-staff', 'STAFF', 'Staff - Basic operations (Receive/Withdraw)'),
    ('role-viewer', 'VIEWER', 'Read-only access');

-- C. Map Permissions to Roles
-- ADMIN: Everything
INSERT INTO inventory.role_permissions (role_id, permission_id)
SELECT 'role-admin', id FROM inventory.permissions;

-- MANAGER: All STOCK, REPORT, AUDIT operations (No USER management)
INSERT INTO inventory.role_permissions (role_id, permission_id)
SELECT 'role-manager', id FROM inventory.permissions 
WHERE resource IN ('STOCK', 'REPORT', 'AUDIT');

-- STAFF: STOCK_READ, STOCK_RECEIVE, STOCK_WITHDRAW, STOCK_SALE, REPORT_READ
INSERT INTO inventory.role_permissions (role_id, permission_id)
SELECT 'role-staff', id FROM inventory.permissions 
WHERE name IN ('STOCK_READ', 'STOCK_RECEIVE', 'STOCK_WITHDRAW', 'STOCK_SALE', 'REPORT_READ', 'STOCK_RESERVE');

-- VIEWER: READ only
INSERT INTO inventory.role_permissions (role_id, permission_id)
SELECT 'role-viewer', id FROM inventory.permissions 
WHERE action = 'READ';

-- D. Initial Admin User (Password: admin123)
-- Using a valid BCrypt hash for 'admin123'
INSERT INTO inventory.users (id, username, email, password_hash, first_name, last_name, is_active) VALUES
    ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@example.com', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin', 'User', TRUE)
ON CONFLICT DO NOTHING;

-- Assign ADMIN role to admin user
INSERT INTO inventory.user_roles (user_id, role_id, assigned_by) VALUES
    ('00000000-0000-0000-0000-000000000001', 'role-admin', '00000000-0000-0000-0000-000000000001')
ON CONFLICT DO NOTHING;
