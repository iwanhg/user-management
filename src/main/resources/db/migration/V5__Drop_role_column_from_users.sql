-- Drop the old role column as it's now managed by the user_roles table
ALTER TABLE users DROP COLUMN role;