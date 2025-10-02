-- Add an index on the refresh_token column to speed up lookups by refresh token
-- This migration targets MySQL (the project uses MySQL per application.properties).
-- It creates the index only if it does not already exist.

SET @idx_exists = (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
        AND table_name = 'users'
        AND index_name = 'idx_users_refresh_token'
);

-- Use a prefix index to avoid "Specified key was too long" on older MySQL/MariaDB
-- when using utf8mb4 (4 bytes per char). 191 chars is safe for most setups.
SET @sql = IF(@idx_exists = 0,
    'ALTER TABLE users ADD INDEX idx_users_refresh_token (refresh_token(191));',
    'SELECT 1;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Note: Flyway will run this on MySQL. If you use a different RDBMS in other environments, add a DB-specific migration.