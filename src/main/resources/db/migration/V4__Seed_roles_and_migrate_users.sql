-- Seed the initial roles
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');

-- Migrate existing users to the new user_roles table
-- This script assumes the roles table has been seeded as above
-- where role id 1 = ROLE_USER and 2 = ROLE_ADMIN

-- Assign ROLE_USER to all users who had the 'USER' role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, (SELECT r.id FROM roles r WHERE r.name = 'ROLE_USER')
FROM users u WHERE u.role = 'USER';

-- Assign ROLE_ADMIN to all users who had the 'ADMIN' role
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, (SELECT r.id FROM roles r WHERE r.name = 'ROLE_ADMIN')
FROM users u WHERE u.role = 'ADMIN';