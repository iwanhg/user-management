-- Add new permission for managing roles and permissions
INSERT INTO permissions(name) VALUES('MANAGE_AUTHORIZATION');

-- Assign the new permission to the ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
VALUES (
    (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'),
    (SELECT id FROM permissions WHERE name = 'MANAGE_AUTHORIZATION')
);